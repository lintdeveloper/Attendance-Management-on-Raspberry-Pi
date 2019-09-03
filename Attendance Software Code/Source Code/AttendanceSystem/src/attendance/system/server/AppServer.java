/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;

/**
 *
 * @author Abbasogaji
 */
public class AppServer {
    private static AppServer appserver;
    //private static ServerSocket server;
    private static Socket connection;
    private static volatile boolean isConnected = false, isAttendanceAvailable = false, isReportAvailable = false;
    private static volatile String fingerprintdetails = ""; 
    //private static volatile String fingerprintdetails; 
    private static volatile String attendanceQuery;
    private static volatile String reportQuery;
    
    public AppServer(){
           startRunning(); 
    }
    public static AppServer getInstance(){
        if(appserver == null){
            appserver = new AppServer();
        }
        return appserver;
    
    }
    private void startRunning(){
        try{
            //server = new ServerSocket(6789,1);
            connection = null;
            while(true){
                try{
                    //connection = new Socket("169.254.200.40", 6789); - This is my Raspberry pi ethernet (wired) network IP
                    connection = new Socket("192.168.4.1", 6789); //Connecting to my Raspberry pi wi-fi ip address
                    waitingForConnection();
                    if(connection != null){
                        break;
                    }
                }catch(IOException e){
                    Thread.sleep(1000);
                }
            
            }
        
        }catch(Exception e){
        
        }
    
    }
    
    private void waitingForConnection() throws IOException{
        //connection = server.accept();
        if(connection.isConnected()){
            //System.out.println("Client has been connected");
            isConnected = true;
            
        }
    
    }
    public synchronized static boolean isConnectedToClient(){
        return isConnected;
    }
    public static void closeAllConnections(){
        try {
            connection.close();
            //server.close();
        } catch (IOException ex) {
            Logger.getLogger(AppServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public static void sendJsonFile(String filename){
        try {
            FileInputStream fs = new FileInputStream(filename);
            //new addition on 04 september 2018
            File file = new File(filename);
            int i = (int)file.length();
            System.out.println(i);
            byte b[] = new byte[i];
            fs.read(b, 0, b.length);
            fs.close();
            OutputStream os = connection.getOutputStream();
            os.write(b, 0, b.length);
            os.flush();
        
        } catch (IOException ex) {
            System.err.println("JSON file not found"+ ex.getLocalizedMessage());
        } catch(NullPointerException np){
        
        }
    }
    public static void recieveFingerprint(Node display){
        try {
            
             BufferedReader stdLn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             fingerprintdetails = stdLn.readLine();
            
        } catch (Exception ex) {
            System.err.println("xccddc"+ ex.getLocalizedMessage());
        }
 
    }
    //RECIEVE LIVE ATTENDANCE IS USED FOR RECIEVING ATTENDANCE DATA IN REAL-TIME
    public static void recieveLiveAttendance(){
        
            try{
                BufferedReader stdLn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                isAttendanceAvailable = stdLn.ready();
                attendanceQuery = Command.decode(stdLn.readLine(), Command.RTATR);
                //stdLn.close();

            }catch(IOException | NullPointerException e){
                //e.printStackTrace();

            }
        
    }
    //RECIEVE ATTENDANCE REPORT IS USED FOR RECIEVEIOING ATTERNDANCE OF STORED ATTENDANCE DATA
    public static void recieveAttendanceReport(){
        try{
            BufferedReader stdLn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            isReportAvailable = stdLn.ready();
            reportQuery = Command.decode(stdLn.readLine(), Command.RECVATR);
            //stdLn.close();

        }catch(IOException | NullPointerException e){

        }    
    }
    public synchronized static boolean isAttendanceAvailable(){
        return attendanceQuery != null;
    }
    public synchronized static boolean checkfingerprintExists(){
        return !fingerprintdetails.isEmpty();
        //return fingerprintdetails != null;
    }
    public synchronized static String getFingerprintDetails(){
               return fingerprintdetails;
    }
    public synchronized static String getAttendanceQuery(){
               return attendanceQuery;
    }
    public synchronized static boolean isReportAvailable(){
        return reportQuery != null;
            
    }
    public synchronized static String getReportQuery(){
               return reportQuery;
    }
}
