/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.model;

import abaezcorp.sql.generator.SqlGenerator;
import attendance.system.dbconfig.DatabaseHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;

/**
 *
 * @author Abbasogaji
 */
public class Lecturer {
    private static Lecturer lec;
    private static String lecturertitle, username, fullName, email, phone, department;
    
    public static Lecturer getInstance(String username){
        if(lec == null){
            lec = new Lecturer(username);
        }
        return lec;
    }
    public Lecturer(String username){
        SqlGenerator sql = new SqlGenerator();
            String str = sql.table("LECTURER").select().whereEqual("lecturer_id", username).get();
            ResultSet data = DatabaseHandler.getInstance().exeQuery(str);
        try {
            while(data.next()){
                Lecturer.lecturertitle = data.getString("lecturer_title");
                Lecturer.username = data.getString("lecturer_id");
                Lecturer.fullName = data.getString("full_name");
                Lecturer.email = data.getString("email");
                Lecturer.phone = data.getString("phone");
                Lecturer.department = data.getString("department");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Lecturer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Image getPic(){
        InputStream is = null; OutputStream os;
        Image img = null;
        SqlGenerator sql = new SqlGenerator();
        String str = sql.table("LECTURER").select("pic").whereEqual("lecturer_id", username).get();
        ResultSet data = DatabaseHandler.getInstance().exeQuery(str); 
        
        try{
            while(data.next()){
                is = data.getBinaryStream("pic");  
            }
            
            os = new FileOutputStream(new File("lecturer.jpg"));
            byte[] content = new byte[1024];
            int size = 0;
            while((size = is.read(content)) != -1){
                            os.write(content, 0, size);
                        }
            os.close();
            is.close();
            img = new Image("file:lecturer.jpg", 150, 150, true, true);
                    
        }catch(SQLException ex){
            Logger.getLogger(Lecturer.class.getName()).log(Level.SEVERE, null, ex);
        }catch(IOException ex){
            Logger.getLogger(Lecturer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return img;
    }
    
    public static String getTitle() {
        return lecturertitle;
    }

    public static void setLecturerTitle(String title) {
        Lecturer.lecturertitle = title;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Lecturer.username = username;
    }

    public static String getFullName() {
        return fullName;
    }

    public static void setFullName(String fullName) {
        Lecturer.fullName = fullName;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        Lecturer.email = email;
    }

    public static String getPhone() {
        return phone;
    }

    public static void setPhone(String phone) {
        Lecturer.phone = phone;
    }

    public static String getDepartment() {
        return department;
    }

    public static void setDepartment(String department) {
        Lecturer.department = department;
    }
    
    
    
    
    
}
