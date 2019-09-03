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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Abbasogaji
 */
public class LectureAttendance {
   
    public static int getTotalAttendancePerCourse(String cc, String stid){
        SqlGenerator sql = new SqlGenerator();
        String str = sql.table(DbSqliteConstants.LECTURER_ATTENDANCE_TABLE_NAME).select("COUNT(*) AS total").whereEqual("student_id", stid).andWhereEqual("course_id", cc).get();
        ResultSet data = DatabaseHandler.getInstance().exeQuery(str);
        int total = 0;
        try {
            while(data.next()){
                total = data.getInt("total");
            }
        } catch (SQLException ex) {
            Logger.getLogger(LectureAttendance.class.getName()).log(Level.SEVERE, null, ex);
        }  
        
        return total;
    }
    public static int getCourseWeekAttendanceStatus(String stid, String cc, int weekNo){
        SqlGenerator sql = new SqlGenerator();
        String str = sql.table(DbSqliteConstants.LECTURER_ATTENDANCE_TABLE_NAME).select().whereEqual("student_id", stid).andWhereEqual("course_id", cc).andWhereEqual("week_number", Integer.toString(weekNo)).get();
        ResultSet data = DatabaseHandler.getInstance().exeQuery(str);
        try {
            if(data.next()){
                return 1;
            }
        } catch (SQLException ex) {
            return 0;
           // Logger.getLogger(LectureAttendance.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return 0;
    }
    
}
