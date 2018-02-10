package MessageHandler;

import Launches.LaunchObject;
import Launches.LaunchesReader;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;

import java.util.List;

public class MessageConstructorLaunches {
    private JDA jda;

    public MessageConstructorLaunches(JDA jda){
        this.jda = jda;
    }

    public void sendLaunchMessage(Long channelID){
        MessageConstructor constructor = new MessageConstructor("Next Launch", jda);
        LaunchesReader launchesReader = new LaunchesReader();

        LaunchObject nextLaunch = launchesReader.getNextLaunch();
        String vehicle = nextLaunch.name.split("\\|")[0];

        constructor.setThumbnailURL(nextLaunch.rocket.imageURL);
        if(nextLaunch == null){
            constructor.appendDescription("Error getting launch data, please try again");
        }else{
            constructor.appendDescription(
                    "**Launch Vehicle: **" + vehicle + "\n" +
                    getLaunchBody(nextLaunch));

            constructor.appendDescription("\n\n" + getLaunchVidURLs(nextLaunch));
        }
        constructor.sendMessage(channelID);
    }

    public void sendMultiLaunchMessage(Long channelID, int amount){
        LaunchesReader launchesReader = new LaunchesReader();

        List<LaunchObject> launchObjectList = launchesReader.getLaunches(amount).launches;
        MessageConstructor constructor = new MessageConstructor("Upcoming " + launchObjectList.size() + " Launches", jda);

        for(int i = 0; i < launchObjectList.size(); i++){
            LaunchObject launch = launchObjectList.get(i);
            constructor.addField("Launch Vehicle: " + launch.name.split("\\|")[0], getLaunchBody(launch));
        }

        constructor.sendMessage(channelID);
    }

    public void sendLaunchAlertMessage(LaunchObject launch, List<Long> channelIDs, List<Long> userNotifIDs){
        List<String> launchTimeData = launch.timeToLaunchData;
        Long days = Long.parseLong(launchTimeData.get(2));
        Long hours = Long.parseLong(launchTimeData.get(3));
        Long minutes = Long.parseLong(launchTimeData.get(4));
        String netData = null;

        if (days == 1 && hours == 0 && minutes == 0) {
            netData = days * 24 + " Hours";
        }else if (days == 0){
            if (minutes == 0){
                if (hours == 12 || hours == 6 || hours == 3 || hours == 1){
                    netData = hours + " Hours";
                }
            }else{
                if (hours == 0){
                    if(minutes == 30 || minutes == 15 || minutes == 5 ||minutes == 1){
                        netData = minutes + " Minute(s)";
                    }
                }
            }
        }

        if(netData != null) {
            MessageConstructor constructor = new MessageConstructor("Launch Alert", jda);

            String vehicle = launch.name.split("\\|")[0];
            constructor.appendDescription(
                    "**Launch Vehicle: **" + vehicle + "\n" +
                     "**NET in " + netData);

            if(launch.tbddate == 1 || launch.tbdtime == 1){
                constructor.appendDescription(" **TBD**");
            }

            constructor.appendDescription(
                    "**" + "\n\n" +
                    getLaunchVidURLs(launch));
            constructor.setThumbnailURL(launch.rocket.imageURL);
            for(int i = 0; i < channelIDs.size(); i++) {
                constructor.sendMessageNoReset(channelIDs.get(i));
            }

            if(userNotifIDs != null) {
                for (int i = 0; i < userNotifIDs.size(); i++) {
                    User user = jda.getUserById(userNotifIDs.get(i));
                    constructor.sendPrivate(user);
                }
            }
        }
    }
    
    public String getLaunchBody(LaunchObject launchObject){
        String payload = launchObject.name.split("\\|")[1];
        String textBody =
                "**Launch Status: " + launchObject.statusText + "** \n" +
                "**Payload: **"  + payload + "\n" +
                "**Launch Site: **" + launchObject.padName + "\n" +
                "**NET: **" + launchObject.timeToLaunchData.get(0);

        if(launchObject.tbddate == 1 || launchObject.tbdtime == 1){
           textBody += " **TBD**";
        }

        textBody += "\n" + launchObject.timeToLaunchData.get(1);
        
        return textBody;
    }

    public String getLaunchVidURLs(LaunchObject launchObject){
        String returnString = "";

        if(launchObject.vidURLs.size() != 0) {
            returnString = "**Watch Live: **";
            for (int i = 0; i < launchObject.vidURLs.size(); i++) {
                String url = launchObject.vidURLs.get(i);
                returnString += "\n" + url;
            }
            returnString += "\n";
        }
        returnString += "**Rocket Watch: ** \n https://rocketwatch.yasiu.pl/?id=" + launchObject.id;

        return returnString;
    }

}
