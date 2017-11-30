package uk.co.rascagneres.spacexbot.Services;


import net.dv8tion.jda.core.JDA;
import uk.co.rascagneres.spacexbot.Config.ConfigReader;
import uk.co.rascagneres.spacexbot.LaunchData.Launch;
import uk.co.rascagneres.spacexbot.LaunchData.LaunchLibrary;
import uk.co.rascagneres.spacexbot.Utilities.Utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
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
        Date date = new Date();
        Instant now = Instant.now();

        for (int i = 0; i < launches.launches.size(); i++){
            Launch thisLaunch = launches.launches.get(i);
            try{
                date = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss").parse(thisLaunch.net);
            }catch (Exception e){
                e.printStackTrace();
            }

            
        }
    }
}
