package MessageHandler;

import Config.ConfigReader;
import Config.ConfigReddit;
import net.dean.jraw.models.Submission;
import net.dv8tion.jda.core.JDA;

import java.util.List;
import java.util.Map;

import static java.lang.StrictMath.min;

public class MessageConstructorReddit {
    private JDA jda;
    private ConfigReddit configReddit;
    private ConfigReader configReader;
    MessageConstructor constructor ;

    public MessageConstructorReddit(JDA jda){
        this.jda = jda;
        configReddit = new ConfigReddit();
        configReader = new ConfigReader();
        constructor = new MessageConstructor(jda);
    }

    public void sendRedditMessages(Submission submission, List<Long> channelIDs){

        constructor.setTitle("New posts in /r/" + submission.getSubreddit() + " by " + submission.getAuthor());
        constructor.appendDescription("[" + submission.getTitle() + "](" + submission.getUrl() + ")");

        if(submission.isSelfPost()){
            int charLimit = min(submission.getSelfText().length(), 800);
            constructor.addField("Post Text", submission.getSelfText().substring(0, charLimit));
        }

        constructor.addField("Reddit Posts: ", "https://www.reddit.com" + submission.getPermalink());

        for(int i = 0; i < channelIDs.size(); i++) {
            constructor.sendMessageNoReset(channelIDs.get(i));
            System.out.println("NEW REDDIT POST by " + submission.getAuthor() + " CHANNEL: " + channelIDs.get(i));
        }
    }

    public void sendFollowedRedditMessage(Long channelID){
        constructor.setAuthor("Followed Subreddits");
        Boolean empty = true;
        for(Map.Entry<String, List<Long>> entry : configReader.getRedditMap().entrySet()){
            String subreddit = entry.getKey();
            List<Long> channelIDs = entry.getValue();
            if(channelIDs.contains(channelID)){
                constructor.addField(configReddit.getRedditTitle(subreddit), "/r/" + subreddit);
                empty = false;
            }
        }

        if(empty){
            constructor.appendDescription("No subreddits followed in this channel");
        }

        constructor.sendMessage(channelID);
    }
}
