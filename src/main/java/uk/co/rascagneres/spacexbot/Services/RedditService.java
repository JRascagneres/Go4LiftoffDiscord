package uk.co.rascagneres.spacexbot.Services;

import net.dean.jraw.models.Submission;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import uk.co.rascagneres.spacexbot.Config.ConfigReader;
import uk.co.rascagneres.spacexbot.Utilities.Utils;

import java.awt.*;
import java.util.*;
import java.util.List;

import static java.lang.Math.min;

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
                submissions = Utils.getRedditPosts(entry.getKey(), 5);
            }catch(Exception e){
                e.printStackTrace();
                System.out.println("Get posts failed!");
            }

            if (firstRunMap.get(entry.getKey()) == true){
                for (int i = 1; i < submissions.size(); i++) {
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
        System.out.println("SUBMISSION: " + submission.getTitle() + " IN " + submission.getSubredditFullName() +   " CHANNEL: " + channelID);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("New post in /r/" + submission.getSubredditFullName() + " by " + submission.getAuthor());
        embedBuilder.setColor(new Color(51, 153, 255));
        if (!submission.getThumbnail().equals("self")){
            embedBuilder.setThumbnail(submission.getThumbnail());
        }
        if(submission.isSelfPost()){
            int upperBound = min(submission.getSelfText().length(), 800);
            embedBuilder.setDescription("[" + submission.getTitle() + "](" + submission.getUrl() + ")");
            embedBuilder.addField("Post Text: ", submission.getSelfText().substring(0, upperBound), false);
            embedBuilder.addField("Reddit Post: ", submission.getUrl(), false);
        }else{
            embedBuilder.setDescription("[" + submission.getTitle() + "](" + submission.getUrl() + ")");
            embedBuilder.addField("Reddit Post: ", submission.getUrl(), false);
        }

        jda.getTextChannelById(channelID).sendMessage(embedBuilder.build()).queue();
    }


}
