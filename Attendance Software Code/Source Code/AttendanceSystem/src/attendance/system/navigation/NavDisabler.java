/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.navigation;

import attendance.system.server.AppServer;
import javafx.scene.Node;

/**
 *
 * @author Abbasogaji
 */


public class NavDisabler {
    
    public static void disableIfNotConnectedToATDevice(Node node){
        if(!AppServer.isConnectedToClient()){
            node.setDisable(true);
        }
        
    }
    
}
