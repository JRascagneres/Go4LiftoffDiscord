package uk.co.rascagneres.spacexbot.Services;


import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import uk.co.rascagneres.spacexbot.Config.ConfigReader;
import uk.co.rascagneres.spacexbot.LaunchData.Launch;
import uk.co.rascagneres.spacexbot.LaunchData.LaunchLibrary;
import uk.co.rascagneres.spacexbot.Utilities.Utils;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.TimerTask;

public class CountdownService extends TimerTask {

    JDA jda;

    public CountdownService(JDA jda){
        this.jda = jda;
    }

    public void run() {
        ConfigReader configReader = new ConfigReader();
        List<Long> countdownChannelIDs = configReader.getCountdownChannels();
        LaunchLibrary launches = Utils.getLaunches(10);
        if (launches != null) {
            for (int i = 0; i < launches.launches.size(); i++) {
                Launch thisLaunch = launches.launches.get(i);

                if (thisLaunch.status == 1 && (thisLaunch.tbddate == 0 && thisLaunch.tbdtime == 0)) {
                    List<String> timeToLaunchList = Utils.getTimeToLaunchData(thisLaunch);
                    Long days = Long.parseLong(timeToLaunchList.get(0));
                    Long hours = Long.parseLong(timeToLaunchList.get(1));
                    Long minutes = Long.parseLong(timeToLaunchList.get(2));

                    if (days == 1 && hours == 0 && minutes == 0) {
                        sendMessage(thisLaunch, countdownChannelIDs, "24 Hours");
                    } else if (days == 0) {
                        if (minutes == 0) {
                            if (hours == 12) {
                                sendMessage(thisLaunch, countdownChannelIDs, "12 Hours");
                            } else if (hours == 6) {
                                sendMessage(thisLaunch, countdownChannelIDs, "6 Hours");
                            } else if (hours == 1) {
                                sendMessage(thisLaunch, countdownChannelIDs, "1 Hour");
                            }
                        } else {
                            if (hours == 0) {
                                if (minutes == 30) {
                                    sendMessage(thisLaunch, countdownChannelIDs, "30 Minutes");
                                } else if (minutes == 15) {
                                    sendMessage(thisLaunch, countdownChannelIDs, "15 Minutes");
                                } else if (minutes == 1) {
                                    sendMessage(thisLaunch, countdownChannelIDs, "1 Minute!");
                                } else if (minutes == 0) {
                                    sendMessage(thisLaunch, countdownChannelIDs, "NOW!");
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    public void sendMessage(Launch thisLaunch, List<Long> channelIDs, String text){
        for (int i = 0; i < channelIDs.size(); i++){
            EmbedBuilder embedBuilder = new EmbedBuilder();
            String[] launchName = thisLaunch.name.split("\\|");

            embedBuilder.setAuthor("Launch Alert!", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
            embedBuilder.setThumbnail(thisLaunch.rocket.imageURL);
            embedBuilder.setColor(new Color(51, 153, 255));
            embedBuilder.appendDescription("**Name: **" + launchName[0]  + "\n");
            embedBuilder.appendDescription("**NET in " + text + "**\n\n");
            if(text == "15 Minutes" || text == "1 Minute!"){
                String vidURLText = "";
                for(int j = 0; j < thisLaunch.vidURLs.size(); j++) {
                    String url = thisLaunch.vidURLs.get(j);
                    if (!vidURLText.isEmpty()){
                        vidURLText += "\n" + url;
                    }else{
                        vidURLText += url;
                    }
                }
                if (thisLaunch.vidURLs.size() > 0) {
                    embedBuilder.appendDescription("**Watch Live: **\n" + vidURLText + "\n");
                }
            }

            jda.getTextChannelById(channelIDs.get(i)).sendMessage(embedBuilder.build()).queue();
        }


    }
}
