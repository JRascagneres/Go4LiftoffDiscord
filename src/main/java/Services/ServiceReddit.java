package Services;

import Config.ConfigReader;
import Config.ConfigReddit;
import MessageHandler.MessageConstructorReddit;
import net.dean.jraw.models.Submission;
import net.dv8tion.jda.core.JDA;

import java.util.*;

public class ServiceReddit extends TimerTask {

    JDA jda;

    Map<String, List<String>> checkedMap = new HashMap<>();
    Map<String, Boolean> firstRunMap = new HashMap<>();
    Map<String, List<Submission>> newRedditPosts = new HashMap<>();

    public ServiceReddit (JDA jda){
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
                ConfigReddit configReddit = new ConfigReddit();
                submissions = configReddit.getRedditPosts(entry.getKey(), 5);
            }catch(Exception e){
                System.out.println("GET POSTS FAILED");
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
                    MessageConstructorReddit messageConstructorReddit = new MessageConstructorReddit(jda);
                    messageConstructorReddit.sendRedditMessages(submissionList.get(i), channelIDs);
                }
                iterator.remove();
            }
        }
    }
}
