package Config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ConfigObject {

    //Discord bot information
    public String token;
    public String prefix;

    //Reddit information    --> subreddit-DiscordChannel
    //                      --> reddit login info, key-value
    public Map<String, List<Long>> redditChannelIDs = new HashMap<>();
    public Map<String, String> redditData = new HashMap<>();

    //Twitter information   --> twitterUser-DiscordChannel
    //                      --> twitter login info, key-value
    public Map<String, List<Long>> twitterChannelIDs = new HashMap<>();
    public Map<String, String> twitterData = new HashMap<>();

    //Countdown channels    --> Channel list
    public List<Long> countdownChannelIDs = new LinkedList<>();
}
