package CoreModules;

import Commands.CommandObject;
import Commands.CommandReader;
import Config.ConfigReader;
import Launches.LaunchesReader;
import MessageHandler.MessageConstructor;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;
import java.util.Map;

public class CommandsCore extends ListenerAdapter{
    ConfigReader configReader = new ConfigReader();
    private String prefix = configReader.getPrefix();

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        String[] command = event.getMessage().getContent().split(" ");
        MessageConstructor constructor;

        if(!command[0].startsWith(prefix)){
            return;
        }

        if(command[0].equalsIgnoreCase(prefix + "ping")){
            constructor = new MessageConstructor("Ping", event.getJDA());
            constructor.appendDescription(String.valueOf(event.getJDA().getPing()) + "ms");
            constructor.sendMessage(event.getChannel().getIdLong());
        }

        if(command[0].equalsIgnoreCase(prefix + "info")){
            constructor = new MessageConstructor("Bot Info", event.getJDA());
            constructor.appendDescription(
                    "**App Name:** Go4Liftoff \n" +
                    "**Owner:** <@150768477152477186> \n" +
                    "Bot still in beta, invite link can be found on our website \n" +
                    "[Go4Liftoff Website](https://go4liftoff.com)");
            constructor.sendMessage(event.getChannel().getIdLong());
        }

        if(command[0].equalsIgnoreCase(prefix + "getServers")){
            List<Guild> currentServers = event.getJDA().getGuilds();
            constructor = new MessageConstructor("Servers Joined: #" + currentServers.size(), event.getJDA());
            for(int i = 0; i < currentServers.size(); i++){
                Guild currentGuild = currentServers.get(i);
                constructor.addField(currentGuild.getName(), currentGuild.getId());
            }
            constructor.sendMessage(event.getChannel().getIdLong());
        }

        if(command[0].equalsIgnoreCase(prefix + "help")){
            CommandReader commandReader = new CommandReader();
            constructor = new MessageConstructor("Commands List", event.getJDA());
            List<Map<String, List<CommandObject>>> commandsListMap = commandReader.getCommands();
            for(int i = 0; i < commandsListMap.size(); i++){
                Map<String, List<CommandObject>> commandsMap = commandsListMap.get(i);
                for(Map.Entry<String, List<CommandObject>> entry : commandsMap.entrySet()){
                    String commandCategory = entry.getKey();
                    List<CommandObject> categoryCommandList = entry.getValue();
                    constructor.appendDescription("**" + commandCategory + "** \n");
                    for(int j = 0; j < categoryCommandList.size(); j++){
                        CommandObject commandObject = categoryCommandList.get(j);
                        constructor.appendDescription("**" + commandObject.name + "**\n");
                        String aliasList = "";
                        if(commandObject.commandAlias != ""){
                            aliasList = ", " + prefix + commandObject.commandAlias;
                        }
                        constructor.appendDescription(prefix + commandObject.command + aliasList + "\n");

                        if(commandObject.parameters != ""){
                            constructor.appendDescription("Parameters: " + commandObject.parameters);
                        }
                        constructor.appendDescription(commandObject.description + "\n\n");
                    }
                }
            }
            constructor.sendPrivate(event.getMember().getUser());
        }

        if(command[0].equalsIgnoreCase(prefix + "test")){
            LaunchesReader launchesReader = new LaunchesReader();
            event.getChannel().sendMessage(launchesReader.getNextLaunch().name).queue();
            System.out.println(launchesReader.getLaunches(1).launches.get(0).timeToLaunchData.get(0));
        }
    }
}
