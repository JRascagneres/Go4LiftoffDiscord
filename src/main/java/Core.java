import Config.ConfigReader;
import CoreModules.*;
import Services.ServiceLaunchAlerts;
import Services.ServiceReddit;
import Services.ServiceTwitter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Timer;

public class Core extends ListenerAdapter{

    private static JDA jda;

    public static void main(String [] args){
        try{
            ConfigReader configReader = new ConfigReader();
            jda = new JDABuilder(AccountType.BOT).setToken(configReader.getToken()).buildBlocking();
            jda.getPresence().setGame(Game.of("Readying For Launch!"));
            jda.getSelfUser().getManager().setName("Go For Launch").queue();
            jda.addEventListener(new Core());
            jda.addEventListener(new CommandsCore());
            jda.addEventListener(new CommandsLaunches());
            jda.addEventListener(new CommandsTwitter());
            jda.addEventListener(new CommandsReddit());
            jda.addEventListener(new CommandsNotifications());
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("FAILED TO CONNECT");
        }

        Timer countdownAlertsTimer = new Timer();
        countdownAlertsTimer.schedule(new ServiceLaunchAlerts(jda), 0, 60000);

        Timer twitterTimer = new Timer();
        twitterTimer.schedule(new ServiceTwitter(jda), 0, 60000);

        Timer redditTimer = new Timer();
        redditTimer.schedule(new ServiceReddit(jda), 0, 60000);
    }

}
