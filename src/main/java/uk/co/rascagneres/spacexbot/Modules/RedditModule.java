package uk.co.rascagneres.spacexbot.Modules;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import uk.co.rascagneres.spacexbot.Config.Config;
import uk.co.rascagneres.spacexbot.Config.ConfigReader;
import uk.co.rascagneres.spacexbot.Config.PermissionLevel;
import uk.co.rascagneres.spacexbot.Utilities.Utils;


public class RedditModule extends ListenerAdapter{
    ConfigReader configReader = new ConfigReader();
    private String prefix = configReader.getPrefix();

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        String[] command = event.getMessage().getContent().split(" ");
        String message = event.getMessage().getContent();

        EmbedBuilder embedBuilder = new EmbedBuilder();

        if(!command[0].startsWith(prefix))
            return;

        if(command[0].equalsIgnoreCase(prefix + "addReddit")){
            Long channelID = event.getChannel().getIdLong();
            String subreddit = command[1].toLowerCase();
            if(Utils.PermissionResolver(event.getMember(), event.getChannel()).getValue() >= PermissionLevel.BotManager.getValue())
            {
                if(Utils.checkSubredditExists(subreddit)) {
                    ConfigReader configReader = new ConfigReader();
                    if(configReader.getRedditMap().containsKey(subreddit) && configReader.getRedditMap().get(subreddit).contains(channelID)){
                        embedBuilder.setAuthor("Already exists!", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
                    }else {
                        configReader.addRedditChannelID(subreddit, channelID);
                        embedBuilder.setAuthor("Channel Added", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
                    }
                }else{
                    embedBuilder.setAuthor("Subreddit doesn't exist!", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
                }
            }else{
                embedBuilder.setAuthor("You are not authorised to run this command", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
            }
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }

        if(command[0].equalsIgnoreCase(prefix + "removeReddit")){
            if(Utils.PermissionResolver(event.getMember(), event.getChannel()).getValue() >= PermissionLevel.BotManager.getValue())
            {
                Long channelID = event.getChannel().getIdLong();
                String subreddit = command[1].toLowerCase();
                ConfigReader configReader = new ConfigReader();
                if (configReader.getRedditMap().containsKey(subreddit) && configReader.getRedditMap().get(subreddit).contains(channelID)) {
                    configReader.removeRedditChannelID(subreddit, channelID);
                    embedBuilder.setAuthor("Channel Removed", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
                }else{
                    embedBuilder.setAuthor("No record of this channel!", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
                }
            }else{
                embedBuilder.setAuthor("You are not authorised to run this command", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
            }
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }


    }
}
