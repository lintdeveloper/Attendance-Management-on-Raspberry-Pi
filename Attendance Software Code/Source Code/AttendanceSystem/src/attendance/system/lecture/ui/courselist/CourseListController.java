/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.lecture.ui.courselist;

import abaezcorp.sql.generator.SqlGenerator;
import attendance.system.dbconfig.DatabaseHandler;
import attendance.system.dbconfig.DbSqliteConstants;
import attendance.system.lecture.ui.editcourse.EditCourseController;
import attendance.system.model.Courses;
import attendance.system.navigation.LectureModeNavigation;
import attendance.system.navigation.NavController;
import attendance.system.tools.AlertBox;
import attendance.system.tools.FadeAnimation;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Abbasogaji
 */
public class CourseListController extends NavController implements Initializable {

    @FXML
    private AnchorPane mainPane;
    @FXML
    private AnchorPane activityPane;
    @FXML
    private TableView<CourseList> courseList;
    @FXML
    private TableColumn<CourseList, String> courseCode;
    @FXML
    private TableColumn<CourseList, String> courseTitle;
    @FXML
    private TableColumn<CourseList, String> creditLoad;
    @FXML
    private TableColumn<CourseList, String> noOfWeeks;
   
    ObservableList<CourseList> tableList = FXCollections.observableArrayList(); 
    /**
     * Initialises the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        animateActivityPane();
        initColumn();
    }
    @FXML
    private void handleLoadEditWindow(ActionEvent event){
        
        CourseList course = courseList.getSelectionModel().getSelectedItem();
        if(course != null){
        
            String ccode = course.getCourseCode();
            String cload = course.getCreditLoad();
            String ctitle = course.getCourseTitle();
            String weeks = course.getSize();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LectureModeNavigation.COURSE_EDIT_WINDOW));
            try{
                Parent root = loader.load();  
                EditCourseController editc = (EditCourseController)loader.getController();
                editc.setCourseId(ccode, cload, ctitle, weeks);
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.showAndWait();
            }catch(IOException e){
                e.printStackTrace();

            }
        
        }else{
            AlertBox.createErrorAlert("No Course was selected");
        }
        
        
    }
    
    @FXML
    private void handleDeleteCourseOption(ActionEvent event) {
        CourseList courseSelectedForDeletion = courseList.getSelectionModel().getSelectedItem();
        if(courseSelectedForDeletion != null){
        
            String ccode = courseSelectedForDeletion.getCourseCode();
            if(AlertBox.createConfirmationAlert("Are you sure you want to delete - "+ccode)){
                SqlGenerator sql = new SqlGenerator();
                String delSql = sql.table(DbSqliteConstants.LECTURER_COURSES_TABLE_NAME).delete().whereEqual("course_id", ccode).get();
                if(DatabaseHandler.getInstance().exeAction(delSql)){
                        //DISPLAY ALERT AND UPDATE TABLE
                        AlertBox.createInfoAlert(ccode+" - deleted successfully");
                        tableList.remove(courseSelectedForDeletion);
                        
                }else{
                    AlertBox.createErrorAlert("Whoops! Something went wrong");
                }
            }
        
        }else{
            AlertBox.createErrorAlert("No Course was selected for deletion!");
        }
    }
    private void initColumn(){
        courseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseTitle.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));
        creditLoad.setCellValueFactory(new PropertyValueFactory<>("creditLoad"));
        noOfWeeks.setCellValueFactory(new PropertyValueFactory<>("size"));
        
        loadTableData();
    }
    private void loadTableData(){
        Courses cs = new Courses();
        cs.all();

        for(int x=0; x<cs.getCourseTotal(); x++){
            tableList.add(new CourseList(cs.getAllCourseCode().get(x), cs.getAllCourseTitle().get(x), cs.getAllCreditLoad().get(x)+" unit", cs.getAllNoOfWeeks().get(x)+" Weeks"));

        }
        courseList.setItems(tableList);
    }

    
}
