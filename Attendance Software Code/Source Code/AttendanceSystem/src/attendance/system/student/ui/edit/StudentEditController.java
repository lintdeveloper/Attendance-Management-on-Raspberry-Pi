/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.student.ui.edit;

import abaezcorp.sql.generator.SqlGenerator;
import attendance.system.dbconfig.DatabaseHandler;
import attendance.system.dbconfig.DbSqliteConstants;
import attendance.system.lecture.ui.courselist.CourseList;
import attendance.system.model.Courses;
import attendance.system.model.Student;
import attendance.system.student.ui.main.StudentMainController;
import attendance.system.tools.AlertBox;
import attendance.system.tools.InputData;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Abbasogaji
 */
public class StudentEditController implements Initializable {

    @FXML
    private ImageView studPic;
    @FXML
    private JFXTextField studFullName;
    @FXML
    private JFXTextField studId;
    @FXML
    private JFXTextField studMatric;
    @FXML
    private JFXTextField studParentNo;
    @FXML
    private JFXComboBox<String> studDepartment;
    @FXML
    private JFXComboBox<String> studLevel;
    @FXML
    private JFXButton createStud;
    
    
    @FXML
    private TableView<CourseList> courseList;
    @FXML
    private TableColumn<CourseList, String> courseCode;
    @FXML
    private TableColumn<CourseList, String> courseTitle;
    @FXML
    private TableColumn<CourseList, String> creditLoad;
    @FXML
    private JFXTextField studentId;
    
    @FXML
    private JFXComboBox<String> coursesAvailable;

    /**
     * NON FXML VARIABLES
     */
   ObservableList<CourseList> tableList = FXCollections.observableArrayList(); 
   private static List<String> errorList = new ArrayList();
   String errormsg = "";
    
   Connection dbConnection = DatabaseHandler.getConnection();
   FileChooser chooseFile;
   File file;
   FileInputStream fis;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadComboBoxes();
        setUnEditableFields();
        loadCourses();
        //initPicInImageView();
        //initTable();
    }
    @FXML
    private void handlePicChooser(ActionEvent event){
        loadPicIntoImageView();
    }
    @FXML
    private void handleEditProfile(ActionEvent event){
        emptyErrorData();
        if(validateUserInput()){
           if(updateStudentToDb()){
               AlertBox.createInfoAlert("Profile Updated", "Student profile("+studFullName.getText().trim()+") has been updated successfully");
           }else{
               AlertBox.createErrorAlert("Whoops!, Something went wrong");
           }
        }    
    }
    @FXML
    private void handleDeleteCourseOption(ActionEvent event) {
    CourseList courseSelectedForDeletion = courseList.getSelectionModel().getSelectedItem();
        if(courseSelectedForDeletion != null){
        
            String courseregId = studentId.getText()+""+courseSelectedForDeletion.getCourseCode();
            if(AlertBox.createConfirmationAlert("Are you sure you want to remove course with Id - "+courseSelectedForDeletion.getCourseCode())){
                SqlGenerator sql = new SqlGenerator();
                String delSql = sql.table(DbSqliteConstants.STUDENT_REG_TABLE_NAME).delete().whereEqual("coursereg_id", courseregId).get();
                if(DatabaseHandler.getInstance().exeAction(delSql)){
                        //DISPLAY ALERT AND UPDATE TABLE
                        AlertBox.createInfoAlert(courseSelectedForDeletion.getCourseCode()+" - deleted from student course registration successfully");
                        
                        tableList.remove(courseSelectedForDeletion);
                        
                }else{
                    AlertBox.createErrorAlert("Whoops! Something went wrong");
                }
            }
        
        }else{
            AlertBox.createErrorAlert("No Student was selected for deletion!");
        }
    }
    @FXML
    private void handleCloseWindow(MouseEvent event) {
        Stage stage = (Stage)createStud.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void handleAddCourse(ActionEvent event) {
            emptyErrorData();
            if(insertCourseRegIntoDb()){ 
                AlertBox.createInfoAlert("Course -"+coursesAvailable.getValue()+" added to Student -"+studentId.getText()+" course registration");
                loadCourseDetails(coursesAvailable.getValue());
            }else{
                AlertBox.createErrorAlert("Course already registered for student");
            }
             //addNewTabledata() -- the add new table data will check if table has been initalized if it has then it adds new data
            //UPDATE THE TABLE WITH A NEW ROW
        
    }
    
    
    public void setCredentials(String std, String fullName, String matricNum, String dept, String lvl){
            studentId.setText(std);
            studId.setText(std);
            studFullName.setText(fullName);
            studMatric.setText(matricNum);
            studDepartment.setValue(dept);
            studLevel.setValue(lvl);
    }
    private boolean validateUserInput(){
                   if(!InputData.validateName(studFullName.getText().trim())){
                       errorList.add("\t - Incorrect Student Name");
                   }
                   if(!InputData.validateUsername(studId.getText().trim())){
                       errorList.add("\t - Incorrect Student Id");
                   }
                   
                   if(!InputData.validateMatricNumber(studMatric.getText().trim())){
                       errorList.add("\t - Incorrect Student Matriculation Number");
                   }
                   
                   if(!InputData.validateMobileNumber(studParentNo.getText().trim())){
                       errorList.add("\t - Phone Number invalid -- e.g use: 08023456781");
                   }
                   
                   if(studDepartment.getSelectionModel().isEmpty()){
                       errorList.add("\t - Student department was not selected");
                   }
                   if(studLevel.getSelectionModel().isEmpty()){
                       errorList.add("\t - Student Level was not selected");
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
    //CALLED BY: STUDENT LIST CONTROLLER
    public void initPicInImageView(){
        Student st = new Student();
        st.find(studentId.getText());
        studPic.setImage(st.getPic());
        studPic.setFitHeight(150);
        studPic.setFitWidth(150);
        studPic.setPreserveRatio(true);
    }
    private void loadPicIntoImageView(){
        
       chooseFile = new FileChooser();
       file = chooseFile.showOpenDialog(null);
       if(file != null){
           Image image = new Image(file.toURI().toString(), 150, 150, true, true);//path, prefwifth, prefheight, xxx, xxx
           studPic.setImage(image);
           studPic.setFitHeight(150);
           studPic.setFitWidth(150);
           studPic.setPreserveRatio(true);
       }
       
       
    
    }
    private void loadComboBoxes(){
        studDepartment.getItems().addAll("Computer Engineering", "Electrical Engineering", "Cyber Security", "Telecommunication Engineering");
        studLevel.getItems().addAll("100", "200", "300", "400", "500");
    }
    private boolean updateStudentToDb(){
            try {
                
                SqlGenerator sql = new SqlGenerator();
                String query;
                if(file == null){
                 query =  "UPDATE students SET  matric_number = ?,  parent_mobile = ?, full_name = ?, department = ?, level = ? WHERE student_id = '"+studId.getText().trim()+"'"; 
                }else{
                 query =  "UPDATE students SET  matric_number = ?,  parent_mobile = ?, full_name = ?, department = ?, level = ?, pic = ? WHERE student_id = '"+studId.getText().trim()+"'";
                }
                PreparedStatement prepstmt = dbConnection.prepareStatement(query);
                prepstmt.setString(1, studMatric.getText().trim());
                prepstmt.setString(2, studParentNo.getText().trim());
                prepstmt.setString(3, studFullName.getText().trim());
                prepstmt.setString(4, studDepartment.getSelectionModel().getSelectedItem());
                prepstmt.setInt(5, Integer.parseInt(studLevel.getValue()));
                if(file != null){
                    fis = new FileInputStream(file);
                    prepstmt.setBinaryStream(6, (InputStream)fis, (int)file.length());
                }
                prepstmt.execute();
                return true;
            } catch (SQLException e) {
                AlertBox.createErrorAlert("SQL error");
            } catch (FileNotFoundException e){
                AlertBox.createErrorAlert("Error", "Whoops! Something went wrong - File not Found");
            }
            return false;
             
    }
    private void clearAllFields(){
            studFullName.clear();
            studId.clear();
            studMatric.clear();
            studPic.setImage(new Image("@abbas.png"));
    }
    private void emptyErrorData(){
            errorList.clear();
            errormsg = "";
   } 
   
   
    private void loadCourses(){
        Courses cs = new Courses();
        cs.all();
        coursesAvailable.getItems().addAll(cs.getAllCourseCode());
        
    }
    public void initTable(){
        courseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseTitle.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));
        creditLoad.setCellValueFactory(new PropertyValueFactory<>("creditLoad"));
        loadTableData();
        
    }
    private void loadTableData(){
        String std = studentId.getText().trim();
        String sql = "SELECT a.*, b.* FROM "+DbSqliteConstants.LECTURER_COURSES_TABLE_NAME+" a, "+DbSqliteConstants.STUDENT_REG_TABLE_NAME+" b WHERE b.student_id = '"+std+"' AND a.course_id = b.course_id";
        System.out.println(sql);
        ResultSet data = DatabaseHandler.getInstance().exeQuery(sql);
        try {
            while(data.next()){
                    tableList.add(new CourseList(data.getString("course_id"), data.getString("title"), Integer.toString(data.getInt("credit_load"))));
            }
        } catch (SQLException ex) {
            Logger.getLogger(StudentMainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        courseList.setItems(tableList);
    }
    private void loadCourseDetails(String courseId){
            Courses cc = new Courses();
            cc.find(courseId);
            tableList.add(new CourseList(cc.getCourseCode(), cc.getCourseTitle(), cc.getCreditLoad()));
            
    }
    private boolean insertCourseRegIntoDb(){
        try {
                
                SqlGenerator sql = new SqlGenerator();
                String query =  sql.table(DbSqliteConstants.STUDENT_REG_TABLE_NAME).insert("coursereg_id", "student_id", "course_id").prepValues().get();
                PreparedStatement prepstmt = dbConnection.prepareStatement(query);
                    prepstmt.setString(1, studentId.getText().trim().toUpperCase()+""+coursesAvailable.getSelectionModel().getSelectedItem());
                    prepstmt.setString(2, studentId.getText().trim().toUpperCase());
                    prepstmt.setString(3, coursesAvailable.getSelectionModel().getSelectedItem());
                    prepstmt.execute();
                return true;
        } catch (SQLException e) {
                //AlertBox.createErrorAlert("Database Error - Data Already exists");
        }
        return false;
             
    }
    private void setUnEditableFields(){
        studId.setEditable(false);
        studentId.setEditable(false);
    }

    
    
}
