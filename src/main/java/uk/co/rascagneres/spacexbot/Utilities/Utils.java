package uk.co.rascagneres.spacexbot.Utilities;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.models.WikiPageSettings;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import uk.co.rascagneres.spacexbot.Config.ConfigReader;
import uk.co.rascagneres.spacexbot.Config.PermissionLevel;
import uk.co.rascagneres.spacexbot.LaunchData.Launch;
import uk.co.rascagneres.spacexbot.LaunchData.LaunchLibrary;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

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

    public static boolean checkSubredditExists(String subreddit){
        UserAgent userAgent = UserAgent.of("desktop", "uk.co.rascagneres.spacexbot", "v1.0", "Scorp1579");
        RedditClient redditClient = new RedditClient(userAgent);
        Map<String, String> redditData = new ConfigReader().getRedditData();
        Credentials credentials = Credentials.script(redditData.get("username"), redditData.get("password"), redditData.get("clientid"), redditData.get("clientsecret"));
        OAuthData authData = null;
        try {
            authData = redditClient.getOAuthHelper().easyAuth(credentials);

        }catch(Exception e){
            e.printStackTrace();
        }
        redditClient.authenticate(authData);

        try {
            redditClient.getSubreddit(subreddit);
        }catch (Exception ex){
            return false;
        }
        return true;
    }

    public static boolean checkTwitterExists(String twitterUser){
        ConfigReader configReader = new ConfigReader();
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder
                .setDebugEnabled(true)
                .setOAuthConsumerKey(configReader.getTwitterData().get("consumerkey"))
                .setOAuthConsumerSecret(configReader.getTwitterData().get("consumersecret"))
                .setOAuthAccessToken(configReader.getTwitterData().get("accesstoken"))
                .setOAuthAccessTokenSecret(configReader.getTwitterData().get("accesstokensecret"))
                .setTweetModeExtended(true);

        TwitterFactory twitterFactory = new TwitterFactory(configurationBuilder.build());
        Twitter twitter = twitterFactory.getInstance();
        boolean userExists = true;
        try {
            twitter.showUser(twitterUser);
        }catch (Exception e){
            userExists = false;
        }

        return userExists;
    }
}
