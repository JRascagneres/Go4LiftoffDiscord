package uk.co.rascagneres.spacexbot.LaunchData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Launch {
    public int id;
    public String name;
    public String net;
    public Rocket rocket;
    public int tbdtime;
    public int tbddate;

}