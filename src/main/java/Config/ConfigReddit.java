package Config;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.DefaultPaginator;

import java.util.List;
import java.util.Map;

public class ConfigReddit {
    ConfigReader configReader = new ConfigReader();

    public boolean addReddit(String subreddit, Long channelID){
        subreddit = subreddit.toLowerCase();
        if(checkSubredditExists(subreddit)){
            if(!(configReader.getRedditMap().containsKey(subreddit) && configReader.getRedditMap().get(subreddit).contains(channelID))){
                configReader.addRedditChannelID(subreddit, channelID);
                return true;
            }
        }
        return false;
    }

    public boolean removeReddit(String subreddit, Long channelID){
        subreddit = subreddit.toLowerCase();
        if(configReader.getRedditMap().containsKey(subreddit) && configReader.getRedditMap().get(subreddit).contains(channelID)){
            configReader.removeRedditChannelID(subreddit, channelID);
            return true;
        }
        return false;
    }

    public String getRedditTitle(String subreddit){
        return connectReddit().subreddit(subreddit).about().getTitle();
    }

    public List<Submission> getRedditPosts(String subreddit, int numberOfPosts){
        DefaultPaginator<Submission> paginator = connectReddit().subreddit(subreddit).posts()
                .limit(numberOfPosts)
                .sorting(SubredditSort.NEW)
                .build();
        return paginator.next();
    }

    public boolean checkSubredditExists(String subreddit){
        RedditClient redditClient = connectReddit();
        try{
            redditClient.subreddit(subreddit);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public RedditClient connectReddit(){
        UserAgent userAgent = new UserAgent("desktop", "uk.co.rascagneres.goforlaunch", "v1.0", "Scorp1579");
        NetworkAdapter networkAdapter = new OkHttpNetworkAdapter(userAgent);
        Map<String, String> redditData = new ConfigReader().getRedditData();
        Credentials credentials = Credentials.script(redditData.get("username"), redditData.get("password"), redditData.get("clientid"), redditData.get("clientsecret"));
        RedditClient redditClient = OAuthHelper.automatic(networkAdapter, credentials);
        redditClient.setLogHttp(false);
        return redditClient;
    }
}
