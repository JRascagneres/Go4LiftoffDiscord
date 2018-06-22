package Launches;

import java.util.List;

public class LaunchObject {
    //Default
    public int id;
    public String name;
    public String net;
    public RocketObject rocket;
    public int tbdtime;
    public int tbddate;
    public List<String> vidURLs;
    public int status;
    public LocationObject location;
    public String padName;
    public AgencyObject lsp;

    //Added
    public String statusText;
    public List<String> timeToLaunchData;
}
