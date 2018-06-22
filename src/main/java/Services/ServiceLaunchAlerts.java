package Services;

import Config.ConfigReader;
import Launches.LaunchObject;
import Launches.LaunchesReader;
import MessageHandler.MessageConstructorLaunches;
import net.dv8tion.jda.core.JDA;

import java.util.*;

public class ServiceLaunchAlerts extends TimerTask{

    JDA jda;

    public ServiceLaunchAlerts(JDA jda){
        this.jda = jda;
    }

    public void run(){
        LaunchesReader launchesReader = new LaunchesReader();

        try {
            List<LaunchObject> launchObjectList = launchesReader.getLaunches(5).launches;
            for (int i = 0; i < launchObjectList.size(); i++) {
                ConfigReader configReader = new ConfigReader();
                List<Long> channelIDs = configReader.getCountdownChannels();
                List<Long> userNotifs = new LinkedList<>();
                Map<Integer, List<Long>> customNotifMap = configReader.getCustomNotificationMap();

                Iterator iterator = configReader.getUserNoficationMap().entrySet().iterator();

                while (iterator.hasNext()){
                    Map.Entry<String, List<Long>> pair = (Map.Entry) iterator.next();

                    for(int j = 0; j < pair.getValue().size(); j++){
                        Long userID = pair.getValue().get(j);
                        if(!userNotifs.contains(userID)){
                            userNotifs.add(userID);
                        }
                    }
                }

                MessageConstructorLaunches messageConstructorLaunches = new MessageConstructorLaunches(jda);

                LaunchObject launchObject = launchObjectList.get(i);

                if (launchObject.tbdtime == 0 && launchObject.tbddate == 0) {
                    messageConstructorLaunches.sendLaunchAlertMessage(launchObject, channelIDs, userNotifs);
                    messageConstructorLaunches.sendLaunchCustomAlertMessage(launchObject, customNotifMap);
                }

            }
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error getting launches for alerts!");
        }
    }
}
