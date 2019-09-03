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
public class DefaultersList {
 DatabaseHandler db;
    

    private List<String> allStudentName = new ArrayList<>(), allStudentId = new ArrayList<>(), allStudentMatric = new ArrayList<>();
    private Map<String, Integer> totalStudentAttendance = new HashMap<>();
    private Map<String, Double> percentagePerStudent = new HashMap<>();
    private Map<String, String> examStatus = new HashMap<>();
    private static String cid;
    private static int total_weeks;
    public DefaultersList(String cid){
            
            db = DatabaseHandler.getInstance();
            this.cid = cid;
            setTotalCourseWeek();
            getTotalStudentsOfferingCourse();
    }
    private void setTotalCourseWeek(){
        SqlGenerator str = new SqlGenerator();
        String sql = str.table("LECTURER_COURSES").select("no_of_weeks").whereEqual("course_id", cid).get();
        ResultSet data = DatabaseHandler.getInstance().exeQuery(sql);
        try {
            while(data.next()){
                total_weeks = data.getInt("no_of_weeks");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DefaultersList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static boolean checkIfCourseCompleted(){
        SqlGenerator str = new SqlGenerator();
        String sql = str.table("LECTURE_ATTENDANCE").select("course_id").whereEqual("course_id", cid).andWhereEqual("week_number", Integer.toString(total_weeks)).get();
        try {
            if(DatabaseHandler.getInstance().exeQuery(sql).next()){
                return true;
            }else{
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DefaultersList.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    private void getTotalStudentsOfferingCourse(){
        SqlGenerator str = new SqlGenerator();
        
        String sql  = "SELECT DISTINCT a.full_name, a.matric_number, b.student_id FROM students a, STUDENT_COURSE_REG b WHERE b.course_id = '"+cid+"' AND a.student_id = b.student_id";
        ResultSet data = db.exeQuery(sql);
        try {
            while(data.next()){
                //STORE INTO A LIST PERHAPS
                System.out.println(data.getString("full_name"));
                this.allStudentName.add(data.getString("full_name"));
                this.allStudentMatric.add(data.getString("matric_number"));
                this.allStudentId.add(data.getString("student_id"));
                
                //this.getStudentTotalAttendanceForCourse(data.getString("student_id"));
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(DefaultersList.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for(int i=0; i<this.getAllStudentId().size();i++){
            this.getStudentTotalAttendanceForCourse(this.getAllStudentId().get(i));
        }

    }
    private void getStudentTotalAttendanceForCourse(String stid){
       
        SqlGenerator str = new SqlGenerator();
        String sql = str.table("LECTURE_ATTENDANCE").select("count(*) as total").whereEqual("student_id", stid).andWhereEqual("course_id", cid).get();
        ResultSet rs = db.exeQuery(sql);

        //SELECT FROM LECTURE_ATTENDANCE WHERE student_id = '' and course_id ='';
        try{
            while(rs.next()){
               totalStudentAttendance.put(stid, rs.getInt("total")); 
            }
        
        }catch (SQLException ex) {
            Logger.getLogger(DefaultersList.class.getName()).log(Level.SEVERE, null, ex);
        }
       this.getPerStudentPercentage(stid);
       this.setExamStatus(stid);
        
        
    
    }
    private void getPerStudentPercentage(String stid){
        double percentage = ((double)totalStudentAttendance.get(stid) / (double)total_weeks) * 100;
        percentagePerStudent.put(stid, percentage);
    
    }
    private void setExamStatus(String stid){
         if(percentagePerStudent.get(stid)>=70){
             examStatus.put(stid, "IGS");
         }else{
             examStatus.put(stid, "DEF");
         }
    }
    public List<String> getAllStudentName() {
        return allStudentName;
    }

    public List<String> getAllStudentId() {
        return allStudentId;
    }

    public List<String> getAllStudentMatric() {
        return allStudentMatric;
    }

    public Map<String, Double> getPercentagePerStudent() {
        return percentagePerStudent;
    }

    public Map<String, String> getExamStatus() {
        return examStatus;
    }
}
