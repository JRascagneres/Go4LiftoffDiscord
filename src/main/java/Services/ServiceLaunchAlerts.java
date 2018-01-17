package Services;

import Config.ConfigReader;
import Launches.LaunchObject;
import Launches.LaunchesReader;
import MessageHandler.MessageConstructorLaunches;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;

import java.util.List;
import java.util.TimerTask;

public class ServiceLaunchAlerts extends TimerTask{

    JDA jda;

    public ServiceLaunchAlerts(JDA jda){
        this.jda = jda;
    }

    public void run(){
        LaunchesReader launchesReader = new LaunchesReader();
        List<LaunchObject> launchObjectList = launchesReader.getLaunches(5).launches;
        for(int i = 0; i < launchObjectList.size(); i++){
            ConfigReader configReader = new ConfigReader();
            List<Long> channelIDs = configReader.getCountdownChannels();
            List<Long> userNotifs = configReader.getUserNoficationMap().get("all");

            MessageConstructorLaunches messageConstructorLaunches = new MessageConstructorLaunches(jda);

            LaunchObject launchObject = launchObjectList.get(i);

            messageConstructorLaunches.sendLaunchAlertMessage(launchObject, channelIDs, userNotifs);

        }
    }
}
