/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.lecture.ui.student.view;

import attendance.system.model.CourseRegistration;
import attendance.system.model.Courses;
import attendance.system.model.LectureAttendance;
import attendance.system.model.Student;
import attendance.system.navigation.LectureModeNavigation;
import attendance.system.server.AppServer;
import attendance.system.tools.AlertBox;
import attendance.system.tools.FadeAnimation;
import attendance.system.tools.InputData;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Abbasogaji
 */
public class VstudentController implements Initializable {

    @FXML
    private AnchorPane mainPane;
    @FXML
    private AnchorPane activityPane;
    @FXML
    private JFXTextField studentId;
    @FXML
    private NumberAxis percentAttendance;
    @FXML
    private CategoryAxis courseTaken;
   
    private static List<String> errorList = new ArrayList();
    String errormsg = "";
    private static boolean courseLoaded = false;
    
    @FXML
    private ImageView studPic;
    @FXML
    private Label studentName;
    @FXML
    private Label dept;
    @FXML
    private Label matric;
    @FXML
    private Label level;
    @FXML
    private AnchorPane studentPanel;
    @FXML
    private LineChart<String, Integer> lineChart;
    @FXML
    private JFXComboBox<String> courseId;
    /**
     * Initialises the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        studentPanel.setVisible(false);
        animateActivityPane();
        courseId.setOnAction(e -> findStudent(studentId.getText().trim().toUpperCase(), false));
    }    

    @FXML
    private void handleLoadDashboardHomePage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LectureModeNavigation.HOME_PAGE));
            AnchorPane homepage = loader.load();
            mainPane.getChildren().setAll(homepage);
        } catch (IOException e) {
        }
    }
    @FXML
    private void handleLoadStudentPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LectureModeNavigation.STUDENT_PORTAL_PAGE));
            AnchorPane homepage = loader.load();
            mainPane.getChildren().setAll(homepage);
        } catch (IOException e) {
        }

    }
    @FXML
    private void handleLoadCourseListPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LectureModeNavigation.COURSE_LIST_PAGE));
            AnchorPane homepage = loader.load();
            mainPane.getChildren().setAll(homepage);
        } catch (IOException e) {
        }
    }
    @FXML
    private void handleLoadAddCoursePage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LectureModeNavigation.ADD_COURSE_PAGE));
            AnchorPane homepage = loader.load();
            mainPane.getChildren().setAll(homepage);
        } catch (IOException e) {
        }
    }
    
    @FXML
    private void handleLoadSendReportPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LectureModeNavigation.REPORT_PORTAL_PAGE));
            AnchorPane homepage = loader.load();
            mainPane.getChildren().setAll(homepage);
        } catch (IOException e) {
        }
    }
    @FXML
    private void handleSearchStudentAction(ActionEvent event) {
        String std = studentId.getText().trim().toUpperCase();
        emptyErrorData();
        if(validateUserInput()){
            if(!findStudent(std,true)){
                AlertBox.createErrorAlert("Whoops!, No Match Found!");
            }
        }
        
    }
    @FXML
    private void handleLogOut(ActionEvent event){
        System.exit(0);
    }
    
    //NON FXML FUNCTIONS WRITTEN HERE
    private void animateActivityPane(){
        FadeAnimation animate = new FadeAnimation(activityPane, 400);
        animate.fadeIn();
    }
    private boolean validateUserInput(){
                   if(!InputData.validateUsername(studentId.getText().trim())){
                       errorList.add("\t - Incorrect Student Id");
                   }
                   
                  if(errorList.isEmpty()){
                        return true;
                  }else{
                      
                        errorList.forEach((x) -> {
                                errormsg += x+" \n";
                        });
                        AlertBox.createErrorAlert("Search Error", errormsg);
                        return false;
                  }
                   
                 
    }
    private void clearAllFields(){
            studentId.clear();
    }
    private void emptyErrorData(){
            errorList.clear();
            errormsg = "";
   } 
    private boolean findStudent(String std, boolean initload){
        Student st = new Student();
        st.find(std);
        if(st.exists()){
            loadStudentPic(st.getPic());
            studentName.setText(st.getFullName());
            dept.setText(st.getDepartment());
            matric.setText(st.getMatricNumber());
            level.setText(st.getLevel());
            studentPanel.setVisible(true);
            animateStudentPanel();
            if(!courseLoaded && initload){
                loadComboBox(loadStudentCourses());
            }
            loadLineChart();
        return true;
        }else{
        return false;
        }
    }
    private void loadStudentPic(Image img){
           studPic.setImage(img);
           studPic.setFitHeight(150);
           studPic.setFitWidth(150);
           studPic.setPreserveRatio(true);
    }
    private void loadLineChart(){
        lineChart.setAnimated(false);
        lineChart.getData().clear();
        String stid = studentId.getText().trim().toUpperCase();
        CourseRegistration creg = new CourseRegistration();
        creg.find(stid);
       
        XYChart.Series series = new XYChart.Series();
        
        Courses cs = new Courses();
        cs.findWeekOnly(courseId.getValue());
            for(int x=1; x<=Integer.parseInt(cs.getNoOfWeeks()); x++){
                int attendanceStats = LectureAttendance.getCourseWeekAttendanceStatus(studentId.getText().trim().toUpperCase(), courseId.getValue(), x);
                series.getData().add(new XYChart.Data("Week "+x, attendanceStats));
            }
        lineChart.setTitle(courseId.getValue());
        lineChart.getData().add(series);
    }
    private void loadComboBox(List<String> courses){
        courseId.getItems().addAll(courses);
        courseId.setValue(courses.get(0));
        courseLoaded = true;
        
    }
    private List<String> loadStudentCourses(){
        CourseRegistration  cr = new CourseRegistration();
        cr.find(studentId.getText().trim().toUpperCase());
        return cr.getCourseCode();
                    
    }
    private void animateStudentPanel(){
        FadeAnimation animate = new FadeAnimation(studentPanel, 400);
        animate.fadeIn();
    }
    private double attendancePercentage(int x, int y){
            return (x*100)/y;
    }
}
