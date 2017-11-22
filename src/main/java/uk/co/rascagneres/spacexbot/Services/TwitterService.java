package uk.co.rascagneres.spacexbot.Services;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import uk.co.rascagneres.spacexbot.Config.ConfigReader;

import java.awt.*;
import java.util.*;
import java.util.List;

public class TwitterService extends TimerTask{

    Map<String, Boolean> firstRunMap = new HashMap<>();

    Map<String, List<Long>> checkedMap = new HashMap<>();

    JDA jda;
    int initialTweetCheck = 4;
    int tweetCheck = 2;

    public TwitterService (JDA jda){
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

    public void run(){
        ConfigReader configReader = new ConfigReader();
        Map<String, List<Long>> twitterMap = configReader.getTwitterMap();

        for (Map.Entry<String, List<Long>> entry : twitterMap.entrySet()) {
            String twitterUser = entry.getKey();
            if(!firstRunMap.containsKey(twitterUser)){
                initialise();
            }

            List<Long> channelIDs = entry.getValue();

            if (firstRunMap.get(twitterUser) == true){
                List<Status> tweets = getMainTweetsOnly(twitterUser);
                for (int i = 0; i < initialTweetCheck; i++){
                    List<Long> checked = checkedMap.get(twitterUser);
                    checked.add(tweets.get(i).getId());
                    checkedMap.put(twitterUser, checked);
                }
                firstRunMap.put(twitterUser, true);
            }

            Map<String, List<Status>> newTweetsMap = new HashMap<>();


            List<Status> tweets = getMainTweetsOnly(twitterUser);
            for (int i = 0; i < tweetCheck; i++){
                Status tweet = tweets.get(i);
                if(!checkedMap.get(twitterUser).contains(tweet.getId())){
                    if(newTweetsMap.get(twitterUser) != null) {
                        newTweetsMap.get(twitterUser).add(tweet);
                    }else{
                        List<Status> list = new LinkedList<>();
                        list.add(tweet);
                        newTweetsMap.put(twitterUser, list);
                    }


                    checkedMap.get(twitterUser).add(tweet.getId());
                }
            }

            if (newTweetsMap.get(twitterUser) != null && !newTweetsMap.get(twitterUser).isEmpty()){
                for (int i = 0; i < newTweetsMap.get(twitterUser).size(); i++){
                    Status tweet = newTweetsMap.get(twitterUser).get(i);
                    for(int j = 0; j < channelIDs.size(); j++){
                        Long channelID = channelIDs.get(j);
                        EmbedBuilder embedBuilder = new EmbedBuilder();
                        embedBuilder.setTitle("New Tweet by " + tweet.getUser().getScreenName());
                        embedBuilder.setDescription(tweet.getText());

                        embedBuilder.addField("Tweet Link:", ("https://twitter.com/" + tweet.getUser().getScreenName() + "/status/" + tweet.getId()), false);

                        embedBuilder.setThumbnail(tweet.getUser().getProfileImageURL());
                        embedBuilder.setColor(new Color(51, 153, 255));

                        jda.getTextChannelById(channelID).sendMessage(embedBuilder.build()).queue();
                    }
                }
            }

        }
    }

    public List<Status> getMainTweetsOnly(String user){
        List<Status> allTweets = getTweets(user);
        List<Status> mainTweets = new LinkedList<>();
        for(int i = 0; i < allTweets.size(); i++){
            Status currentTweet = allTweets.get(i);
            if(!currentTweet.isRetweet() && currentTweet.getInReplyToStatusId() == -1){
                mainTweets.add(currentTweet);
            }
        }
        return mainTweets;
    }

    public List<Status> getTweets(String user){
        ConfigReader configReader = new ConfigReader();
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder
                .setDebugEnabled(true)
                .setOAuthConsumerKey(configReader.getTwitterData().get("consumerkey"))
                .setOAuthConsumerSecret(configReader.getTwitterData().get("consumersecret"))
                .setOAuthAccessToken(configReader.getTwitterData().get("accesstoken"))
                .setOAuthAccessTokenSecret(configReader.getTwitterData().get("accesstokensecret"))
                .setTweetModeExtended(true);

        TwitterFactory twitterFactory = new TwitterFactory(configurationBuilder.build());
        Twitter twitter = twitterFactory.getInstance();
        List<Status> statusList = new LinkedList<>();
        try {
            statusList = twitter.getUserTimeline(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusList;
    }
}
