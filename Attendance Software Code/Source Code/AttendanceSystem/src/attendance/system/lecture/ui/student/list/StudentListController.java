/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.lecture.ui.student.list;

import abaezcorp.sql.generator.SqlGenerator;
import attendance.system.dbconfig.DatabaseHandler;
import attendance.system.dbconfig.DbSqliteConstants;
import attendance.system.model.Student;
import attendance.system.navigation.LectureModeNavigation;
import attendance.system.server.AppServer;
import attendance.system.student.ui.edit.StudentEditController;
import attendance.system.tools.AlertBox;
import attendance.system.tools.FadeAnimation;
import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
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
public class StudentListController implements Initializable {

    @FXML
    private AnchorPane mainPane;
    @FXML
    private AnchorPane activityPane;
   
    ObservableList<StudentList> tableList = FXCollections.observableArrayList();
    @FXML
    private TableView<StudentList> studentList;
    @FXML
    private TableColumn<StudentList, String> studentId;
    @FXML
    private TableColumn<StudentList, String> fullName;
    @FXML
    private TableColumn<StudentList, String> matricNumber;
    @FXML
    private TableColumn<StudentList, String> department;
    @FXML
    private TableColumn<StudentList, String> level;
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
    private void handleLoadEditWindow(ActionEvent event){
        
        StudentList std = studentList.getSelectionModel().getSelectedItem();
        if(std != null){
        
            String stdId = std.getStudentId();
            String name = std.getFullName();
            String matricNum = std.getMatricNumber();
            String dept = std.getDepartment();
            String lvl = std.getLevel();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LectureModeNavigation.STUDENT_EDIT_WINDOW));
            try{
                Parent root = loader.load();  
                StudentEditController edits = (StudentEditController)loader.getController();
                edits.setCredentials(stdId, name, matricNum, dept, lvl);
                edits.initPicInImageView();
                edits.initTable();
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.showAndWait();
            }catch(IOException e){
                e.printStackTrace();

            }
        
        }else{
            AlertBox.createErrorAlert("No Student was selected");
        }
        
        
    }
    

    @FXML
    private void handleLogOut(ActionEvent event) {
        System.exit(0);
    }
    @FXML
    private void handleDeleteCourseOption(ActionEvent event) {
        StudentList studentSelectedForDeletion = studentList.getSelectionModel().getSelectedItem();
        if(studentSelectedForDeletion != null){
        
            String stdId = studentSelectedForDeletion.getStudentId();
            if(AlertBox.createConfirmationAlert("Are you sure you want to remove student with Id - "+stdId)){
                SqlGenerator sql = new SqlGenerator();
                String delSql = sql.table(DbSqliteConstants.STUDENTS_TABLE_NAME).delete().whereEqual("student_id", stdId).get();
                if(DatabaseHandler.getInstance().exeAction(delSql)){
                        //DISPLAY ALERT AND UPDATE TABLE
                        AlertBox.createInfoAlert(stdId+" - deleted successfully");
                        tableList.remove(studentSelectedForDeletion);
                        
                }else{
                    AlertBox.createErrorAlert("Whoops! Something went wrong");
                }
            }
        
        }else{
            AlertBox.createErrorAlert("No Student was selected for deletion!");
        }
    }
    
    //NON FXML FUNCTIONS WRITTEN HERE
    private void animateActivityPane(){
        FadeAnimation animate = new FadeAnimation(activityPane, 400);
        animate.fadeIn();
    }
    private void initColumn(){
        studentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        fullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        matricNumber.setCellValueFactory(new PropertyValueFactory<>("matricNumber"));
        department.setCellValueFactory(new PropertyValueFactory<>("department"));
        level.setCellValueFactory(new PropertyValueFactory<>("level"));
        
        loadTableData();
    }
    private void loadTableData(){
        Student st = new Student();
        st.all();

        for(int x=0; x<st.getStudentTotal(); x++){
            tableList.add(new StudentList(st.getAllStudentId().get(x), st.getAllFullName().get(x), st.getAllMatricNumber().get(x), st.getAllDepartment().get(x), st.getAllLevel().get(x)));

        }
        studentList.setItems(tableList);
    }
   
    
}
