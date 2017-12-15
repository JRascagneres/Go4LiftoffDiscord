package uk.co.rascagneres.spacexbot.Services;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.Sorting;
import net.dean.jraw.paginators.SubredditPaginator;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import uk.co.rascagneres.spacexbot.Config.Config;
import uk.co.rascagneres.spacexbot.Config.ConfigReader;

import java.awt.*;
import java.util.*;
import java.util.List;

import static com.google.common.primitives.Ints.min;

public class RedditService extends TimerTask {

    JDA jda;

    Map<String, List<String>> checkedMap = new HashMap<>();
    Map<String, Boolean> firstRunMap = new HashMap<>();
    Map<String, List<Submission>> newRedditPosts = new HashMap<>();

    public RedditService (JDA jda){
        this.jda = jda;
        initialise();

    }

    public void initialise(){
       ConfigReader configReader = new ConfigReader();
       Map<String, List<Long>> redditChannelMap = configReader.getRedditMap();
       for(Map.Entry<String, List<Long>> entry : redditChannelMap.entrySet()){
            String subredditName = entry.getKey();
            firstRunMap.put(subredditName, true);
            checkedMap.put(subredditName, new LinkedList<>());
       }
    }

    public void run(){
        ConfigReader configReader = new ConfigReader();
        Map<String, List<Long>> redditChannelMap = configReader.getRedditMap();

        for (Map.Entry<String, List<Long>> entry : redditChannelMap.entrySet()){

            if(!firstRunMap.containsKey(entry.getKey())){
                initialise();
            }

            List<Submission> submissions = new LinkedList<>();

            try {
                submissions = getPosts(entry.getKey(), 5);
            }catch(Exception e){
                e.printStackTrace();
                System.out.println("Get posts failed!");
            }

            if (firstRunMap.get(entry.getKey()) == true){
                for (int i = 0; i < submissions.size(); i++) {
                    List<String> checkedList = checkedMap.get(entry.getKey());
                    checkedList.add(submissions.get(i).getId());
                    checkedMap.put(entry.getKey(), checkedList);
                }
                firstRunMap.put(entry.getKey(), false);
            }

            for (int i = 0; i < submissions.size(); i++){
                Submission post = submissions.get(i);
                if(!checkedMap.get(entry.getKey()).contains(submissions.get(i).getId())){
                    if(newRedditPosts.get(entry.getKey()) != null){
                        newRedditPosts.get(entry.getKey()).add(post);
                    }else{
                        List<Submission> list = new LinkedList<>();
                        list.add(post);
                        newRedditPosts.put(entry.getKey(), list);
                    }
                    checkedMap.get(entry.getKey()).add(post.getId());
                }
            }

        }

        if(!newRedditPosts.isEmpty()){
            Iterator<Map.Entry<String, List<Submission>>> iterator = newRedditPosts.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String, List<Submission>> submissionSet = iterator.next();
                String subreddit = submissionSet.getKey();
                List<Submission> submissionList = submissionSet.getValue();
                List<Long> channelIDs = redditChannelMap.get(subreddit);
                for (int i = 0; i < submissionList.size(); i++){
                    for (int j = 0; j < channelIDs.size(); j++){
                        sendMessage(submissionList.get(i), channelIDs.get(j));
                    }
                }
                iterator.remove();
            }
        }
    }

    public void sendMessage(Submission submission, Long channelID){
        System.out.println("SUBMISSION: " + submission.getTitle() + " IN " + submission.getSubredditName() +   " CHANNEL: " + channelID);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("New post in /r/" + submission.getSubredditName() + " by " + submission.getAuthor());
        embedBuilder.setColor(new Color(51, 153, 255));
        embedBuilder.setThumbnail(submission.getThumbnail());
        if(submission.isSelfPost()){
            int upperBound = min(submission.getSelftext().length(), 800);
            embedBuilder.setDescription("[" + submission.getTitle() + "](" + submission.getShortURL() + ")");
            embedBuilder.addField("Post Text: ", submission.getSelftext().substring(0, upperBound), false);
            embedBuilder.addField("Reddit Post: ", submission.getShortURL(), false);
        }else{
            embedBuilder.setDescription("[" + submission.getTitle() + "](" + submission.getUrl() + ")");
            embedBuilder.addField("Reddit Post: ", submission.getShortURL(), false);
        }

        jda.getTextChannelById(channelID).sendMessage(embedBuilder.build()).queue();
    }

    public List<Submission> getPosts(String subreddit, int numberOfPosts) {
        UserAgent userAgent = UserAgent.of("desktop", "uk.co.rascagneres.spacexbot", "v1.0", "Scorp1579");
        RedditClient redditClient = new RedditClient(userAgent);
        Map<String, String> redditData = new ConfigReader().getRedditData();
        Credentials credentials = Credentials.script(redditData.get("username"), redditData.get("password"), redditData.get("clientid"), redditData.get("clientsecret"));
        OAuthData authData = null;
        try {
            authData = redditClient.getOAuthHelper().easyAuth(credentials);

        }catch(Exception e){
            e.printStackTrace();
        }
        redditClient.authenticate(authData);
        SubredditPaginator subredditPaginator = new SubredditPaginator(redditClient);
        subredditPaginator.setLimit(numberOfPosts);
        subredditPaginator.setSorting(Sorting.NEW);
        subredditPaginator.setSubreddit(subreddit);
        subredditPaginator.next();
        List<Submission> list = subredditPaginator.getCurrentListing();
        return list;
    }
}
