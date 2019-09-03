/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.lecture.ui.student;

import attendance.system.model.Courses;
import attendance.system.model.CourseRegistration;
import attendance.system.navigation.LectureModeNavigation;
import attendance.system.navigation.NavController;
import attendance.system.tools.WindowLoader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Abbasogaji
 */
public class StudentPortalController extends NavController implements Initializable {

    @FXML
    private AnchorPane mainPane;
    @FXML
    private AnchorPane activityPane;
    @FXML
    private BarChart<?, ?> lectureSummary;
    @FXML
    private NumberAxis noOfStudents;
    @FXML
    private CategoryAxis courseTaken;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        animateActivityPane();
        loadBarChart();
    }
    
    @FXML
    private void handleLoadStudentWindow(ActionEvent event) {
        WindowLoader win = new WindowLoader();
        win.loadWindow("Student Portal", LectureModeNavigation.STUDENT_PORTAL);
    }
    @FXML
    private void handleLoadStudentList(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LectureModeNavigation.STUDENT_LIST_PAGE));
            AnchorPane homepage = loader.load();
            mainPane.getChildren().setAll(homepage);
        } catch (IOException e) {
        }
    }
    @FXML
    private void handleLoadViewStudent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LectureModeNavigation.VIEW_STUDENT_ALT_PAGE));
            AnchorPane homepage = loader.load();
            mainPane.getChildren().setAll(homepage);
        } catch (IOException e) {
        }
    }
    
    private void loadBarChart(){
        XYChart.Series set1 = new XYChart.Series<>();
        Courses cs = new Courses();
        cs.all();
        for(int x=1; x<cs.getCourseTotal(); x++){
            set1.getData().add(new XYChart.Data<>(cs.getAllCourseCode().get(x), CourseRegistration.getNoOfStudent(cs.getAllCourseCode().get(x))));
        }
        lectureSummary.getData().addAll(set1);
        
    }
}
