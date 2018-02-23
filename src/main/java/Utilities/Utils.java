package Utilities;

public class Utils {

    public static int getNthIndex(String inputString, char searchChar, int n){
        int counter = 0;
        for(int i = 0; i < inputString.length(); i++){
            if(inputString.charAt(i) == searchChar){
                counter++;
            }

            if(counter == n){
                return i;
            }
        }

        return -1;
    }

}
