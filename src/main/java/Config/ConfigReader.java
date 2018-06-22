package Config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileReader;
import java.util.*;

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

    public Map<Integer, List<Long>> getCustomNotificationMap(){
        return config.countdownCustomIDs;
    }

    public Map<String, List<Long>> getUserNoficationMap(){
        if(config.userNotifications == null){
            config.userNotifications = new HashMap<String, List<Long>>();
        }
        return config.userNotifications;
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
        if (getUserNoficationMap().get(agency) != null) {
            getUserNoficationMap().get(agency).add(userID);
        } else {
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

    public boolean addCustomNotifications(Long channelID, Integer minutes){
        if(getCustomNotificationMap().containsKey(minutes) && getCustomNotificationMap().get(minutes).contains(channelID)){
            return false;
        }

        if(getCustomNotificationMap().get(minutes) != null){
            getCustomNotificationMap().get(minutes).add(channelID);
        }else{
            getCustomNotificationMap().put(minutes, Collections.singletonList(channelID));
        }
        saveJSONFile();
        return true;
    }

    public boolean removeCustomNotifications(Long channelID, Integer minutes){
        if(!getCustomNotificationMap().containsKey(minutes) && !getCustomNotificationMap().get(minutes).contains(channelID)){
            return false;
        }

        getCustomNotificationMap().get(minutes).remove(channelID);
        if(getCustomNotificationMap().get(minutes).isEmpty()){
            getCustomNotificationMap().remove(minutes);
        }
        saveJSONFile();
        return true;
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
