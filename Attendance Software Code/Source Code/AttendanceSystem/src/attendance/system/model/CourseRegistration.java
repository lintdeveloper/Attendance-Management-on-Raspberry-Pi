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
public class CourseRegistration {

    private List<String> courseCode = new ArrayList(); List<Integer> courseDuration = new ArrayList();
    private int courseRegTotal;
    
    public void find(String stid){
        
        SqlGenerator sql = new SqlGenerator();
        String str = sql.table(DbSqliteConstants.STUDENT_REG_TABLE_NAME).select().whereEqual("student_id", stid).get();
        ResultSet data = DatabaseHandler.getInstance().exeQuery(str);
        Courses cs = new Courses();
        
        try {
            while(data.next()){
                this.courseRegTotal++;
                this.courseCode.add(data.getString("course_id"));
                int nowk = Integer.parseInt(cs.findWeekOnly(data.getString("course_id")).getNoOfWeeks());
                this.courseDuration.add(nowk);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CourseRegistration.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
    public List<String> getCourseCode() {
        return courseCode;
    }

    public List<Integer> getCourseDuration() {
        return courseDuration;
    }
    
    public int getCourseRegTotal() {
        return courseRegTotal;
    }

    public static int getNoOfStudent(String cid){
        int total = 0;
        SqlGenerator sql = new SqlGenerator();
        String str = sql.table(DbSqliteConstants.STUDENT_REG_TABLE_NAME).select("COUNT(course_id) AS total").whereEqual("course_id", cid).get();
        ResultSet data = DatabaseHandler.getInstance().exeQuery(str);
        
        try {
            while(data.next()){
               total = data.getInt("total");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Courses.class.getName()).log(Level.SEVERE, null, ex);
        }       
    return total;
    }

}
