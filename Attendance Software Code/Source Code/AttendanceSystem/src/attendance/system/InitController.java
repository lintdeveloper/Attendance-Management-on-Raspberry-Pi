/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system;

import abaezcorp.sql.generator.SqlGenerator;
import attendance.system.dbconfig.DatabaseHandler;
import attendance.system.model.Lecturer;
import attendance.system.tools.AlertBox;
import attendance.system.tools.InputData;
import attendance.system.tools.WindowLoader;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author Abbasogaji
 */
public class InitController implements Initializable {

    @FXML
    private JFXButton signUpButton;
    @FXML
    private ImageView profilePic;
    @FXML
    private JFXTextField fullName;
    @FXML
    private JFXComboBox<String> lectureTitle;
    @FXML
    private JFXComboBox<String> department;
    @FXML
    private JFXTextField email;
    @FXML
    private JFXTextField phoneNumber;
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXPasswordField verifyPassword;

    /**
     *NON FXML VARIABLES 
    */
    private static List<String> errorList = new ArrayList();
    String errormsg = "";
   
    Connection dbConnection = DatabaseHandler.getConnection();
    private FileChooser chooseFile;
    private File file;
    private FileInputStream fis;
 
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadComboBox();
    }    

    @FXML
    private void handlePicChooser(ActionEvent event) {
        loadPicIntoImageView();
    }

    @FXML
    private void handleSignUpAction(ActionEvent event) {
        emptyErrorData();
        if(validateUserInput()){
           if(insertLecturerInDb()){
               AlertBox.createInfoAlert("Profile Created", "Lecturer profile("+fullName.getText().trim()+") has been created successfully \n You can now login with your USERNAME and PASSWORD");
               clearAllFields();
               WindowLoader  load = new WindowLoader();
               Lecturer.getInstance(username.getText().trim());
               load.loadNewStage(fullName," ","/attendance/system/main.fxml");
           }     
        }
    }
    
    private void loadPicIntoImageView(){
        
       chooseFile = new FileChooser();
       file = chooseFile.showOpenDialog(null);
       if(file != null){
           Image image = new Image(file.toURI().toString(), 150, 150, true, true);//path, prefwifth, prefheight, xxx, xxx
           profilePic.setImage(image);
           profilePic.setFitHeight(150);
           profilePic.setFitWidth(150);
           profilePic.setPreserveRatio(true);
       }
       
       
    
    }
    private boolean insertLecturerInDb(){
      try {
                
                SqlGenerator sql = new SqlGenerator();
                String query =  sql.table("LECTURER").insert("lecturer_id", "full_name", "email", "phone", "lecturer_title", "department", "password", "pic").prepValues().get();
                PreparedStatement prepstmt = dbConnection.prepareStatement(query);
                prepstmt.setString(1, username.getText().trim());
                prepstmt.setString(2, fullName.getText().trim());
                prepstmt.setString(3, email.getText().trim());
                prepstmt.setString(4, phoneNumber.getText().trim());
                prepstmt.setString(5, lectureTitle.getValue().trim());
                prepstmt.setString(6, department.getValue().trim());
                prepstmt.setString(7, password.getText().trim());
                
                fis = new FileInputStream(file);
                prepstmt.setBinaryStream(8, (InputStream)fis, (int)file.length());
                
                prepstmt.execute();
                return true;
            } catch (SQLException e) {
               System.err.println(e.getLocalizedMessage());
               AlertBox.createErrorAlert("SQL error");
            } catch (FileNotFoundException e){
                AlertBox.createErrorAlert("Error", "Whoops! Something went wrong - File not Found");
            }
            return false;
    }
    private boolean validateUserInput(){
                   if(!InputData.validateName(fullName.getText().trim())){
                       errorList.add("\t - Invalid Name");
                   }
                   if(lectureTitle.getSelectionModel().isEmpty()){
                       errorList.add("\t - Select Lecture Title");
                   }
                   
                   if(department.getSelectionModel().isEmpty()){
                       errorList.add("\t - Select a department");
                   }
                   
                   if(!InputData.validateEmail(email.getText().trim())){
                       errorList.add("\t - Invalid E-mail");
                   }
                   if(!InputData.validateMobileNumber(phoneNumber.getText().trim())){
                       errorList.add("\t - Invalid Phone Number");
                   }
                   if(!InputData.validateUsername(username.getText().trim())){
                       errorList.add("\t - Invalid Username");
                   }
                   
                   if(!InputData.validatePassword(password.getText().trim())){
                       errorList.add("\t - Invalid Password");
                   }
                   if(!InputData.checkPasswords(password.getText().trim(), verifyPassword.getText().trim())){
                       errorList.add("\t - Password don't match");
                   }
                   if(file == null){
                       errorList.add("\t - Profile picture not selected");
                   }
                  if(errorList.isEmpty()){
                        return true;
                  }else{
                      
                        errorList.forEach((x) -> {
                                errormsg += x+" \n";
                        });
                        AlertBox.createErrorAlert("Registration Error", errormsg);
                        return false;
                  }
                        
    }
    
    private void clearAllFields(){
            fullName.clear();
            email.clear();
            phoneNumber.clear();
            username.clear();
            password.clear();
            verifyPassword.clear();
            profilePic.setImage(new Image("/attendance/system/abbas.png"));
    }
   private void emptyErrorData(){
            errorList.clear();
            errormsg = "";
   }
   private void loadComboBox(){
        department.getItems().addAll("Computer Engineering", "Electrical Engineering", "Mechanical Engineering", "Telecommunication Engineering", "Computer Science", "Civil Engineering");
        lectureTitle.getItems().addAll("Engr", "Mr", "Mrs", "Miss", "Doc", "Prof");
  
   }
    
}
