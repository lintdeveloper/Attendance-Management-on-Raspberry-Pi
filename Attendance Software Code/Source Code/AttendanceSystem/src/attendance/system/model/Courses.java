/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.model;

import abaezcorp.sql.generator.SqlGenerator;
import attendance.system.dbconfig.DatabaseHandler;
import attendance.system.dbconfig.DbSqliteConstants;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Abbasogaji
 */
public class Courses {
    
    DatabaseHandler db = DatabaseHandler.getInstance();
    private List<String> allCourseCode = new ArrayList(), allDepartment = new ArrayList(), allCourseTitle = new ArrayList(), allCreditLoad = new ArrayList(), allNoOfWeeks = new ArrayList(), allLecturerId = new ArrayList();
    private String courseCode, department, courseTitle, creditLoad, noOfWeeks, lecturerId;
    private String noOfStudentTakingCourse = "23";
    private int courseTotal;
    
    
    public boolean exists(){
        return courseCode != null;
        
    }
    public Courses findWeekOnly(String cc){
        SqlGenerator sql = new SqlGenerator();
        String str = sql.table(DbSqliteConstants.LECTURER_COURSES_TABLE_NAME).select("no_of_weeks").whereEqual("course_id", cc).get();
        ResultSet data = db.exeQuery(str);
        
        try {
            while(data.next()){
               this.noOfWeeks = data.getString("no_of_weeks");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Courses.class.getName()).log(Level.SEVERE, null, ex);
        }  
        return this;
    }
    public void find(String cc){
        SqlGenerator sql = new SqlGenerator();
        String str = sql.table(DbSqliteConstants.LECTURER_COURSES_TABLE_NAME).select().whereEqual("course_id", cc).get();
        ResultSet data = db.exeQuery(str);
        
        try {
            while(data.next()){
               this.courseCode = data.getString("course_id");
               this.lecturerId = data.getString("lecturer_id");
               this.courseTitle = data.getString("title");
               this.creditLoad = Integer.toString(data.getInt("credit_load"));
               this.department = data.getString("department");
               this.noOfWeeks = Integer.toString(data.getInt("no_of_weeks"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Courses.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
    public void all(){
        SqlGenerator sql = new SqlGenerator();
        String str = sql.table(DbSqliteConstants.LECTURER_COURSES_TABLE_NAME).select().get();
        ResultSet data = db.exeQuery(str);
        
        try {
            while(data.next()){
               this.courseTotal++;
               this.allCourseCode.add(data.getString("course_id"));
               this.allLecturerId.add(data.getString("lecturer_id"));
               this.allCourseTitle.add(data.getString("title"));
               this.allCreditLoad.add(Integer.toString(data.getInt("credit_load")));
               this.allDepartment.add(data.getString("department"));
               this.allNoOfWeeks.add(Integer.toString(data.getInt("no_of_weeks")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Courses.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getCourseTotal() {
        return courseTotal;
    }

    public List<String> getAllCourseCode() {
        return allCourseCode;
    }

    public List<String> getAllDepartment() {
        return allDepartment;
    }

    public List<String> getAllCourseTitle() {
        return allCourseTitle;
    }

    public List<String> getAllCreditLoad() {
        return allCreditLoad;
    }

    public List<String> getAllNoOfWeeks() {
        return allNoOfWeeks;
    }

    public List<String> getAllLecturerId() {
        return allLecturerId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getDepartment() {
        return department;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getCreditLoad() {
        return creditLoad;
    }

    public String getNoOfWeeks() {
        return noOfWeeks;
    }

    public String getLecturerId() {
        return lecturerId;
    }

    public String getNoOfStudentTakingCourse() {
        return noOfStudentTakingCourse;
    }
}
