/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Abbasogaji
 */
public class Configs {

    public static void createDirectory(){
        JFileChooser fr = new JFileChooser();
        FileSystemView fw = fr.getFileSystemView();
        String path = fw.getDefaultDirectory().getPath();
        File dir = new File(path+"\\FUTMINNA E-ATTENDANCE DOCS\\ATTENDANCE RECORD");
        File dirTwo = new File(path+"\\FUTMINNA E-ATTENDANCE DOCS\\EXAM SCREENING AND DEFAULERS LIST");
       
      
        if(!dir.exists()){
                dir.mkdirs();
        }
        if(!dirTwo.exists()){
                dirTwo.mkdirs();
        }
    }
    public static String getPathToAttendanceRecord(){
        JFileChooser fr = new JFileChooser();
        FileSystemView fw = fr.getFileSystemView();
        String path = fw.getDefaultDirectory().getPath();
        return path+"\\FUTMINNA E-ATTENDANCE DOCS\\ATTENDANCE RECORD";
    }
    public static String getPathToExamDefaultersList(){
        JFileChooser fr = new JFileChooser();
        FileSystemView fw = fr.getFileSystemView();
        String path = fw.getDefaultDirectory().getPath();
        return path+"\\FUTMINNA E-ATTENDANCE DOCS\\EXAM SCREENING AND DEFAULERS LIST";
    }
    
    public static boolean storeExcel(String name, XSSFWorkbook wb){
        boolean stored = false;
        FileOutputStream fileOut;
        String path = Configs.getPathToAttendanceRecord()+"\\"+name;
        System.out.println(path);
        try {
            fileOut = new FileOutputStream(Configs.getPathToAttendanceRecord()+"\\"+name);
            wb.write(fileOut);
            fileOut.close();
            stored = true;
        } catch (IOException ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            stored = false;
        }
          return stored;  
    }
    public static boolean storeDefaulters(String name, XSSFWorkbook wb){
        boolean stored = false;
        FileOutputStream fileOut;
        String path = Configs.getPathToExamDefaultersList()+"\\"+name;
        System.out.println(path);
        try {
            fileOut = new FileOutputStream(Configs.getPathToExamDefaultersList()+"\\"+name);
            wb.write(fileOut);
            fileOut.close();
            stored = true;
        } catch (IOException ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            stored = false;
        }
          return stored;  
    }
    
}
