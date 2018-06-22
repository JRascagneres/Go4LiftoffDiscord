package MessageHandler;

import Config.ConfigNotifications;
import Config.ConfigReader;
import Launches.LaunchObject;
import Launches.LaunchesReader;
import Utilities.Utils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;

import java.util.*;

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

            constructor.appendDescription("\n\n" + getMoreInfoURLs(nextLaunch));
            constructor.appendDescription("\n\n" + getLaunchVidURLs(nextLaunch));

        }
        constructor.sendMessage(channelID);
    }

    public void sendMultiLaunchMessage(Long channelID, int amount){
        sendMultiLaunchMessage(channelID, amount, true);
    }


    public void sendMultiLaunchMessage(Long channelID, int amount, boolean upcoming){
        LaunchesReader launchesReader = new LaunchesReader();

        List<LaunchObject> launchObjectList;
        MessageConstructor constructor;
        if(upcoming) {
            launchObjectList = launchesReader.getLaunches(amount).launches;
            constructor = new MessageConstructor("Upcoming " + launchObjectList.size() + " Launches", jda);
        }else {
            launchObjectList = launchesReader.getLaunches(amount, false).launches;
            constructor = new MessageConstructor("Past " + launchObjectList.size() + " Launches", jda);
        }

        for(int i = 0; i < launchObjectList.size(); i++){
            LaunchObject launch = launchObjectList.get(i);

            if(upcoming) {
                constructor.addField("Launch Vehicle: " + launch.name.split("\\|")[0], getLaunchBody(launch));
            }else{
                constructor.addField("Launch Vehicle: " + launch.name.split("\\|")[0], getLaunchBody(launch, false));
            }
        }
        constructor.addField("**See More Launches**", "[Go4Liftoff Website](https://go4liftoff.com)");
        constructor.sendMessage(channelID);
    }

    public void sendLaunchCustomAlertMessage(LaunchObject launch, Map<Integer, List<Long>> channelMap){
        List<String> launchTimeData = launch.timeToLaunchData;
        Long days = Long.parseLong(launchTimeData.get(2));
        Long hours = Long.parseLong(launchTimeData.get(3));
        Long minutes = Long.parseLong(launchTimeData.get(4));
        Integer totalMinutes = Integer.parseInt(launchTimeData.get(5));
        String netData = days + " Day(s) " + hours + " Hour(s) " + minutes + " Minute(s)";

        List<Long> sendToChannelIDs =  new LinkedList<>();

        if(channelMap.containsKey(totalMinutes)){
            sendToChannelIDs.addAll(channelMap.get(totalMinutes));
        }else{
            return;
        }

        MessageConstructor constructor = new MessageConstructor("Launch Alert", jda);

        String vehicle = launch.name.split("\\|")[0];
        constructor.appendDescription(
                "**Launch Vehicle: **" + vehicle + "\n" +
                        "**NET in " + netData + "**");

        if(launch.tbddate == 1 || launch.tbdtime == 1){
            constructor.appendDescription(" **TBD**");
        }

        constructor.appendDescription(
                "\n\n" +
                        getMoreInfoURLs(launch));
        constructor.appendDescription(
                "\n\n" +
                        getLaunchVidURLs(launch));

        constructor.setThumbnailURL(launch.rocket.imageURL);
        for(int i = 0; i < sendToChannelIDs.size(); i++) {
            constructor.sendMessageNoReset(sendToChannelIDs.get(i));
        }

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
                     "**NET in " + netData + "**");

            if(launch.tbddate == 1 || launch.tbdtime == 1){
                constructor.appendDescription(" **TBD**");
            }

            constructor.appendDescription(
                    "\n\n" +
                            getMoreInfoURLs(launch));
            constructor.appendDescription(
                    "\n\n" +
                    getLaunchVidURLs(launch));

            constructor.setThumbnailURL(launch.rocket.imageURL);
            for(int i = 0; i < channelIDs.size(); i++) {
                constructor.sendMessageNoReset(channelIDs.get(i));
            }

            if(userNotifIDs != null) {
                for (int i = 0; i < userNotifIDs.size(); i++) {

                    List<Integer> userLSPs = new LinkedList<>();
                    User user = jda.getUserById(userNotifIDs.get(i));

                    ConfigReader configReader = new ConfigReader();
                    Iterator iterator = configReader.getUserNoficationMap().entrySet().iterator();

                    while(iterator.hasNext()){
                        Map.Entry<String, List<Long>> pair = (Map.Entry) iterator.next();
                        if(pair.getValue().contains(user.getIdLong())){
                            userLSPs.add(Integer.valueOf(pair.getKey()));
                        }
                    }

                    if(userLSPs.contains(launch.lsp.id) || userLSPs.contains(-1)){
                        constructor.sendPrivate(user);
                    }
                }
            }
        }
    }

    public String getLaunchBody(LaunchObject launchObject){
        return getLaunchBody(launchObject, true);
    }

    public String getLaunchBody(LaunchObject launchObject, boolean upcoming){
        String payload = launchObject.name.split("\\|")[1];
        String textBody =
                "**Launch Status: " + launchObject.statusText + "** \n" +
                "**Payload: **"  + payload + "\n" +
                "**Launch Site: **" + launchObject.padName + "\n";

        if(upcoming) {
            textBody += "**NET: **";
        }else{
            textBody += "**Launched On: **";
        }

        textBody += launchObject.timeToLaunchData.get(0);

        if(launchObject.tbddate == 1 || launchObject.tbdtime == 1){
           textBody += " **TBD**";
        }

        if(upcoming) {
            textBody += "\n" + launchObject.timeToLaunchData.get(1);
        }
        
        return textBody;
    }

    public String getLaunchVidURLs(LaunchObject launchObject){
        String returnString = "";
        returnString = "**Follow Along Live: **";
        if(launchObject.vidURLs != null && launchObject.vidURLs.size() != 0) {

            for (int i = 0; i < launchObject.vidURLs.size(); i++) {
                String url = launchObject.vidURLs.get(i);
                url = getFormattedURL(url);
                returnString += "\n" + url;
            }
        }
        returnString += "\n[Rocket Watch](https://rocketwatch.yasiu.pl/?id=" + launchObject.id + "&utm_source=discord&utm_campaign=launchbot)" ;
        returnString += "\n";
        return returnString;
    }

    public String getMoreInfoURLs(LaunchObject launchObject){
        String returnString = "";
        returnString = "**Learn More: **";
        returnString += "\n[Go4Liftoff](https://go4liftoff.com/#page=singleLaunch?filters=launchID=" + launchObject.id + ")" ;
        return returnString;
    }

    public String getFormattedURL(String vidURL){
        if(vidURL.contains("youtube")){
            return "[YouTube](" + vidURL + ")";
        }else{
            String domain = vidURL.substring(Utils.getNthIndex(vidURL, "/".toCharArray()[0], 2) + 1, Utils.getNthIndex(vidURL, "/".toCharArray()[0], 3));
            return "[" + domain + "](" + vidURL + ")";
        }
    }

}
