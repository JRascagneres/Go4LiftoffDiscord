package uk.co.rascagneres.spacexbot.Config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommandReader {
    Commands commands = null;

    public CommandReader(){
        try{
            commands = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new FileReader("commands.json"), Commands.class);
        }catch (Exception e){
            e.printStackTrace();
            commands = new Commands();
        }
    }

    public Map<String, List<Command>> getCommandsMap(){
        return commands.commandsMap;
    }

//    public void addCommand(){
//        Command command1 = new Command();
//        command1.name = "name";
//        command1.command = "command";
//        command1.parameters = "param";
//        command1.description = "desc";
//        command1.commandAlias = "alias";
//
//        List<Command> list = new LinkedList<>();
//        list.add(command1);
//        list.add(command1);
//        commands.commandsMap = new HashMap<>();
//        commands.commandsMap.put("Cat1", list);
//        saveJSONFile();
//    }
//
//    public void saveJSONFile(){
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            mapper.writeValue(new File("commands.json"), commands);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }
}
