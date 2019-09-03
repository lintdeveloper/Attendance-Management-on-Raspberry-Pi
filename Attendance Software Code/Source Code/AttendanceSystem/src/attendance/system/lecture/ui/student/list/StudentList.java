/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.lecture.ui.student.list;



/**
 *
 * @author Abbasogaji
 */
public class StudentList {
   private String studentId;
   private String fullName;
   private String matricNumber;
   private String department;
   private String level;
   
   
    public StudentList(String studentId, String fullName, String matricNumber, String department, String level){
        this.studentId = studentId;
        this.fullName = fullName;
        this.matricNumber = matricNumber;
        this.department = department;
        this.level = level;
        
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
   
}
