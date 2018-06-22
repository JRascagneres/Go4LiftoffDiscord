package Services;

import Config.ConfigReader;
import Config.ConfigTwitter;
import MessageHandler.MessageConstructorTwitter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import twitter4j.Status;

import java.awt.*;
import java.util.*;
import java.util.List;

import static jdk.nashorn.internal.objects.NativeMath.min;

public class ServiceTwitter extends TimerTask {

    Map<String, Boolean> firstRunMap = new HashMap<>();

    Map<String, List<Long>> checkedMap = new HashMap<>();
    Map<String, List<Status>> newTweetsMap = new HashMap<>();

    JDA jda;
    int initialTweetCheck = 5;
    int tweetCheck = 2;

    public ServiceTwitter (JDA jda){
        this.jda = jda;
        initialise();
    }

    public void initialise(){
        ConfigReader configReader = new ConfigReader();
        Map<String, List<Long>> twitterMap = configReader.getTwitterMap();
        for(Map.Entry<String, List<Long>> entry : twitterMap.entrySet()){
            String username = entry.getKey();
            firstRunMap.put(username, true);
            checkedMap.put(username, new LinkedList<>());
        }
    }

    public void run() {
        ConfigReader configReader = new ConfigReader();
        Map<String, List<Long>> twitterMap = configReader.getTwitterMap();


        for (Map.Entry<String, List<Long>> entry : twitterMap.entrySet()) {
            ConfigTwitter configTwitter = new ConfigTwitter();

            String twitterUser = entry.getKey();
            if (!firstRunMap.containsKey(twitterUser)) {
                initialise();
            }

            List<Status> tweets = configTwitter.getMainTweetsOnly(twitterUser);

            if (firstRunMap.get(twitterUser) == true) {

                for (int i = 0; i < min(initialTweetCheck, tweets.size()); i++) {
                    List<Long> checked = checkedMap.get(twitterUser);
                    checked.add(tweets.get(i).getId());
                    checkedMap.put(twitterUser, checked);
                }
                firstRunMap.put(twitterUser, false);
            }

            for (int i = 0; i < min(tweetCheck, tweets.size()); i++) {
                Status tweet = tweets.get(i);
                if (!checkedMap.get(twitterUser).contains(tweet.getId())) {
                    if (newTweetsMap.get(twitterUser) != null) {
                        newTweetsMap.get(twitterUser).add(tweet);
                    } else {
                        List<Status> list = new LinkedList<>();
                        list.add(tweet);
                        newTweetsMap.put(twitterUser, list);
                    }


                    checkedMap.get(twitterUser).add(tweet.getId());
                }
            }
        }

        if(!newTweetsMap.isEmpty()){
            Iterator<Map.Entry<String, List<Status>>> iterator = newTweetsMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String, List<Status>> tweetSet = iterator.next();
                String twitterUser = tweetSet.getKey();
                List<Status> tweets = tweetSet.getValue();
                List<Long> channelIDs = twitterMap.get(twitterUser);
                for (int i = 0; i < tweets.size(); i++){
                    if(checkedMap.containsKey(twitterUser) && checkedMap.get(twitterUser).contains(tweets.get(i).getId())){
                        if(checkedMap.get(twitterUser).size() > initialTweetCheck && checkedMap.get(twitterUser).get(i) != null) {
                            MessageConstructorTwitter messageConstructorTwitter = new MessageConstructorTwitter(jda);
                            messageConstructorTwitter.sendTweetMessages(tweets.get(i), channelIDs);
                        }
                    }
                }
                iterator.remove();
            }
        }
    }
}
