package Launches;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

public class LaunchesReader {

    private String getJSONFromURL(String url) throws Exception{
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null){
            response.append(inputLine);
        }

        in.close();
        return response.toString();
    }

    public LibraryObject getLaunches(int amount){
        LibraryObject libraryObject = new LibraryObject();
        String launchLibraryURL = "https://launchlibrary.net/1.3/launch/next/";

        try {
         libraryObject = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(getJSONFromURL(launchLibraryURL + amount), LibraryObject.class);
        }catch (Exception e){
            System.out.println("ERROR GETTING LAUNCHES");
        }

        if (libraryObject != null){
            for(int i = 0; i < libraryObject.launches.size(); i++){
                String status = "";
                switch (libraryObject.launches.get(i).status){
                    case 1:
                        status = "Green";
                        break;
                    case 2:
                        status = "Red";
                        break;
                    case 3:
                        status = "Success";
                        break;
                    case 4:
                        status = "Failed";
                        break;
                }
                
                libraryObject.launches.get(i).statusText = status;
                libraryObject.launches.get(i).padName = libraryObject.launches.get(i).location.pads.get(0).name;
                libraryObject.launches.get(i).timeToLaunchData = getTimeToLaunchData(libraryObject.launches.get(i));
            }
            return  libraryObject;
        }
        return null;
    }

    public LaunchObject getNextLaunch(){
        int launchCount = 0;
        LaunchObject currentLaunch = getLaunches(launchCount + 1).launches.get(launchCount);
        while (currentLaunch.status != 1){
            launchCount++;
            currentLaunch = getLaunches(launchCount + 1).launches.get(launchCount);
        }
        return currentLaunch;
    }

    public List<String> getTimeToLaunchData(LaunchObject launchObject){
        Date date = new Date();
        Instant now = ZonedDateTime.now(ZoneOffset.UTC).toInstant();

        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = simpleDateFormat.parse(launchObject.net);
        }catch (Exception e){
            System.out.println("FAILED PARSE");
        }

        Duration timeToLaunch = Duration.between(now, date.toInstant());
        Long days = timeToLaunch.toDays();
        Long hours = timeToLaunch.toHours() - days * 24;
        Long minutes = timeToLaunch.toMinutes() - days * 24 * 60 - hours * 60;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        List<String> launchTimeData = new LinkedList<>();
        launchTimeData.add(simpleDateFormat.format(date));

        launchTimeData.add("**Launch In: **" + days + " Days " + hours + " hours " + minutes + " minutes");

        launchTimeData.add(String.valueOf(days));
        launchTimeData.add(String.valueOf(hours));
        launchTimeData.add(String.valueOf(minutes));

        return launchTimeData;
    }
}
