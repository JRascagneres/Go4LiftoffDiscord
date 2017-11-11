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
            embedBuilder.setAuthor("Next Launch", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
            embedBuilder.setThumbnail(nextLaunch.rocket.imageURL);
            embedBuilder.setColor(new Color(51, 153, 255));
            if (nextLaunch instanceof Launch){
                embedBuilder.appendDescription("**Name: **" + nextLaunch.name  + "\n");

            }else{
                embedBuilder.appendDescription("**ERROR, Report to developer**");
            }
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }

        if(command[0].equalsIgnoreCase(prefix + "listLaunches") || command[0].equalsIgnoreCase(prefix + "ll")){
            int numberOfLaunches = 10;

            if (command.length > 1){
                numberOfLaunches = Integer.parseInt(command[1]);
            }

            LaunchLibrary launchLibrary = Utils.getLaunches(numberOfLaunches);
            Launch nextLaunch = launchLibrary.launches.get(0);

            embedBuilder.setAuthor("Upcoming Launches", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
            embedBuilder.setThumbnail(nextLaunch.rocket.imageURL);
            embedBuilder.setColor(new Color(51, 153, 255));

            for (int i = 0; i < launchLibrary.count; i++){
                Launch thisLaunch = launchLibrary.launches.get(i);
                String[] launchName = thisLaunch.name.split("\\|");
                embedBuilder.addField(launchName[0], "**" + launchName[1] + "**\nLaunch NET: " + thisLaunch.net, false);
            }
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }

    }
}
