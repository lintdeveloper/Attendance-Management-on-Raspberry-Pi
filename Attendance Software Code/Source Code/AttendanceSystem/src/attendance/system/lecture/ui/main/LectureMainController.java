/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.lecture.ui.main;

import attendance.system.model.Courses;
import attendance.system.model.Lecturer;
import attendance.system.model.CourseRegistration;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import attendance.system.navigation.NavController;
import attendance.system.navigation.NavDisabler;
/**
 * FXML Controller class
 *
 * @author Abbasogaji
 */
public class LectureMainController  extends NavController implements Initializable{

/*
    
    @FXML
    private AnchorPane mainPane;
    
    @FXML
    private AnchorPane activityPane;
*/    
    @FXML
    private ImageView img;
    
    @FXML 
    private Label staffName;
    
    @FXML
    private Label staffEmail;
    
    @FXML
    private Label staffPhone;
    
    @FXML
    private Label deptInfo;
    
    @FXML
    private BarChart<?, ?> lectureSummary;

    @FXML
    private CategoryAxis courseTaken;

    @FXML
    private NumberAxis noOfStudents;
    
    
    @FXML
    private JFXButton startLectureButton;
    
    /**
     * NON FXML VARIABLES
     */
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
        NavDisabler.disableIfNotConnectedToATDevice(startLectureButton);
        animateActivityPane();
        loadProfilePic();
        loadProfileDetails();
        loadBarChart();
        
        
    }
    /*
    @FXML
    private void handleLoadStartLecturePage(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LectureModeNavigation.START_LECTURE_PAGE));
        try {
            AnchorPane formPane = loader.load();
            mainPane.getChildren().setAll(formPane);
        } catch (IOException ex) {
            Logger.getLogger(LectureMainController.class.getName()).log(Level.SEVERE, null, ex);
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
            e.printStackTrace();
        }
    }
    @FXML
    private void handleLoadAddCoursePage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LectureModeNavigation.ADD_COURSE_PAGE));
            AnchorPane homepage = loader.load();
            mainPane.getChildren().setAll(homepage);
        } catch (IOException e) {
             e.printStackTrace();
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
    private void handleLogOut(ActionEvent event){
        System.exit(0);
    }
    */
    private void loadProfileDetails(){
        staffPhone.setText(Lecturer.getPhone());
        staffName.setText(Lecturer.getTitle()+" "+Lecturer.getFullName());
        deptInfo.setText("Lecturer of "+Lecturer.getDepartment()+" Department");
        staffEmail.setText(Lecturer.getEmail());
    }
    private void loadProfilePic(){
         img.setImage(Lecturer.getPic());
         img.setCache(true);
    }
    private void loadBarChart(){
        XYChart.Series set1 = new XYChart.Series<>();
        Courses cs = new Courses();
        cs.all();
        for(int x=0; x<cs.getCourseTotal(); x++){
            set1.getData().add(new XYChart.Data<>(cs.getAllCourseCode().get(x), CourseRegistration.getNoOfStudent(cs.getAllCourseCode().get(x))));
        }
        lectureSummary.getData().addAll(set1);
        
    }
    
  /*
    private void animateActivityPane(){
        FadeAnimation animate = new FadeAnimation(activityPane, 400);
        animate.fadeIn();
    }
  */
    
    
}
