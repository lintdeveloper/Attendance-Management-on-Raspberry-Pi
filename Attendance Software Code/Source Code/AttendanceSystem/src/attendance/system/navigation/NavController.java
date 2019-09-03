/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.navigation;


import attendance.system.tools.FadeAnimation;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Abbasogaji
 */

    public class NavController {
        
    @FXML
    public AnchorPane mainPane;
    
    @FXML
    public AnchorPane activityPane;
    
     
    @FXML
    public void handleLoadDashboardHomePage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LectureModeNavigation.HOME_PAGE));
            AnchorPane homepage = loader.load();
            mainPane.getChildren().setAll(homepage);
        } catch (IOException e) {
        }
    }
    
    @FXML
    public void handleLoadStartLecturePage(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LectureModeNavigation.START_LECTURE_PAGE));
        try {
            AnchorPane formPane = loader.load();
            mainPane.getChildren().setAll(formPane);
        } catch (IOException ex) {
            
        }
        
       
    }
    @FXML
    public void handleLoadStudentPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LectureModeNavigation.STUDENT_PORTAL_PAGE));
            AnchorPane homepage = loader.load();
            mainPane.getChildren().setAll(homepage);
        } catch (IOException e) {
        }

    }

    @FXML
    public void handleLoadCourseListPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LectureModeNavigation.COURSE_LIST_PAGE));
            AnchorPane homepage = loader.load();
            mainPane.getChildren().setAll(homepage);
        } catch (IOException e) {
        }
    }
    @FXML
    public void handleLoadAddCoursePage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LectureModeNavigation.ADD_COURSE_PAGE));
            AnchorPane homepage = loader.load();
            mainPane.getChildren().setAll(homepage);
        } catch (IOException e) {
        }
        
    }
    
    @FXML
    public void handleLoadSendReportPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LectureModeNavigation.REPORT_PORTAL_PAGE));
            AnchorPane homepage = loader.load();
            mainPane.getChildren().setAll(homepage);
        } catch (IOException e) {
        }
    }
    
    @FXML
    public void handleLogOut(ActionEvent event){
        System.exit(0);
    }
    
    public void animateActivityPane(){
        FadeAnimation animate = new FadeAnimation(activityPane, 400);
        animate.fadeIn();
    }
}
