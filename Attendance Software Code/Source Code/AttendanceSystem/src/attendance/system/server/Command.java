/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.server;

/**
 *
 * @author Abbasogaji
 */
public class Command {
    public static final String LATR = "LATR---";
    public static final String RECVATR = "RECVATR---";
    public static final String RTATR = "RTATR---";
    public static final String RECVFP = "RECVFP---";
    public static final String SHUTDOWN = "SHUTDOWNPI---";
    
    
    public static String decode(String str, String cmd){
        String command = str.contains(cmd) ? str.substring(cmd.length()): "error";
        return command;
    }
    
    public static boolean isLegit(String str){
        return (str.contains(LATR) || str.contains(RECVATR) || str.contains(RECVFP)) && str.length() > 6;
    }
    
    
    
}
