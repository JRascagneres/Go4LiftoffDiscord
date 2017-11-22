package uk.co.rascagneres.spacexbot.Utilities;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dean.jraw.models.WikiPageSettings;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import uk.co.rascagneres.spacexbot.Config.PermissionLevel;
import uk.co.rascagneres.spacexbot.LaunchData.Launch;
import uk.co.rascagneres.spacexbot.LaunchData.LaunchLibrary;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import static javafx.scene.input.KeyCode.L;

public class Utils {
    private static String getText(String url) throws Exception{
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();
        return response.toString();
    }

    public static LaunchLibrary getLaunches(int amount) {
        try {
            LaunchLibrary launchLibrary = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(getText("https://launchlibrary.net/1.3/launch/next/" + amount), LaunchLibrary.class);
            return launchLibrary;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Launch getNextLaunch(){
        return getLaunches(1).launches.get(0);
    }

    public static PermissionLevel PermissionResolver(Member member, Channel channel){

        if(member.getUser().getId().equals(String.valueOf(150768477152477186L))){
            return PermissionLevel.BotOwner;
        }

        if(member.getUser().equals(channel.getGuild().getOwner().getUser())){
            return PermissionLevel.ServerOwner;
        }

        List<Permission> serverPerms = member.getPermissions(channel);

        if(serverPerms.contains(Permission.MANAGE_ROLES)){
            return PermissionLevel.ServerAdmin;
        }
        if(serverPerms.contains(Permission.MESSAGE_MANAGE) && serverPerms.contains(Permission.KICK_MEMBERS) && serverPerms.contains(Permission.BAN_MEMBERS)){
            return PermissionLevel.ServerModerator;
        }

        if(member.getRoles().stream().map(Role::getName).anyMatch(name -> name.equalsIgnoreCase("Bot Manager"))){
            return PermissionLevel.BotManager;
        }

        return PermissionLevel.User;
    }
}
