package CoreModules;

import Config.ConfigReader;
import Launches.LaunchesReader;
import MessageHandler.MessageConstructor;
import MessageHandler.MessageConstructorLaunches;
import Permissions.PermissionLevel;
import Permissions.PermissionsChecker;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;

import static java.lang.StrictMath.min;

public class CommandsLaunches extends ListenerAdapter {
    LaunchesReader launchesReader = new LaunchesReader();
    ConfigReader configReader = new ConfigReader();

    private String prefix = configReader.getPrefix();

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        String[] command = event.getMessage().getContent().split(" ");
        MessageConstructorLaunches constructor = new MessageConstructorLaunches(event.getJDA());
        MessageConstructor messageConstructor = new MessageConstructor(event.getJDA());

        if(!command[0].startsWith(prefix)){
            return;
        }

        if(command[0].equalsIgnoreCase(prefix + "nextLaunch") || command[0].equalsIgnoreCase(prefix + "nl")){
            constructor.sendLaunchMessage(event.getChannel().getIdLong());
        }

        if(command[0].equalsIgnoreCase(prefix + "listLaunches") || command[0].equalsIgnoreCase(prefix + "ll")){
            int amount = 10;
            try {
                if (command.length >= 2) {
                    amount = Integer.parseInt(command[1]);
                }
                amount = min(amount, 10);
                constructor.sendMultiLaunchMessage(event.getChannel().getIdLong(), amount);
            }catch(Exception e){
                messageConstructor.setAuthor("Please only supply a number!");
                messageConstructor.setFailColor();
                messageConstructor.sendMessage(event.getChannel().getIdLong());
            }
        }

        if(command[0].equalsIgnoreCase(prefix + "addCountdownAlerts")){
            if(PermissionsChecker.canRun(event.getMember(), event.getChannel(), PermissionLevel.BotManager)){
                if(configReader.addCountdownChannelID(event.getChannel().getIdLong())){
                    messageConstructor.setSuccessColor();
                    messageConstructor.setAuthor("Channel Added!");
                }else{
                    messageConstructor.setFailColor();
                    messageConstructor.setAuthor("Already exists!");
                }
            }else{
                messageConstructor.setFailColor();
                messageConstructor.setAuthor("You are not authorised to run this command!");
            }
            messageConstructor.sendMessage(event.getChannel().getIdLong());
        }

        if(command[0].equalsIgnoreCase(prefix + "removeCountdownAlerts")){
            if(PermissionsChecker.canRun(event.getMember(), event.getChannel(), PermissionLevel.BotManager)){
                if(configReader.removeCoundownChannelID(event.getChannel().getIdLong())){
                    messageConstructor.setSuccessColor();
                    messageConstructor.setAuthor("Channel Removed!");
                }else{
                    messageConstructor.setFailColor();
                    messageConstructor.setAuthor("No record of this channel!");
                }
            }else{
                messageConstructor.setFailColor();
                messageConstructor.setAuthor("You are not authorised to run this command");
            }
            messageConstructor.sendMessage(event.getChannel().getIdLong());
        }
    }
}
