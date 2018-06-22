package CoreModules;

import Commands.CommandObject;
import Commands.CommandReader;
import Config.ConfigNotifications;
import Config.ConfigReader;
import Launches.LaunchObject;
import Launches.LaunchesReader;
import MessageHandler.MessageConstructor;
import MessageHandler.MessageConstructorLaunches;
import Permissions.PermissionLevel;
import Permissions.PermissionsChecker;
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

        if(command[0].equalsIgnoreCase(prefix + "info") || command[0].equalsIgnoreCase(prefix + "i")){
            constructor = new MessageConstructor("Bot Info", event.getJDA());
            constructor.appendDescription(
                    "**App Name:** Go4Liftoff \n" +
                    "**Owner:** <@150768477152477186> \n" +
                    "**Join Discord Server:** [Join Now!](https://discord.gg/A7wR7sV) \n" +
                    "Bot still in beta, invite link can be found on our website \n" +
                    "[Go4Liftoff Website](https://go4liftoff.com)");
            constructor.sendMessage(event.getChannel().getIdLong());
        }

        if(command[0].equalsIgnoreCase(prefix + "getServers")){
            if(PermissionsChecker.canRun(event.getMember(), event.getChannel(), PermissionLevel.BotOwner)) {
                List<Guild> currentServers = event.getJDA().getGuilds();
                constructor = new MessageConstructor("Servers Joined: #" + currentServers.size(), event.getJDA());
                for (int i = 0; i < currentServers.size(); i++) {
                    Guild currentGuild = currentServers.get(i);
                    constructor.appendDescription(currentGuild.getMembers().size() + " Users on " + currentGuild.getName() + "\n");
                }
                constructor.sendMessage(event.getChannel().getIdLong());
            }else{
                constructor = new MessageConstructor("Nice Try! This command is not for you!", event.getJDA());
                constructor.setFailColor();
                constructor.sendMessage(event.getChannel().getIdLong());
            }
        }

        if(command[0].equalsIgnoreCase(prefix + "serverCount")){
            if(PermissionsChecker.canRun(event.getMember(), event.getChannel(), PermissionLevel.BotOwner)) {
                List<Guild> currentServers = event.getJDA().getGuilds();
                constructor = new MessageConstructor("Servers Joined: #" + currentServers.size(), event.getJDA());
                constructor.sendMessage(event.getChannel().getIdLong());
            }else{
                constructor = new MessageConstructor("Nice Try! This command is not for you!", event.getJDA());
                constructor.setFailColor();
                constructor.sendMessage(event.getChannel().getIdLong());
            }
        }

        if(command[0].equalsIgnoreCase(prefix + "cumUsers")){
            if(PermissionsChecker.canRun(event.getMember(), event.getChannel(), PermissionLevel.BotOwner)) {
                List<Guild> currentServers = event.getJDA().getGuilds();

                int cumUsers = 0;
                for(int i = 0; i < currentServers.size(); i++){
                    cumUsers += currentServers.get(i).getMembers().size();
                }

                constructor = new MessageConstructor("Cumulative Users: #" + cumUsers, event.getJDA());
                constructor.sendMessage(event.getChannel().getIdLong());
            }else{
                constructor = new MessageConstructor("Nice Try! This command is not for you!", event.getJDA());
                constructor.setFailColor();
                constructor.sendMessage(event.getChannel().getIdLong());
            }
        }

        if(command[0].equalsIgnoreCase(prefix + "help")){
            String textToAdd = "";
            Integer stringLength = 0;
            CommandReader commandReader = new CommandReader();
            constructor = new MessageConstructor("Commands List", event.getJDA());
            List<Map<String, List<CommandObject>>> commandsListMap = commandReader.getCommands();
            for(int i = 0; i < commandsListMap.size(); i++){
                Map<String, List<CommandObject>> commandsMap = commandsListMap.get(i);
                for(Map.Entry<String, List<CommandObject>> entry : commandsMap.entrySet()){
                    String commandCategory = entry.getKey();
                    List<CommandObject> categoryCommandList = entry.getValue();
                    textToAdd += ("**" + commandCategory + "** \n");
                    for(int j = 0; j < categoryCommandList.size(); j++){


                        CommandObject commandObject = categoryCommandList.get(j);
                        textToAdd += ("**" + commandObject.name + "**\n");
                        String aliasList = "";
                        if(commandObject.commandAlias != ""){
                            aliasList = ", " + prefix + commandObject.commandAlias;
                        }
                        textToAdd += (prefix + commandObject.command + aliasList + "\n");

                        if(commandObject.parameters != ""){
                            textToAdd += ("Parameters: " + commandObject.parameters + "\n");
                        }

                        textToAdd += (commandObject.description + "\n\n");

                        stringLength += textToAdd.length();

                        if(stringLength > 2035){
                            constructor.sendPrivate(event.getMember().getUser());
                            constructor = new MessageConstructor("Commands List", event.getJDA());
                            stringLength = textToAdd.length();
                        }
                        constructor.appendDescription(textToAdd);
                        textToAdd = "";
                    }
                }
            }
            constructor.sendPrivate(event.getMember().getUser());
        }

        if(command[0].equalsIgnoreCase(prefix + "test")){
            ConfigNotifications configNotifications = new ConfigNotifications();
            System.out.println(configNotifications.getUserNotification(event.getAuthor()));
        }
    }
}
