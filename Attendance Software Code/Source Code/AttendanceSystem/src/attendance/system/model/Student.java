/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.model;

import abaezcorp.sql.generator.SqlGenerator;
import attendance.system.dbconfig.DatabaseHandler;
import attendance.system.dbconfig.DbSqliteConstants;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;

/**
 *
 * @author Abbasogaji
 */
public class Student {
    
    DatabaseHandler db = DatabaseHandler.getInstance();
    private List<String> allStudentId = new ArrayList(), allFullName = new ArrayList(), allMatricNumber = new ArrayList(), allDepartment = new ArrayList(), allLevel = new ArrayList();
    private String studentId, fullName, matricNumber, department, level;
    private int studentTotal;
    
    public boolean exists(){
        return studentId != null;   
    }
    
    public void find(String stdId){
        SqlGenerator sql = new SqlGenerator();
        String str = sql.table(DbSqliteConstants.STUDENTS_TABLE_NAME).select().whereEqual("student_id", stdId).get();
        ResultSet data = db.exeQuery(str);
        
        try {
            while(data.next()){
               this.studentId = data.getString("student_id");
               this.fullName = data.getString("full_name");
               this.matricNumber = data.getString("matric_number");
               this.department = data.getString("department");
               this.level = data.getString("level");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Courses.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
    public static int getNoOfAttendance(String cid){
        
        int total = 0;/*
        SqlGenerator sql = new SqlGenerator();
        String str = sql.table(DbSqliteConstants.STUDENT_REG_TABLE_NAME).select("COUNT(course_id) AS total").whereEqual("course_id", cid).get();
        ResultSet data = DatabaseHandler.getInstance().exeQuery(str);
        
        try {
            while(data.next()){
               total = data.getInt("total");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Courses.class.getName()).log(Level.SEVERE, null, ex);
        }  */     
    return total;
    }
    public void all(){
        SqlGenerator sql = new SqlGenerator();
        String str = sql.table(DbSqliteConstants.STUDENTS_TABLE_NAME).select().get();
        ResultSet data = db.exeQuery(str);
        try {
            while(data.next()){
               this.studentTotal++;
               this.allStudentId.add(data.getString("student_id"));
               this.allFullName.add(data.getString("full_name"));
               this.allMatricNumber.add(data.getString("matric_number"));
               this.allDepartment.add(data.getString("department"));
               this.allLevel.add(Integer.toString(data.getInt("level")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Courses.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<String> getAllStudentId() {
        return allStudentId;
    }

    public List<String> getAllFullName() {
        return allFullName;
    }

    public List<String> getAllMatricNumber() {
        return allMatricNumber;
    }

    public List<String> getAllDepartment() {
        return allDepartment;
    }

    public List<String> getAllLevel() {
        return allLevel;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getMatricNumber() {
        return matricNumber;
    }

    public String getDepartment() {
        return department;
    }

    public String getLevel() {
        return level;
    }

    public int getStudentTotal() {
        return studentTotal;
    }
    public Image getPic(){
        InputStream is = null; OutputStream os;
        Image img = null;
        SqlGenerator sql = new SqlGenerator();
        String str = sql.table(DbSqliteConstants.STUDENTS_TABLE_NAME).select("pic").whereEqual("student_id", this.getStudentId()).get();
        ResultSet data = DatabaseHandler.getInstance().exeQuery(str); 
        
        try{
            while(data.next()){
                is = data.getBinaryStream("pic");  
            }
            
            os = new FileOutputStream(new File("student.jpg"));
            byte[] content = new byte[1024];
            int size = 0;
            while((size = is.read(content)) != -1){
                            os.write(content, 0, size);
                        }
            os.close();
            is.close();
            img = new Image("file:student.jpg", 150, 150, true, true);
                    
        }catch(SQLException ex){
            Logger.getLogger(Lecturer.class.getName()).log(Level.SEVERE, null, ex);
        }catch(IOException ex){
            Logger.getLogger(Lecturer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return img;
    }
    
    
}
