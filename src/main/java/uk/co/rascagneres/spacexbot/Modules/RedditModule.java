package uk.co.rascagneres.spacexbot.Modules;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import uk.co.rascagneres.spacexbot.Config.Config;
import uk.co.rascagneres.spacexbot.Config.ConfigReader;


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

        if(command[0].equalsIgnoreCase(prefix + "addChannel")){
            Long redditChannelID = event.getChannel().getIdLong();
            String subreddit = command[1];
            ConfigReader configReader = new ConfigReader();
            configReader.addRedditChannelID(subreddit, redditChannelID);
            embedBuilder.setAuthor("Channel Added", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }

        if(command[0].equalsIgnoreCase(prefix + "removeChannel")){
            Long redditChannelID = event.getChannel().getIdLong();
            ConfigReader configReader = new ConfigReader();
            configReader.removeRedditChannelID(redditChannelID);
            embedBuilder.setAuthor("Channel Removed", null, "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg");
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }


    }
}
