package uk.co.rascagneres.spacexbot.LaunchData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

public class Launch {
    public int id;
    public String name;
    public String net;
    public Rocket rocket;
    public int tbdtime;
    public int tbddate;
    public List<String> vidURLs;
    public int status;
    public String statusText;
    public Location location;
    public String padName;

}