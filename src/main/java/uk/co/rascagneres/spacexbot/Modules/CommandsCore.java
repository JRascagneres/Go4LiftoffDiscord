package uk.co.rascagneres.spacexbot.Modules;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import uk.co.rascagneres.spacexbot.Config.Config;
import uk.co.rascagneres.spacexbot.Config.ConfigReader;

import java.awt.*;

/**
 * Created by jacqu on 10/11/2017.
 */
public class CommandsCore extends ListenerAdapter {
    ConfigReader configReader = new ConfigReader();
    private String prefix = configReader.getPrefix();

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        String[] command = event.getMessage().getContent().split(" ");
        String message = event.getMessage().getContent();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if(!command[0].startsWith(prefix))
            return;

        if(command[0].equalsIgnoreCase(prefix + "ping")){
            String msg = "Pong!  " + event.getJDA().getPing();
            if(command.length == 1) {
                event.getChannel().sendMessage(msg).queue();
            }
        }

        if(command[0].equalsIgnoreCase(prefix + "info")){
            embedBuilder.setAuthor("Bot Info", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
            embedBuilder.setDescription("**App Name: **SpaceBot \n**App Version: **1.0 \n**Owner: ** Scorp");
            embedBuilder.setColor(new Color(51, 153, 255));
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }

        if(command[0].equalsIgnoreCase(prefix + "test")){
            //System.out.println(configReader.getChannelIDs());
            System.out.println(configReader.getPrefix());
        }

    }
}
