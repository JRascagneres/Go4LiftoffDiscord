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

    public String getToken(){
        return config.token;
    }

    public String getPrefix(){
        return config.prefix;
    }

    public Map<String, String> getRedditData(){
        return config.redditData;
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

    public void removeRedditChannelID(Long channelID){
        config.redditChannelIDs.remove(channelID);
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
