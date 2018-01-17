package Config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.core.entities.User;

import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ConfigReader {
    ConfigObject config = null;

    public ConfigReader(){
        try{
            config = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new FileReader("config.json"), ConfigObject.class);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("FAILED TO READ CONFIG");
        }
    }

    public String getToken(){
        return config.token;
    }

    public String getPrefix(){
        return config.prefix;
    }

    public Map<String, List<Long>> getRedditMap(){
        return config.redditChannelIDs;
    }

    public Map<String, String> getRedditData(){
        return config.redditData;
    }

    public Map<String, List<Long>> getTwitterMap(){
        return config.twitterChannelIDs;
    }

    public Map<String, String> getTwitterData(){
        return config.twitterData;
    }

    public List<Long> getCountdownChannels(){
        return config.countdownChannelIDs;
    }

    public Map<String, List<Long>> getUserNoficationMap(){
        return config.userNoifications;
    }

    public void addRedditChannelID(String subreddit, Long channelID){
        if(getRedditMap().get(subreddit) != null){
            getRedditMap().get(subreddit).add(channelID);
        }else{
            List<Long> list = new LinkedList<>();
            list.add(channelID);
            getRedditMap().put(subreddit, list);
        }
        saveJSONFile();
    }

    public void removeRedditChannelID(String subreddit, Long channelID){
        getRedditMap().get(subreddit).remove(channelID);
        if(getRedditMap().get(subreddit).isEmpty()){
            getRedditMap().remove(subreddit);
        }
        saveJSONFile();
    }

    public void addTwitterChannelID(String twitterUser, Long channelID){
        if(getTwitterMap().get(twitterUser) != null){
            getTwitterMap().get(twitterUser).add(channelID);
        }else{
            List<Long> list = new LinkedList<>();
            list.add(channelID);
            getTwitterMap().put(twitterUser, list);
        }
        saveJSONFile();
    }

    public void removeTwitterChannelID(String twitterUser, Long channelID){
        getTwitterMap().get(twitterUser).remove(channelID);
        if(getTwitterMap().get(twitterUser).isEmpty()){
            getTwitterMap().remove(twitterUser);
        }
        saveJSONFile();
    }

    public boolean addCountdownChannelID(Long channelID){
        if(getCountdownChannels().contains(channelID)){
            return false;
        }
        config.countdownChannelIDs.add(channelID);
        saveJSONFile();
        return true;
    }

    public boolean removeCoundownChannelID(Long channelID){
        if(!getCountdownChannels().contains(channelID)){
            return false;
        }
        config.countdownChannelIDs.remove(channelID);
        saveJSONFile();
        return true;
    }

    public void addUserNotifications(String agency, Long userID){
        if(getUserNoficationMap().get(agency) != null){
            getUserNoficationMap().get(agency).add(userID);
        }else{
            getUserNoficationMap().put(agency, Collections.singletonList(userID));
        }
        saveJSONFile();
    }

    public void removeUserNotifications(String agency, Long userID){
        getUserNoficationMap().get(agency).remove(userID);
        if(getUserNoficationMap().get(agency).isEmpty()){
            getUserNoficationMap().remove(agency);
        }
        saveJSONFile();
    }

    public void saveJSONFile(){
        ObjectMapper mapper = new ObjectMapper();
        try{
            mapper.writeValue(new File("config.json"), config);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("JSON SAVED");
        }
    }

}
