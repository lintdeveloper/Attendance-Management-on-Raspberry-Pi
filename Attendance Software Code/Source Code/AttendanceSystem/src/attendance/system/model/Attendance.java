/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.model;

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
public class Attendance {
    
    private int attendanceTotal;
    private List<String>    allStudentId = new ArrayList<>(), 
                            allFullName = new ArrayList<>(),
                            allMatricNumber = new ArrayList<>(),
                            allCourseId = new ArrayList<>(),
                            allEntryTime = new ArrayList<>();
                         
    private String attendanceId;
    private String studentId;
    private String fullName;
    private String matricNumber;
    private String courseId;
    private String entryTime;
    
    public boolean exists(){
        return this.allEntryTime != null;
    }
    public Attendance(String courseId, String weekNo){
        String sql = "SELECT a.student_id, a.full_name, a.matric_number, b.entry_time, b.course_id,  b.week_number  FROM students a,"
                + ""+DbSqliteConstants.LECTURER_ATTENDANCE_TABLE_NAME+ " b WHERE a.student_id = b.student_id AND b.week_number ='"+weekNo+"' AND b.course_id ='"+courseId+"'";
        
        ResultSet data = DatabaseHandler.getInstance().exeQuery(sql);
        try {
            while(data.next()){
                
                this.attendanceTotal++;
                this.allStudentId.add(data.getString("student_id"));
                this.allFullName.add(data.getString("full_name"));
                this.allMatricNumber.add(data.getString("matric_number"));
                this.allCourseId.add(data.getString("course_id"));
                this.allEntryTime.add(data.getString("entry_time"));
            }
        } catch (SQLException ex) {
            System.err.println("Class: Attendance [attendance.model]");
            Logger.getLogger(LectureAttendance.class.getName()).log(Level.SEVERE, null, ex);
        }  
        
        
    }
    public Attendance(){
        this.getLatestAttendance();
        if(!this.attendanceId.isEmpty()){
            String sql = "SELECT a.student_id, a.full_name, a.matric_number, b.entry_time, b.course_id, b.week_number  FROM students a,"
                + ""+DbSqliteConstants.LECTURER_ATTENDANCE_TABLE_NAME+ " b WHERE a.student_id = b.student_id AND b.attendance_id ='"+this.attendanceId+"'";
        
            ResultSet data = DatabaseHandler.getInstance().exeQuery(sql);
            try {
                while(data.next()){
                    this.studentId = data.getString("student_id");
                    this.fullName = data.getString("full_name");
                    this.matricNumber = data.getString("matric_number");
                    this.courseId = data.getString("course_id");
                    this.entryTime = data.getString("entry_time");
                }
            } catch (SQLException ex) {
                System.err.println("Class: Attendance [attendance.model]");
                Logger.getLogger(LectureAttendance.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        
    }
    
    
    private void getLatestAttendance(){
        String sql = "SELECT attendance_id FROM "+DbSqliteConstants.LECTURER_ATTENDANCE_TABLE_NAME+" ORDER BY id DESC LIMIT 1";
        ResultSet data = DatabaseHandler.getInstance().exeQuery(sql);
        try {
            while(data.next()){
                this.attendanceId = data.getString("attendance_id");
            }
        } catch (SQLException ex) {
            Logger.getLogger(LectureAttendance.class.getName()).log(Level.SEVERE, null, ex);
        }  
        
    }

    public List<String> getAllStudentId() {
        return allStudentId;
    }

    public int getAttendanceTotal() {
        return attendanceTotal;
    }

    public List<String> getAllFullName() {
        return allFullName;
    }

    public List<String> getAllMatricNumber() {
        return allMatricNumber;
    }

    public List<String> getAllCourseId() {
        return allCourseId;
    }

    public List<String> getAllEntryTime() {
        return allEntryTime;
    }
    
    public String getMatricNumber(){
        return this.matricNumber;
    }

    public String getAttendanceId() {
        return attendanceId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getEntryTime() {
        return entryTime;
    }
    
}
