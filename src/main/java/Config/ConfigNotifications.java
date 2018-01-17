package Config;

import net.dv8tion.jda.core.entities.User;


public class ConfigNotifications {
    ConfigReader configReader = new ConfigReader();

    public boolean addUserNotifications(String agency, User user){
        if(!(configReader.getUserNoficationMap().containsKey(agency) && configReader.getUserNoficationMap().get(agency).contains(user.getIdLong()))){
            configReader.addUserNotifications(agency, user.getIdLong());
            return true;
        }else{
            return false;
        }
    }

    public boolean removeUserNotifications(String agency, User user){
        if(configReader.getUserNoficationMap().containsKey(agency) && configReader.getUserNoficationMap().get(agency).contains(user.getIdLong())){
            configReader.removeUserNotifications(agency, user.getIdLong());
            return true;
        }else{
            return false;
        }
    }
}
