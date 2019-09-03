/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.tools;

import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONObject;

/**
 *
 * @author Abbasogaji
 */
public class JsonFile {
    
    public static boolean create(String path_filename, JSONObject obj){
        FileWriter file;
        try{
            file = new FileWriter(path_filename+".json");
            file.write(obj.toJSONString().trim().concat("\n"));
            
            file.flush();
            file.close();
            return true; 
        }
        catch(IOException e){
            return false;
        }
    }
    
}
