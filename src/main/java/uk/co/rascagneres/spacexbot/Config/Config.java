package uk.co.rascagneres.spacexbot.Config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by jacqu on 10/11/2017.
 */
public class Config {
    public String token;
    public String prefix ;
    public Map<String, List<Long>> redditChannelIDs = new HashMap<>();
    public Map<String, String> redditData = new HashMap<>();
    public Map<String, List<Long>> twitterChannelIDs = new HashMap<>();
    public Map<String, String> twitterData = new HashMap<>();
    public List<Long> countdownChannelIDs = new LinkedList<>();
}
