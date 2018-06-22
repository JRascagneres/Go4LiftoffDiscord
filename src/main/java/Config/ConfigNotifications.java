package Config;

import net.dv8tion.jda.core.entities.User;

import java.util.*;
import java.util.stream.Collectors;


public class ConfigNotifications {

    public final static Map<String, Integer> agencyLSP;
    static
    {
        agencyLSP = new HashMap<String, Integer>();
        agencyLSP.put("spacex", 121);
        agencyLSP.put("ula", 124);
        agencyLSP.put("arianespace", 115);
        agencyLSP.put("roscosmos", 63);
        agencyLSP.put("rocketlab", 147);
        agencyLSP.put("all", -1);
    }

    ConfigReader configReader;

    public Map<Integer, String> invert(Map<String, Integer> map){
        return map.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getValue, c -> c.getKey()));
    }

    public boolean addUserNotifications(String agency, User user){
        configReader = new ConfigReader();
        agency = agency.toLowerCase();
        if(agencyLSP.containsKey(agency) || agency.equals("all")) {
            String agencyIDStr = agencyLSP.get(agency).toString();
            if(!(configReader.getUserNoficationMap().containsKey(agencyIDStr) && configReader.getUserNoficationMap().get(agencyIDStr).contains(user.getIdLong()))){
                configReader.addUserNotifications(agencyIDStr, user.getIdLong());
                return true;
            }
        }
        return false;
    }

    public boolean removeUserNotifications(String agency, User user){
        configReader = new ConfigReader();
        agency = agency.toLowerCase();
        if (agencyLSP.containsKey(agency) || agency.equals("all")) {
            String agencyIDStr = agencyLSP.get(agency).toString();
            if(configReader.getUserNoficationMap().containsKey(agencyIDStr) && configReader.getUserNoficationMap().get(agencyIDStr).contains(user.getIdLong())){
                configReader.removeUserNotifications(agencyIDStr, user.getIdLong());
                return true;
            }
        }
        return false;
    }

    public String getUserNotification(User user){
        configReader = new ConfigReader();
        String agencyList = "";
        Iterator iterator = configReader.getUserNoficationMap().entrySet().iterator();
        Boolean found = false;
        while(iterator.hasNext()){
            Map.Entry<String, List<Integer>> pair = (Map.Entry) iterator.next();
            if(pair.getValue().contains(user.getIdLong())){
                String agencyStr = invert(agencyLSP).get(Integer.valueOf(pair.getKey()));
                agencyList += agencyStr + " ";
                found = true;
            }
        }

        if(!found){
            agencyList = "NONE";
        }

        return agencyList;
    }
}
