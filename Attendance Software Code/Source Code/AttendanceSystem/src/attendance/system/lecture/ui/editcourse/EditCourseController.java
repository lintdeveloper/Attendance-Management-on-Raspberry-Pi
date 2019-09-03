/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.lecture.ui.editcourse;

import abaezcorp.sql.generator.SqlGenerator;
import attendance.system.dbconfig.DatabaseHandler;
import attendance.system.dbconfig.DbSqliteConstants;
import attendance.system.tools.AlertBox;
import attendance.system.tools.InputData;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author Abbasogaji
 */
public class EditCourseController implements Initializable {

    @FXML
    private JFXTextField ccode;
    @FXML
    private JFXComboBox<Integer> cload;
    @FXML
    private JFXTextField ctitle;
    @FXML
    private JFXComboBox<Integer> cduration;

    private static List<String> errorList = new ArrayList();
    String errormsg = "";
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    @FXML
    private void handleSaveEditAction(ActionEvent event) {
        emptyErrorData();
        if(validateUserInput()){
           if(updateCourseInDb()){
               AlertBox.createInfoAlert(ccode.getText()+" - updated successfully");
            
           }else{
               AlertBox.createErrorAlert("Whoops!, Something went wrong");
           }
        } 
    }
    public void setCourseId(String courseCode, String creditLoad, String courseTitle, String noOfWeeks){
        ccode.setText(courseCode);
        ccode.setEditable(false);
        cload.getItems().addAll(1,2,3,4,5,6,7,8);
        cload.setValue(InputData.extractInt(creditLoad));
        ctitle.setText(courseTitle);
        cduration.getItems().addAll(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24);
        cduration.setValue(InputData.extractInt(noOfWeeks));
    }
    //private void 
    private boolean updateCourseInDb(){
                String cc = ccode.getText().toUpperCase().trim();
                String cl = Integer.toString(cload.getValue());
                String tt = ctitle.getText();
                String dura = Integer.toString(cduration.getValue());
                SqlGenerator sql = new SqlGenerator();
                String query = sql.table(DbSqliteConstants.LECTURER_COURSES_TABLE_NAME).update("credit_load", "title", "no_of_weeks").to(cl,tt,dura).whereEqual("course_id", cc).get();
                return DatabaseHandler.getInstance().exeAction(query);
    }
    private boolean validateUserInput(){
            if(!InputData.validateName(ctitle.getText().trim())){
                errorList.add("\t - Invalid Course Title");
            }
            if(!InputData.validateCourseCode(ccode.getText().trim())){
                errorList.add("\t - Invalid Course Title");
            }
            if(cload.getSelectionModel().isEmpty()){
                errorList.add("\t - Select Credit Load");
            }

            if(cduration.getSelectionModel().isEmpty()){
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
   private void emptyErrorData(){
            errorList.clear();
            errormsg = "";
   }
    
    
}
