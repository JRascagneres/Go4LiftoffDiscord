package uk.co.rascagneres.spacexbot.Utilities;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.co.rascagneres.spacexbot.LaunchData.Launch;
import uk.co.rascagneres.spacexbot.LaunchData.LaunchLibrary;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

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
}
