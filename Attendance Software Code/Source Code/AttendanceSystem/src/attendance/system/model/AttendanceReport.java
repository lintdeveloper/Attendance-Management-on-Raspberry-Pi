/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.model;

import abaezcorp.sql.generator.SqlGenerator;
import attendance.system.dbconfig.DatabaseHandler;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Abbasogaji
 */
public class AttendanceReport {
    
    DatabaseHandler db;
    
    private List<String> allPhoneNumber = new ArrayList<>();
    private List<String> allStudentId = new ArrayList<>(), 
            allStudentName = new ArrayList<>(),
            allStudentMatric = new ArrayList<>();
    private Map<String, String> studentsCourseRegs = new HashMap<>();
    private Map<String, String> courseAttendanceStats = new HashMap<>(), studentText = new HashMap<>();
    private int weekNo;
    public AttendanceReport(int weekNo){
            db = DatabaseHandler.getInstance();
            this.weekNo = weekNo;
            getAllStudentPersonalAndCourseData();
            
            //getTextsofAllStudents();
    }
    private void getAllStudentPersonalAndCourseData(){
          SqlGenerator str = new SqlGenerator();
          String studentData = str.table("students").select("student_id, full_name, matric_number, parent_mobile").get();
          ResultSet data = DatabaseHandler.getInstance().exeQuery(studentData);
        try {
            while(data.next()){
                
                allStudentId.add(data.getString("student_id"));
                allPhoneNumber.add(data.getString("parent_mobile"));
                allStudentName.add(data.getString("full_name"));
                allStudentMatric.add(data.getString("matric_number"));
                this.addAllCourseRegDataForStudentInQuestion(data.getString("student_id"));
                courseAttendanceStats.clear();
                studentsCourseRegs.clear();
            }
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
            /*
            for(int x=0; x<allStudentId.size(); x++){
                  //this.addAllCourseRegDataForStudentInQuestion(allStudentId.get(x));
                  System.out.print(allStudentId.get(x));
            }
*/
           
    }
    private void addAllCourseRegDataForStudentInQuestion(String stid){
          String sql = "SELECT course_id FROM STUDENT_COURSE_REG  WHERE student_id = '"+stid+"'"; 
          ResultSet rs = db.exeQuery(sql);
        try {
            while(rs.next()){
                studentsCourseRegs.put(stid, rs.getString("course_id"));
                this.checkIfAttendanceExists(stid, rs.getString("course_id"));
            }
            /*
            for(String st : studentsCourseRegs.values()){
                  this.checkIfAttendanceExists(stid, weekNo, st);      
            }
*/
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void checkIfAttendanceExists(String studentId, String courseCode){
        //First we will check if has been taken for the week taken
        String sql = "SELECT * FROM LECTURE_ATTENDANCE WHERE attendance_id = 'W"+weekNo+studentId+courseCode+"'";
        ResultSet rs = db.exeQuery(sql);
        try {
            if(rs.next()){
               this.courseAttendanceStats.put(courseCode, "PRESENT( "+rs.getString("entry_time")+" )"); 
            }else{
               String sql2 = "SELECT * FROM LECTURE_ATTENDANCE WHERE course_id = '"+courseCode+"' AND week_number = '"+weekNo+"'";
               ResultSet rs2 = db.exeQuery(sql2);
               if(rs2.next()){
                    this.courseAttendanceStats.put(courseCode, "ABSENT");
                    //this.courseAttendanceStats.put(courseCode, "NO LECTURE TAKEN"); 
               }else{
                    //this.courseAttendanceStats.put(courseCode, "ABSENT");
                    this.courseAttendanceStats.put(courseCode, "NO LECTURE TAKEN");
               }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        appendTexts(studentId);
    }
    private void appendTexts(String studentId){
        String text = "LECTURE WEEEK "+ weekNo+" Attendance Report\n See Courses Below\n";
            for(String key: this.courseAttendanceStats.keySet()){
                    text  = text + key+": "+this.courseAttendanceStats.get(key)+"\n";
            }
            studentText.put(studentId, text);
            
    }
    private void getTextsofAllStudents(){
            for(int x = 0 ; x<allStudentId.size(); x++){
                        System.out.println("NAME: "+allStudentName.get(x)+" \n MATRIC: "+allStudentMatric.get(x)+"\n"+studentText.get(allStudentId.get(x)));
            }
    }
    public List<String> getAllPhoneNumber(){
        return this.allPhoneNumber;
    }
    public List<String> getAllStudentId (){
        return this.allStudentId;
    }
    public Map<String, String> getAllStudentText(){
        return this.studentText;
    }
    public List<String> getAllStudentName (){
        return this.allStudentName;
    }
    public List<String> getAllStudentMatric (){
        return this.allStudentMatric;
    }
}
