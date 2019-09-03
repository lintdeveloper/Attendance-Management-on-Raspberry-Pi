/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.student.ui.main;

import abaezcorp.sql.generator.SqlGenerator;
import attendance.system.dbconfig.DatabaseHandler;
import attendance.system.dbconfig.DbSqliteConstants;
import attendance.system.lecture.ui.courselist.CourseList;
import attendance.system.model.Courses;
import attendance.system.model.Student;
import attendance.system.server.AppServer;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * FXML Controller class
 *
 * @author Abbasogaji
 */
public class StudentMainController implements Initializable {

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
    
    /**
     * FXML VARIABLE FOR COURSE REGISTRATION TAB
     */

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
    Timer timer;
    TimerTask task;
    private static boolean isTableInit = false;
    ObservableList<CourseList> tableList = FXCollections.observableArrayList(); 
    private static List<String> errorList = new ArrayList();
    String errormsg = "";
    private static String fingerHash = "";
    
    Connection dbConnection = DatabaseHandler.getConnection();
    FileChooser chooseFile;
    File file;
    FileInputStream fis;
    
    
    //FOR FINGERPRINT REGISTRATION
   
    @FXML
    private VBox fprintDetailsPanel;
    @FXML
    private Label fphashInfo;
    @FXML
    private JFXTextField fpStudId;
    @FXML
    private JFXComboBox<String> thumbType;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        courseRegTabStidListener();
        loadComboBoxes();
        loadCourses();
        initThumbTypes();
        fprintDetailsPanel.setVisible(false);
        
    }
    
    @FXML
    private void handlePicChooser(ActionEvent event){
        loadPicIntoImageView();
    }
    @FXML
    private void handleCreateProfile(ActionEvent event){
        emptyErrorData();
        if(validateUserInput()){
           if(insertStudentToDb()){
               AlertBox.createInfoAlert("Profile Created", "Student profile("+studFullName.getText().trim()+") has been created successfully");
               clearAllFields();
           }else{
               AlertBox.createErrorAlert("Whoops!, Something went wrong");
           }
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
        if(checkIfStudentExists()){
            if(insertCourseRegIntoDb()){ 
                AlertBox.createInfoAlert("Course -"+coursesAvailable.getValue()+" added to Student -"+studentId.getText()+" course registration");
                if(!isTableInit){
                    initTable();
                }
                loadCourseDetails(coursesAvailable.getValue());
            }else{
                AlertBox.createErrorAlert("Course already registered for student");
            }
            if(!isTableInit){
             initTable();
            }
             //addNewTabledata() -- the add new table data will check if table has been initalized if it has then it adds new data
            //UPDATE THE TABLE WITH A NEW ROW
        }else{
            AlertBox.createErrorAlert("Student ID -"+studentId.getText()+" does not exists");
        }
    }
    
    /**
     * STUDENT BASIC INFORMATION REGISTRATION METHODS
     * @return 
     */
    private boolean validateUserInput(){
                   if(!InputData.validateName(studFullName.getText().trim())){
                       errorList.add("\t - Incorrect Student Name");
                   }
                   if(!InputData.validateMobileNumber(studParentNo.getText().trim())){
                       errorList.add("\t - Wrong number format use e.g: 08012345678");
                   }
                   if(!InputData.validateUsername(studId.getText().trim())){
                       errorList.add("\t - Incorrect Student Id");
                   }
                   
                   if(!InputData.validateMatricNumber(studMatric.getText().trim())){
                       errorList.add("\t - Incorrect Student Matriculation Number");
                   }
                   
                   if(studDepartment.getSelectionModel().isEmpty()){
                       errorList.add("\t - Student department was not selected");
                   }
                   if(studLevel.getSelectionModel().isEmpty()){
                       errorList.add("\t - Student Level was not selected");
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
    private boolean insertStudentToDb(){
            try {
                
                SqlGenerator sql = new SqlGenerator();
                String query =  sql.table("students").insert("student_id", "matric_number", "parent_mobile", "full_name", "department", "level", "pic").prepValues().get();
                PreparedStatement prepstmt = dbConnection.prepareStatement(query);
                prepstmt.setString(1, studId.getText().toUpperCase().trim());
                prepstmt.setString(2, studMatric.getText().trim());
                prepstmt.setString(3, studParentNo.getText().trim().replaceFirst("0", "234"));
                prepstmt.setString(4, studFullName.getText().trim());
                prepstmt.setString(5, studDepartment.getSelectionModel().getSelectedItem());
                prepstmt.setInt(6, Integer.parseInt(studLevel.getValue()));
                
                fis = new FileInputStream(file);
                prepstmt.setBinaryStream(7, (InputStream)fis, (int)file.length());
                
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
            file = null;
            studPic.setImage(new Image("/attendance/system/images/abbas.png"));
    }
    private void emptyErrorData(){
            errorList.clear();
            errormsg = "";
   } 
   //END OF STUDENT BASIC REGISTRATION METHOD
   
   /**
    * FINGERPRINT REGISTATION METHODS
   */
    @FXML
    private void handleCheckforFp(ActionEvent event) {
        requestforFingerprintData();
        checkIfFingerprintLoaded();
    }

    @FXML
    private void handleStoreFingerprintToDb(ActionEvent event) {
        String sid = fpStudId.getText().trim().toUpperCase();
        String fingerprintId = AppServer.getFingerprintDetails();
        String fingerprintHash = fingerHash;
        String thumb_db_column = "finger_print_id";
        String thumb_hash_column = "finger_print_hash";
        //System.out.println(fingerprintId);
        //System.out.println(fingerprintHash);
        //UPDATE TABLE SET fingerpirnt id and fingerprint_hash where user == userid
        //if succesfull show alert box user fingerprint data stored successfully
        SqlGenerator str = new SqlGenerator();
        if(thumbType.getValue().equals("Left Thumb")){
                thumb_db_column = "finger_print_id_left";
                thumb_hash_column = "finger_print_hash_left";
        }
        String sql = str.table("students").update(thumb_db_column, thumb_hash_column).to(fingerprintId, fingerprintHash).whereEqual("student_id", sid).get();
        System.out.println(sql);
        if(DatabaseHandler.getInstance().exeAction(sql)){
            AlertBox.createInfoAlert("You have Successfully saved fingerprint for user: "+sid);
        }
    }
    private void requestforFingerprintData(){
       //the input streams will be checking for message 
       //if message is gotten it then split it or recieves it inform of json
       //it will triggger change the text in a two labels - one for the Fingerprint DATA gotten 
       //the other for fingerprint 
       //then a SAVE BUTTON WILL BE SET TO USABLE
       //then one i click save it will store the fingerprint data in the sqlite database
       //after storing in sqlite database it should show an alertbo saying fingerprint stored succesfully
       
       timer = new Timer(true);
       Thread st1 = new Thread(() -> {
                
                AppServer.recieveFingerprint(fprintDetailsPanel);
       });
       st1.setDaemon(true);
       st1.start();
               
       
   }
    private void checkIfFingerprintLoaded(){
        Thread st2 = new Thread(() -> {
            task = new TimerTask(){
                @Override
                public void run() {
                    if(!AppServer.getFingerprintDetails().isEmpty()){
                            Platform.runLater(() -> {
                            fingerHash = DigestUtils.sha256Hex(AppServer.getFingerprintDetails());
                            fphashInfo.setText(fingerHash);
                            fprintDetailsPanel.setVisible(true);
                            
                            this.cancel();
                           });
                    }
                }
            };
            timer.scheduleAtFixedRate(task, 0, 100);
                
        });
        st2.setDaemon(true);
        st2.start();
   }
   
    private void initThumbTypes(){
        thumbType.getItems().addAll("Left Thumb", "Right Thumb");
    }
   //END OF FINGERPRINT REGISTRATION METHODS
    
    private void loadCourses(){
        Courses cs = new Courses();
        cs.all();
        coursesAvailable.getItems().addAll(cs.getAllCourseCode());
        
    }
    private void initTable(){
        courseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseTitle.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));
        creditLoad.setCellValueFactory(new PropertyValueFactory<>("creditLoad"));
        loadTableData();
        
    }
    private void loadTableData(){
        String std = studentId.getText().toUpperCase().trim();
        String sql = "SELECT a.*, b.* FROM "+DbSqliteConstants.LECTURER_COURSES_TABLE_NAME+" a, "+DbSqliteConstants.STUDENT_REG_TABLE_NAME+" b WHERE b.student_id = '"+std+"' AND a.course_id = b.course_id";
        //System.out.println(sql);
        ResultSet data = DatabaseHandler.getInstance().exeQuery(sql);
        try {
            while(data.next()){
                    tableList.add(new CourseList(data.getString("course_id"), data.getString("title"), Integer.toString(data.getInt("credit_load"))));
            }
        } catch (SQLException ex) {
            Logger.getLogger(StudentMainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        courseList.setItems(tableList);
        isTableInit = true;
    }
    private void loadCourseDetails(String courseId){
            Courses cc = new Courses();
            cc.find(courseId);
            tableList.add(new CourseList(cc.getCourseCode(), cc.getCourseTitle(), cc.getCreditLoad()));
            
    }
    private boolean checkIfStudentExists(){
        String stdId = studentId.getText().trim().toUpperCase();
        if(InputData.validateUsername(stdId)){
            Student std = new Student();
            std.find(stdId);
            return std.exists();
        }else{
            errorList.add("Invalid username entered");
            return false;
        }
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
    private void courseRegTabStidListener(){
        studId.textProperty().addListener((o, oldVal, newVal) ->{
            if(!oldVal.equals(newVal) && !studId.getText().isEmpty()){
                studentId.setText(studId.getText().toUpperCase().trim());
            }
        });
        studentId.setEditable(false);
    }

}