/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.lecture.ui.courselist;

/**
 *
 * @author Abbasogaji
 */
public class CourseList {
   private String courseCode;
   private String courseTitle;
   private String creditLoad;
   private String size;
   
    public CourseList(String courseCode, String courseTitle, String creditLoad, String size){
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.creditLoad = creditLoad;
        this.size = size;
        
    }
    public CourseList(String courseCode, String courseTitle, String creditLoad){
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.creditLoad = creditLoad;
    }

    public String getCourseCode() {
        return courseCode;
    }


    public String getCourseTitle() {
        return courseTitle;
    }

    public String getCreditLoad() {
        return creditLoad;
    }


    public String getSize() {
        return size;
    }

}
