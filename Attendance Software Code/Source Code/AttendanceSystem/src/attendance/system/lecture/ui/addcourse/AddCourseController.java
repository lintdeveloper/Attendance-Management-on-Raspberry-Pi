/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.lecture.ui.addcourse;

import abaezcorp.sql.generator.SqlGenerator;
import attendance.system.dbconfig.DatabaseHandler;
import attendance.system.model.Lecturer;
import attendance.system.navigation.LectureModeNavigation;
import attendance.system.navigation.NavController;
import attendance.system.tools.AlertBox;
import attendance.system.tools.FadeAnimation;
import attendance.system.tools.InputData;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Abbasogaji
 */
public class AddCourseController extends NavController implements Initializable {

    @FXML
    private AnchorPane mainPane;
    @FXML
    private AnchorPane activityPane;
    @FXML
    private JFXTextField courseCode;
    @FXML
    private JFXComboBox<String> creditLoad;
    @FXML
    private JFXTextField courseTitle;
    @FXML
    private JFXComboBox<String> noOfWeeks;
    @FXML
    private JFXButton addCourse;
        /**
     *NON FXML VARIABLES 
    */
    private static List<String> errorList = new ArrayList();
    String errormsg = "";
    Connection dbConnection = DatabaseHandler.getConnection();
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        animateActivityPane();
        loadComboBox();
    }
    @FXML
    private void handleAddCourse(ActionEvent event) {
        emptyErrorData();
        if(validateUserInput()){
           if(insertCourseInDb()){
               AlertBox.createInfoAlert("Add Course", "Course : ("+courseCode.getText()+") has been created successfully");
               clearAllFields();
           }else{
               AlertBox.createErrorAlert("Course ID already exists");
           }     
        }
    }
    /**
     * NON-FXML METHODS 
     */
    
    private void loadComboBox(){
        noOfWeeks.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24");
        creditLoad.getItems().addAll("1","2","3","5","6","7","8");
    }
    private boolean insertCourseInDb(){
        try {
            SqlGenerator sql = new SqlGenerator();
            String query =  sql.table("LECTURER_COURSES").insert("course_id", "lecturer_id", "title", "credit_load", "department", "no_of_weeks").prepValues().get();
            PreparedStatement prepstmt = dbConnection.prepareStatement(query);
            prepstmt.setString(1, courseCode.getText().toUpperCase().trim());
            prepstmt.setString(2, Lecturer.getUsername().trim());
            prepstmt.setString(3, courseTitle.getText().trim());
            prepstmt.setInt(4, Integer.parseInt(creditLoad.getValue()));
            prepstmt.setString(5, Lecturer.getDepartment().trim());
            prepstmt.setInt(6, Integer.parseInt(noOfWeeks.getValue()));

            prepstmt.execute();
            return true;
        } catch (SQLException e) {
           //System.err.println(e.getLocalizedMessage());
           //AlertBox.createErrorAlert("SQL error");
        }
           return false;
    }
    private boolean validateUserInput(){
            if(!InputData.validateName(courseTitle.getText().trim())){
                errorList.add("\t - Invalid Course Title");
            }
            if(!InputData.validateCourseCode(courseCode.getText().trim())){
                errorList.add("\t - Invalid Course Code");
            }
            if(creditLoad.getSelectionModel().isEmpty()){
                errorList.add("\t - Select Credit Load");
            }

            if(noOfWeeks.getSelectionModel().isEmpty()){
                errorList.add("\t - Select Number of Weeks");
            }
           if(errorList.isEmpty()){
                 return true;
           }else{

                 errorList.forEach((x) -> {
                         errormsg += x+" \n";
                 });
                 AlertBox.createErrorAlert("Whoops! Something went wrong", errormsg);
                 return false;
           }

    }
    
    private void clearAllFields(){
            courseCode.clear();
            courseTitle.clear();
    }
    private void emptyErrorData(){
            errorList.clear();
            errormsg = "";
   }
    
}
