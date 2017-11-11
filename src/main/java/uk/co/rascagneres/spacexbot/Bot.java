package uk.co.rascagneres.spacexbot;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import uk.co.rascagneres.spacexbot.Config.ConfigReader;
import uk.co.rascagneres.spacexbot.Modules.CommandsCore;
import uk.co.rascagneres.spacexbot.Modules.LaunchCore;
import uk.co.rascagneres.spacexbot.Modules.RedditModule;
import uk.co.rascagneres.spacexbot.Services.RedditService;

import javax.security.auth.login.LoginException;
import java.util.Timer;

/**
 * Created by jacqu on 10/11/2017.
 */
public class Bot extends ListenerAdapter{
    private static JDA jda;

    public static void main(String [] args){
        try {
            ConfigReader configReader = new ConfigReader();
            jda = new JDABuilder(AccountType.BOT).setToken(configReader.getToken()).buildBlocking();
            jda.getPresence().setGame(Game.of("Monitoring Chat"));
            jda.addEventListener(new CommandsCore());
            jda.addEventListener(new Bot());
            jda.addEventListener(new LaunchCore());
            jda.addEventListener(new RedditModule());

        } catch (LoginException | InterruptedException | RateLimitedException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        Timer timer = new Timer();
        timer.schedule(new RedditService(jda), 0, 10000);
    }

    public void onGuildJoinEvent(GuildJoinEvent event){
        System.out.println("Joined: " + event.getGuild().getName());
    }
}
