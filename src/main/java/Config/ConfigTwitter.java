package Config;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.LinkedList;
import java.util.List;

public class ConfigTwitter {
    ConfigReader configReader = new ConfigReader();

    public boolean addTwitterUser(String twitterUser, Long channelID){
        twitterUser = twitterUser.toLowerCase();
        if(checkTwitterExists(twitterUser)){
            if(!(configReader.getTwitterMap().containsKey(twitterUser) && configReader.getTwitterMap().get(twitterUser).contains(channelID))){
                configReader.addTwitterChannelID(twitterUser, channelID);
                return true;
            }
        }
        return false;
    }

    public boolean removeTwitterUser(String twitterUser, Long channelID){
        twitterUser = twitterUser.toLowerCase();
        if(configReader.getTwitterMap().containsKey(twitterUser) && configReader.getTwitterMap().get(twitterUser).contains(channelID)){
            configReader.removeTwitterChannelID(twitterUser, channelID);
            return true;
        }
        return false;
    }

    public List<Status> getMainTweetsOnly(String user){
        List<Status> allTweets = getTweets(user);
        List<Status> mainTweets = new LinkedList<>();
        for(int i = 0; i < allTweets.size(); i++){
            Status tweet = allTweets.get(i);
            if(!tweet.isRetweet() && tweet.getInReplyToStatusId() == -1){
                mainTweets.add(tweet);
            }
        }
        return mainTweets;
    }

    public List<Status> getTweets(String user){
        Twitter twitter = connectTwitter();
        List<Status> statusList = new LinkedList<>();
        try {
            statusList = twitter.getUserTimeline(user);
        }catch (Exception e){
            //System.out.println("FAILED TO GET TWEET");
        }

        return statusList;
    }

    public String getTwitterName(String twitterUser){
        Twitter twitter = connectTwitter();
        try{
            return twitter.users().showUser(twitterUser).getName();
        }catch (Exception e){
            return null;
        }
    }

    public boolean checkTwitterExists(String twitterUser){
        Twitter twitter = connectTwitter();
        try{
            twitter.showUser(twitterUser);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public Twitter connectTwitter(){
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
        return twitter;
    }
}
