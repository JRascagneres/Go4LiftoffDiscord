package uk.co.rascagneres.spacexbot.Config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ConfigReader {
    Config config = null;

    public ConfigReader(){
        try {
            config = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new FileReader("config.json"), Config.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, List<Long>> getRedditMap(){
        return config.redditChannelIDs;
    }

    public Map<String, List<Long>> getTwitterMap(){
        return config.twitterChannelIDs;
    }

    public String getToken(){
        return config.token;
    }

    public String getPrefix(){
        return config.prefix;
    }

    public Map<String, String> getRedditData(){
        return config.redditData;
    }

    public Map<String, String> getTwitterData(){
        return config.twitterData;
    }

    public void addRedditChannelID(String subRedditName, Long channelID){
        List<Long> list = new LinkedList<>();
        if (config.redditChannelIDs.get(subRedditName) != null) {
            list.addAll(config.redditChannelIDs.get(subRedditName));
        }
        list.add(channelID);
        config.redditChannelIDs.put(subRedditName, list);
        saveJSONFile();
    }

    public void removeRedditChannelID(String subRedditName, Long channelID){
        config.redditChannelIDs.get(subRedditName).remove(channelID);
        if (config.redditChannelIDs.get(subRedditName).isEmpty()){
            config.redditChannelIDs.remove(subRedditName);
        }
        saveJSONFile();
    }

    public void addTwitter(String twitterUser, Long channelID){
        List<Long> list = new LinkedList<>();
        if (config.twitterChannelIDs.get(twitterUser) != null){
            list.addAll(config.twitterChannelIDs.get(twitterUser));
        }
        list.add(channelID);
        config.twitterChannelIDs.put(twitterUser, list);
        saveJSONFile();
    }

    public void removeTwitter(String twitterUser, Long channelID){
        config.twitterChannelIDs.get(twitterUser).remove(channelID);
        if (config.twitterChannelIDs.get(twitterUser).isEmpty()){
            config.twitterChannelIDs.remove(twitterUser);
        }
        saveJSONFile();

    }

    public List<Long> getCountdownChannels(){
        return config.countdownChannelIDs;
    }

    public void addCountdownChannel(Long channelID){
        config.countdownChannelIDs.add(channelID);
        saveJSONFile();
    }

    public void removeCountdownChannel(Long channelID){
        config.countdownChannelIDs.remove(channelID);
        saveJSONFile();
    }

    public void saveJSONFile(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File("config.json"), config);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
