package MessageHandler;

import Config.ConfigReader;
import Config.ConfigTwitter;
import net.dv8tion.jda.core.JDA;
import twitter4j.Status;
import twitter4j.TweetEntity;

import java.util.List;
import java.util.Map;

public class MessageConstructorTwitter {
    private JDA jda;
    private ConfigTwitter configTwitter;
    private ConfigReader configReader;
    MessageConstructor constructor ;

    public MessageConstructorTwitter(JDA jda){
        this.jda = jda;
        configTwitter = new ConfigTwitter();
        configReader = new ConfigReader();
        constructor = new MessageConstructor(jda);
    }

    public void sendTweetMessages(Status tweet, List<Long> channelIDs){
        if(tweet != null && channelIDs != null) {
            constructor.setTitle("New Tweet by " + tweet.getUser().getScreenName());
            constructor.appendDescription(tweet.getText());

            constructor.addField("Tweet Link:", "https://twitter.com/" + tweet.getUser().getScreenName() + "/status/" + tweet.getId());

            constructor.setThumbnailURL(tweet.getUser().getProfileImageURL());

            for (int i = 0; i < channelIDs.size(); i++) {
                constructor.sendMessageNoReset(channelIDs.get(i));
                System.out.println("NEW TWEET by " + tweet.getUser().getScreenName() + " CHANNEL: " + channelIDs.get(i));
            }
        }
    }

    public void sendFollowedTwitterMessage(Long channelID) {
        constructor.setAuthor("Followed Twitter Accounts");
        Boolean empty = true;
        for(Map.Entry<String, List<Long>> entry : configReader.getTwitterMap().entrySet()) {
            String twitterUser = entry.getKey();
            List<Long> channelIDs = entry.getValue();
            if(channelIDs.contains(channelID)){
                constructor.addField(configTwitter.getTwitterName(twitterUser), "@" + twitterUser);
                empty = false;
            }
        }

        if(empty){
            constructor.appendDescription("No twitter accounts followed in this channel");
        }

        constructor.sendMessage(channelID);
    }
}
