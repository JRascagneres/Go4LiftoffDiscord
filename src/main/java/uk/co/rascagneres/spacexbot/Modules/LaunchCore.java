package uk.co.rascagneres.spacexbot.Modules;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import uk.co.rascagneres.spacexbot.Config.Config;
import uk.co.rascagneres.spacexbot.Config.ConfigReader;
import uk.co.rascagneres.spacexbot.LaunchData.Launch;
import uk.co.rascagneres.spacexbot.LaunchData.LaunchLibrary;
import uk.co.rascagneres.spacexbot.Utilities.Utils;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static java.lang.StrictMath.min;

public class LaunchCore extends ListenerAdapter{
    ConfigReader configReader = new ConfigReader();
    private String prefix = configReader.getPrefix();

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        String[] command = event.getMessage().getContent().split(" ");
        String message = event.getMessage().getContent();

        EmbedBuilder embedBuilder = new EmbedBuilder();

        if(!command[0].startsWith(prefix))
            return;

        if(command[0].equalsIgnoreCase(prefix + "nextLaunch") || command[0].equalsIgnoreCase(prefix + "nl")){
            Launch nextLaunch = Utils.getNextLaunch();
            String[] launchName = nextLaunch.name.split("\\|");
            List<String> dateInfo = getDateInfo(nextLaunch);
            String newNET = dateInfo.get(0);
            String launchInText = dateInfo.get(1);
            embedBuilder.setAuthor("Next Launch", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
            embedBuilder.setThumbnail(nextLaunch.rocket.imageURL);
            embedBuilder.setColor(new Color(51, 153, 255));
            if (nextLaunch instanceof Launch){
                embedBuilder.appendDescription("**Name: **" + launchName[0]  + "\n");
                embedBuilder.appendDescription("**Payload: **" + launchName[1]  + "\n");
                embedBuilder.appendDescription("**NET: **" + newNET + "\n");
                embedBuilder.appendDescription(launchInText);
            }else{
                embedBuilder.appendDescription("**ERROR, Report to developer**");
            }
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }

        if(command[0].equalsIgnoreCase(prefix + "listLaunches") || command[0].equalsIgnoreCase(prefix + "ll")){
            int amount = 10;
            try {
                if (command.length >= 2) {
                    amount = Integer.parseInt(command[1]);
                }
                int numberOfLaunches = min(amount,10);
                LaunchLibrary launchLibrary = Utils.getLaunches(numberOfLaunches);
                embedBuilder.setAuthor("Upcoming " + numberOfLaunches + " Launches", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
                embedBuilder.setColor(new Color(51, 153, 255));
                for (int i = 0; i < launchLibrary.count; i++){
                    List<String> dateInfo = getDateInfo(launchLibrary.launches.get(i));
                    Launch thisLaunch = launchLibrary.launches.get(i);
                    String[] launchName = thisLaunch.name.split("\\|");
                    embedBuilder.addField("**Name: **" + launchName[0], "\n**Payload: **" + launchName[1]  + "\n**NET: **" + dateInfo.get(0) + "\n" + dateInfo.get(1), false);
                }
            }catch (Exception ex){
                embedBuilder.setAuthor("Please only supply a number!", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
                ex.printStackTrace();
            }
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }

    }

    public List<String> getDateInfo(Launch thisLaunch){
        Date date = new Date();
        Instant now = Instant.now();
        try {
            date = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss").parse(thisLaunch.net);
        }catch (Exception e){
            e.printStackTrace();
        }
        String newNET = new SimpleDateFormat("dd MMMM HH:mm:ss").format(date);
        Duration timeToLaunch = Duration.between(now, date.toInstant());
        String days = String.valueOf(timeToLaunch.toHours() / 24);
        String hours = String.valueOf(timeToLaunch.toHours() - (Long.parseLong(days) * 24));
        String minutes = String.valueOf(timeToLaunch.toMinutes() - (Long.parseLong(hours) * 60) - (Long.parseLong(days) * 24 * 60));
        List<String> dateInfo = new LinkedList<String>();
        dateInfo.add(newNET);
        dateInfo.add("**Launch In: **" + days + " Days " + hours + " hours " + minutes + " minutes");
        return dateInfo;
    }
}
