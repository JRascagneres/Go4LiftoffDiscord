package CoreModules;

import Config.ConfigReader;
import Config.ConfigTwitter;
import MessageHandler.MessageConstructor;
import MessageHandler.MessageConstructorTwitter;
import Permissions.PermissionLevel;
import Permissions.PermissionsChecker;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandsTwitter extends ListenerAdapter{
    ConfigReader configReader = new ConfigReader();
    private String prefix = configReader.getPrefix();

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] command = event.getMessage().getContent().split(" ");
        MessageConstructor constructor = new MessageConstructor(event.getJDA());

        if (!command[0].startsWith(prefix)) {
            return;
        }

        if(command[0].equalsIgnoreCase(prefix + "addTwitter")){
            if(PermissionsChecker.canRun(event.getMember(), event.getChannel(), PermissionLevel.BotManager)) {
                ConfigTwitter configTwitter = new ConfigTwitter();
                String twitterUser = command[1];
                Long channelID = event.getChannel().getIdLong();
                if (configTwitter.addTwitterUser(twitterUser, channelID)) {
                    constructor.setAuthor("Twitter Added!");
                    constructor.setSuccessColor();
                } else {
                    if (!configTwitter.checkTwitterExists(twitterUser)) {
                        constructor.setAuthor("Twitter user doesn't exist!");
                        constructor.setFailColor();
                    } else {
                        constructor.setAuthor("Already exists!");
                        constructor.setFailColor();
                    }
                }
            }else{
                constructor.setAuthor("You are not authorised to run this command");
                constructor.setFailColor();
            }

            constructor.sendMessage(event.getChannel().getIdLong());
        }

        if(command[0].equalsIgnoreCase(prefix + "removeTwitter")){
            if(PermissionsChecker.canRun(event.getMember(), event.getChannel(), PermissionLevel.BotManager)){
                ConfigTwitter configTwitter = new ConfigTwitter();
                String twitterUser = command[1];
                Long channelID = event.getChannel().getIdLong();
                if(configTwitter.removeTwitterUser(twitterUser, channelID)){
                    constructor.setAuthor("Twitter Removed!");
                    constructor.setSuccessColor();
                }else{
                    constructor.setAuthor("No record of this twitter channel!");
                    constructor.setFailColor();
                }
            }else{
                constructor.setAuthor("You are not authorised to run this command");
                constructor.setFailColor();
            }
            constructor.sendMessage(event.getChannel().getIdLong());
        }

        if(command[0].equalsIgnoreCase(prefix + "getFollowedTwitter")){
            MessageConstructorTwitter messageConstructorReddit = new MessageConstructorTwitter(event.getJDA());
            messageConstructorReddit.sendFollowedTwitterMessage(event.getChannel().getIdLong());
        }

    }
}
