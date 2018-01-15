package Commands;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileReader;
import java.util.List;
import java.util.Map;

public class CommandReader {
    CommandsObject commands = null;

    public CommandReader(){
        try{
            commands = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new FileReader("commands.json"), CommandsObject.class);
        }catch (Exception e){
            e.printStackTrace();
            commands = new CommandsObject();
        }
    }

    public List<Map<String, List<CommandObject>>> getCommands(){
        return commands.commandsMap;
    }
}
