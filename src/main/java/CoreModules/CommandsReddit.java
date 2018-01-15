package CoreModules;

import Config.ConfigReader;
import Config.ConfigReddit;
import MessageHandler.MessageConstructor;
import MessageHandler.MessageConstructorReddit;
import Permissions.PermissionLevel;
import Permissions.PermissionsChecker;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandsReddit extends ListenerAdapter {
    ConfigReader configReader = new ConfigReader();
    private String prefix = configReader.getPrefix();

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] command = event.getMessage().getContent().split(" ");
        MessageConstructor constructor = new MessageConstructor(event.getJDA());

        if (!command[0].startsWith(prefix)) {
            return;
        }

        if(command[0].equalsIgnoreCase(prefix + "addReddit")){
            if(PermissionsChecker.canRun(event.getMember(), event.getChannel(), PermissionLevel.BotManager)){
                String subreddit = command[1].toLowerCase();
                Long channelID = event.getChannel().getIdLong();
                ConfigReddit configReddit = new ConfigReddit();
                if(configReddit.addReddit(subreddit, channelID)){
                    constructor.setAuthor("Channel Added!");
                    constructor.setSuccessColor();
                }else{
                    if(!configReddit.checkSubredditExists(subreddit)){
                        constructor.setAuthor("Subreddit doesn't exist!");
                        constructor.setFailColor();
                    }else{
                        constructor.setAuthor("Already exists!");
                        constructor.setFailColor();
                    }
                }
            }else{
                constructor.setAuthor("You are not authorised to run this command!");
                constructor.setFailColor();
            }
            constructor.sendMessage(event.getChannel().getIdLong());
        }

        if(command[0].equalsIgnoreCase(prefix + "removeReddit")){
            if(PermissionsChecker.canRun(event.getMember(), event.getChannel(), PermissionLevel.BotManager)){
                String subreddit = command[1].toLowerCase();
                Long channelID = event.getChannel().getIdLong();
                ConfigReddit configReddit = new ConfigReddit();
                if(configReddit.removeReddit(subreddit, channelID)){
                    constructor.setAuthor("Channel Removed!");
                    constructor.setSuccessColor();
                }else{
                    constructor.setAuthor("No record of this channel!");
                    constructor.setFailColor();
                }
            }else{
                constructor.setAuthor("You are not authorised to run this command!");
                constructor.setFailColor();
            }
            constructor.sendMessage(event.getChannel().getIdLong());
        }

        if(command[0].equalsIgnoreCase(prefix + "getFollowedReddit")){
            MessageConstructorReddit messageConstructorReddit = new MessageConstructorReddit(event.getJDA());
            messageConstructorReddit.sendFollowedRedditMessage(event.getChannel().getIdLong());
        }
    }
}
