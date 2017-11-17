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

    //boolean firstRun = true;
    Map<String, Boolean> firstRunMap = new HashMap<>();

    //List<String> visited = new LinkedList<String>();
    Map<String, List<String>> visitedMap = new HashMap<>();

    JDA jda;
    int initialPostCheck = 4;
    int postCheck = 2;

    public RedditService (JDA jda){
        this.jda = jda;

    }

    public void initialise(){
        ConfigReader configReader = new ConfigReader();
        Map<String, List<Long>> redditChannelMap = configReader.getRedditMap();
        for (Map.Entry<String, List<Long>> entry : redditChannelMap.entrySet()) {
            String subreddit = entry.getKey();
            firstRunMap.put(subreddit, true);
            visitedMap.put(subreddit, new LinkedList<>());
        }
    }

    public void run(){
        ConfigReader configReader = new ConfigReader();
        Map<String, List<Long>> redditChannelMap = configReader.getRedditMap();
        for (Map.Entry<String, List<Long>> entry : redditChannelMap.entrySet()) {
            String subreddit = entry.getKey();
            if(!firstRunMap.containsKey(subreddit)){
                initialise();
            }

            boolean firstRun = firstRunMap.get(subreddit);

            List<String> visited = visitedMap.get(subreddit);

            if (firstRun == true) {
                List<Submission> posts = getPosts(subreddit, initialPostCheck);
                for (int i = 0; i < initialPostCheck; i++) {
                    visitedMap.get(subreddit).add(posts.get(i).getId());
                }
                firstRunMap.put(subreddit, false);
            }

            Map<String, List<Submission>> newPostsMap = new HashMap<>();
            List<Submission> newPosts = new LinkedList<Submission>();
            if(newPostsMap.get(subreddit) != null) {
                newPosts.addAll(newPostsMap.get(subreddit));
            }

            List<Submission> posts = getPosts(subreddit, postCheck);
            for (int i = 0; i < postCheck; i++) {
                Submission submission = posts.get(i);

                if (!visited.contains(submission.getId())) {
                    newPosts.add(submission);

                    List<Submission> list = new LinkedList<>();
                    list.addAll(newPosts);
                    newPostsMap.put(subreddit, list);

                    visited.add(submission.getId());

                    List<String> list2 = new LinkedList<>();
                    list2.addAll(visited);
                    visitedMap.put(subreddit, list2);
                }
            }

            if (!newPosts.isEmpty()) {
                for (int i = 0; i < newPosts.size(); i++) {
                    Submission submission = newPosts.get(i);

                    List<Long> redditChannelIDs = redditChannelMap.get(subreddit);
                    for (int j = 0; j < redditChannelIDs.size(); j++) {
                        Long channelID = redditChannelIDs.get(j);
                        EmbedBuilder embedBuilder = new EmbedBuilder();
                        if (submission.isSelfPost()) {
                            int upperBound = min(submission.getSelftext().length(), 800);
                            embedBuilder.setTitle("New post in /r/" + submission.getSubredditName() + " by " + submission.getAuthor());
                            embedBuilder.setDescription("[" + submission.getTitle() + "](" + submission.getShortURL() + ")");
                            embedBuilder.setColor(new Color(51, 153, 255));
                            embedBuilder.addField("Post Text: ", submission.getSelftext().substring(0, upperBound), false);
                            embedBuilder.addField("Reddit Post: ", submission.getShortURL(), false);
                            embedBuilder.setThumbnail(submission.getThumbnail());
                        } else {
                            embedBuilder.setTitle("New post in /r/" + submission.getSubredditName() + " by " + submission.getAuthor());
                            embedBuilder.setDescription("[" + submission.getTitle() + "](" + submission.getUrl() + ")");
                            embedBuilder.setColor(new Color(51, 153, 255));
                            embedBuilder.addField("Reddit Post: ", submission.getShortURL(), false);
                            embedBuilder.setThumbnail(submission.getThumbnail());
                        }
                        jda.getTextChannelById(channelID).sendMessage(embedBuilder.build()).queue();
                    }
                }
            }
        }
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
