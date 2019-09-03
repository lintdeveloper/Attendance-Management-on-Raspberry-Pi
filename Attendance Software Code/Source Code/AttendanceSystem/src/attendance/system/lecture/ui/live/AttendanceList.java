/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.lecture.ui.live;

/**
 *
 * @author Abbasogaji
 */
public class AttendanceList {
   private String studentId;
   private String courseId;
   private String matricNumber;
   private String weekNo;
   private String entryTime;
   private String fullName;
   
   
    public AttendanceList(String studentId, String courseId, String fullName, String matricNumber, String entryTime, String weekNo){
        this.studentId = studentId;
        this.courseId = courseId;
        this.fullName = fullName;
        this.matricNumber = matricNumber;
        this.entryTime = entryTime;
        this.weekNo = weekNo;
        
        
    }

    public String getWeekNo() {
        return weekNo;
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

    public String getCourseId() {
        return courseId;
    }

    public String getEntryTime() {
        return entryTime;
    }
   
}
