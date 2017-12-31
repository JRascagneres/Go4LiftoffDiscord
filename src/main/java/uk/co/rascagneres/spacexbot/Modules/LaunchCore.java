package uk.co.rascagneres.spacexbot.Modules;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import uk.co.rascagneres.spacexbot.Config.Config;
import uk.co.rascagneres.spacexbot.Config.ConfigReader;
import uk.co.rascagneres.spacexbot.Config.PermissionLevel;
import uk.co.rascagneres.spacexbot.LaunchData.Launch;
import uk.co.rascagneres.spacexbot.LaunchData.LaunchLibrary;
import uk.co.rascagneres.spacexbot.Utilities.Utils;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

        if(command[0].equalsIgnoreCase(prefix + "addCountdownChannel")){
            if(Utils.PermissionResolver(event.getMember(), event.getChannel()).getValue() >= PermissionLevel.BotManager.getValue()) {
                Long channelID = event.getChannel().getIdLong();
                if (configReader.getCountdownChannels().contains(channelID)) {
                    embedBuilder.setAuthor("Already exists!", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
                } else {
                    configReader.addCountdownChannel(channelID);
                    embedBuilder.setAuthor("Channel added!", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
                }
            }else{
                embedBuilder.setAuthor("You are not authorised to run this command", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
            }
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }

        if(command[0].equalsIgnoreCase(prefix + "removeCountdownChannel")){
            if(Utils.PermissionResolver(event.getMember(), event.getChannel()).getValue() >= PermissionLevel.BotManager.getValue()) {
                Long channelID = event.getChannel().getIdLong();
                if(configReader.getCountdownChannels().contains(channelID)){
                    configReader.removeCountdownChannel(channelID);
                    embedBuilder.setAuthor("Channel removed!", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
                }else{
                    embedBuilder.setAuthor("No record of this channel!", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
                }
            }else{
                embedBuilder.setAuthor("You are not authorised to run this command", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
            }
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }

        if(command[0].equalsIgnoreCase(prefix + "nextLaunch") || command[0].equalsIgnoreCase(prefix + "nl")){
            Launch nextLaunch = Utils.getNextLaunch();
            if(nextLaunch != null) {
                String[] launchName = nextLaunch.name.split("\\|");
                List<String> dateInfo = getDateInfo(nextLaunch);
                String newNET = dateInfo.get(0);
                String launchInText = dateInfo.get(1);
                embedBuilder.setAuthor("Next Launch", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
                embedBuilder.setThumbnail(nextLaunch.rocket.imageURL);
                embedBuilder.setColor(new Color(51, 153, 255));
                if (nextLaunch instanceof Launch) {
                    embedBuilder.appendDescription("**Name: **" + launchName[0] + "\n");
                    embedBuilder.appendDescription("**Launch Status: " + nextLaunch.statusText + "**\n");
                    embedBuilder.appendDescription("**Payload: **" + launchName[1] + "\n");

                    if (nextLaunch.tbdtime == 1 || nextLaunch.tbddate == 1) {
                        embedBuilder.appendDescription("**NET: **" + newNET + "  **TBD**" + "\n");
                    } else {
                        embedBuilder.appendDescription("**NET: **" + newNET + "\n");
                    }

                    embedBuilder.appendDescription(launchInText + "\n");
                    String vidURLText = "";
                    for (int i = 0; i < nextLaunch.vidURLs.size(); i++) {
                        String url = nextLaunch.vidURLs.get(i);
                        if (!vidURLText.isEmpty()) {
                            vidURLText += "\n" + url;
                        } else {
                            vidURLText += url;
                        }
                    }
                    if (vidURLText != "") {
                        embedBuilder.appendDescription("**Watch Live: **\n" + vidURLText + "\n");
                    }
                    embedBuilder.appendDescription("**Rocket Watch: **\nhttp://rocketwatch.yasiu.pl/?id=" + nextLaunch.id);
                } else {
                    embedBuilder.appendDescription("**ERROR, Report to developer**");
                }
            }else{
                embedBuilder.setAuthor("Error Loading Launches, Please Try Again");
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
                if(launchLibrary != null) {
                    embedBuilder.setAuthor("Upcoming " + numberOfLaunches + " Launches", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
                    embedBuilder.setColor(new Color(51, 153, 255));
                    for (int i = 0; i < launchLibrary.count; i++) {
                        List<String> dateInfo = getDateInfo(launchLibrary.launches.get(i));
                        Launch thisLaunch = launchLibrary.launches.get(i);
                        String[] launchName = thisLaunch.name.split("\\|");

                        String TBD = "";
                        if (thisLaunch.tbdtime == 1 || thisLaunch.tbddate == 1) {
                            TBD = " **TBD**";
                        }
                        embedBuilder.addField("**Name: **" + launchName[0], "\n**Launch Status: " + thisLaunch.statusText + "**\n**Payload: **" + launchName[1] + "\n**NET: **" + dateInfo.get(0) + TBD + "\n" + dateInfo.get(1), false);
                    }
                }else{
                    embedBuilder.setAuthor("Error Loading Launches, Please Try Again");
                }
            }catch (Exception ex){
                embedBuilder.setAuthor("Please only supply a number!", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
                ex.printStackTrace();
            }
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }

    }

    public List<String> getDateInfo(Launch thisLaunch){
        List<String> timeToLaunchList = Utils.getTimeToLaunchData(thisLaunch);
        String days = timeToLaunchList.get(0);
        String hours = timeToLaunchList.get(1);
        String minutes = timeToLaunchList.get(2);
        String tempDate = timeToLaunchList.get(3);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", new Locale("us"));
        Date date;
        try {
            date = sdf.parse(tempDate);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String newNET = simpleDateFormat.format(date);
        List<String> dateInfo = new LinkedList<String>();
        dateInfo.add(newNET);
        dateInfo.add("**Launch In: **" + days + " Days " + hours + " hours " + minutes + " minutes");
        return dateInfo;
    }
}
