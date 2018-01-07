package uk.co.rascagneres.spacexbot.Modules;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.impl.UserImpl;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.RestAction;
import uk.co.rascagneres.spacexbot.Config.*;
import uk.co.rascagneres.spacexbot.LaunchData.Launch;
import uk.co.rascagneres.spacexbot.Services.TwitterService;
import uk.co.rascagneres.spacexbot.Utilities.Utils;

import java.awt.*;
import java.util.List;
import java.util.Map;

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
            String pingRate = String.valueOf(event.getJDA().getPing());
            embedBuilder.setAuthor("Ping", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
            embedBuilder.setDescription(pingRate + " ms");
            embedBuilder.setColor(new Color(51, 153, 255));
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }

        if(command[0].equalsIgnoreCase(prefix + "info")){
            embedBuilder.setAuthor("Bot Info", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
            embedBuilder.setDescription("**App Name: **SpaceBot \n**App Version: **1.0 \n**Owner: ** Scorp");
            embedBuilder.setColor(new Color(51, 153, 255));
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }

        if(command[0].equalsIgnoreCase(prefix + "help")){
            embedBuilder.setAuthor("Commands List", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
            CommandReader commandReader = new CommandReader();
            List<Map<String, List<Command>>> commandsListMap = commandReader.getCommandsMap();
            for (int i = 0; i < commandsListMap.size(); i++) {
                Map<String, List<Command>> commandsMap = commandsListMap.get(i);
                for (Map.Entry<String, List<Command>> entry : commandsMap.entrySet()) {
                    String category = entry.getKey();
                    List<Command> thisCommand = entry.getValue();
                    embedBuilder.appendDescription("**" + category + "** \n");
                    for (int j = 0; j < thisCommand.size(); j++) {
                        embedBuilder.appendDescription("**" + thisCommand.get(j).name + "**\n");
                        String alias = "";
                        if(thisCommand.get(j).commandAlias != "") {
                            alias = ", " + prefix + thisCommand.get(j).commandAlias;
                        }
                        embedBuilder.appendDescription(prefix + thisCommand.get(j).command + alias + "\n");

                        if(thisCommand.get(j).parameters != ""){
                            embedBuilder.appendDescription("Parameters: " + thisCommand.get(j).parameters);
                        }
                        embedBuilder.appendDescription(thisCommand.get(j).description + "\n\n");
                    }
                }
            }
            event.getMember().getUser().openPrivateChannel().queue( (channel) -> channel.sendMessage(embedBuilder.build()).queue() );

        }

        if(command[0].equalsIgnoreCase(prefix + "test")) {
            //System.out.println(Utils.getRedditPosts("spacex", 1).get(0).getTitle());
            //System.out.println(Utils.getRedditTitle("spacex"));
            Utils.getRedditTitle("spacex");
        }
    }
}
