package uk.co.rascagneres.spacexbot.Config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by jacqu on 10/11/2017.
 */
public class Config {
    public String token;
    public String prefix ;
    public Map<String, List<Long>> redditChannelIDs = new HashMap<>();
    public Map<String, String> redditData = new HashMap<>();

}
