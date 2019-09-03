/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.lecture.ui.live;

import attendance.system.dbconfig.DatabaseHandler;
import attendance.system.dbconfig.DbSqliteConstants;
import attendance.system.model.Courses;
import attendance.system.navigation.LectureModeNavigation;
import attendance.system.server.AppServer;
import attendance.system.tools.AlertBox;
import attendance.system.tools.FadeAnimation;
import attendance.system.tools.InputData;
import attendance.system.tools.JsonFile;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author Abbasogaji
 */
public class LectureLiveController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private AnchorPane mainPane;

    @FXML
    private AnchorPane activityPane;

    @FXML
    private JFXButton startLectureButton;
    @FXML
    private JFXComboBox<Integer> weekNo;
    @FXML
    private JFXComboBox<String> dura;
    @FXML
    private VBox courseDetails;
    @FXML
    private Label courseDetailLabel;
    @FXML
    private HBox courseTitleBox;
    @FXML
    private Label courseTitleLabel;
    @FXML
    private HBox DepartmentBox;
    @FXML
    private Label DepartmentLabel;
    @FXML
    private JFXButton startNowButton;
    @FXML
    private JFXComboBox<String> courseCode;
    
    DatabaseHandler db = DatabaseHandler.getInstance();

    
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        animateActivityPane();
        courseDetails.setVisible(false);
        startNowButton.setVisible(false);
        loadComboBox();
        listenToCourseCode();
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
    private void handleStartAttendance(ActionEvent event) {
        
        //create json file then add the list of students with their fingeprint id and hash key
        //Send a json file
        //initialize json object and array
        JSONObject obj = new JSONObject();
        JSONArray fparray_val;
        JSONArray tfparray_val;
        JSONArray fingerprints = new JSONArray();
        
        
//        SqlGenerator str = new SqlGenerator();
        String sql = "SELECT a.student_id, a.finger_print_id, a.finger_print_hash, a.finger_print_id_left, a.finger_print_hash_left  FROM students a,"
                + ""+DbSqliteConstants.STUDENT_REG_TABLE_NAME+ " b WHERE  a.student_id = b.student_id AND b.course_id ='"+courseCode.getValue().trim()+"'";
        ResultSet data = db.exeQuery(sql);
        int counter = 0;
        try {
            while(data.next()){
                ++counter;
                if(data.getString("finger_print_id")!=null){
                    fparray_val  = new JSONArray();
                    fparray_val.add(""+data.getString("student_id")+"");
                    fparray_val.add(""+data.getString("finger_print_id")+"");
                    fparray_val.add(""+data.getString("finger_print_hash")+"");
                    fingerprints.add(fparray_val);
                    
                }
                //NEW CODE ADDED HERE, DATE: 21/09/2018
                //IF IT DOESNT WORK I WILL TRY TO CREATE A DIFFERENT JSON ARRAY OBJECT OR WHILE THE NEW JSON ARRAY OUT THE WHILE LOOP
                    
                if(data.getString("finger_print_id_left")!=null){
                    tfparray_val  = new JSONArray();
                    tfparray_val.add(""+data.getString("student_id")+"");
                    tfparray_val.add(""+data.getString("finger_print_id_left")+"");
                    tfparray_val.add(""+data.getString("finger_print_hash_left")+"");
                    fingerprints.add(tfparray_val);
                }
                
            }
            JSONArray lectureDetails = new JSONArray();
            lectureDetails.add(""+courseTitleLabel.getText().trim()+"");
            lectureDetails.add(""+weekNo.getValue()+"");
            lectureDetails.add(""+courseCode.getValue()+"");
            lectureDetails.add(InputData.extractDouble(dura.getValue()));

            obj.put("fingerprints", fingerprints);
            obj.put("total_size", counter);
            obj.put("lecture_details", lectureDetails);
        
        //System.out.println(obj.toJSONString());
        
        } catch (SQLException ex) {
            Logger.getLogger(LectureLiveController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(JsonFile.create("attendanceData", obj)){
            AlertBox.createConfirmationAlert("Json File has been created");
            AppServer.sendJsonFile("attendanceData.json");
            loadLiveRunningAttendancePage();
        }
    }
    
    @FXML
    private void handleLogOut(ActionEvent event){
        System.exit(0);
    }
    /**
     * NON-FXML METHODS
     */
    private void loadLiveRunningAttendancePage(){
        String noticeInfo;
        noticeInfo = "";
        try {
              String sql =   "SELECT id FROM LECTURE_ATTENDANCE WHERE course_id='"+courseCode.getValue()+"' AND week_number='"+weekNo.getValue()+"'";
              ResultSet rs = db.exeQuery(sql);
                try {
                    if(rs.next()){
                        noticeInfo = "Please note this lecture attendance has already been taken before, real-time recording of already stored will not appear on the table";
                    }
                } catch (SQLException ex) {
    //                Logger.getLogger(LectureLiveController.class.getName()).log(Level.SEVERE, null, ex);
                }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(LectureModeNavigation.START_LIVE_RUNING_PAGE));
            AnchorPane homepage = loader.load();
            LectureRunningController edits = (LectureRunningController)loader.getController();
            edits.setCredentials(courseCode.getValue(), Integer.toString(weekNo.getValue()), InputData.extractDouble(dura.getValue()), noticeInfo);
            mainPane.getChildren().setAll(homepage);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
    private void animateActivityPane(){
        FadeAnimation animate = new FadeAnimation(activityPane, 400);
        animate.fadeIn();
    }
    private void loadComboBox(){
        Courses cs = new Courses();
        cs.all();
        courseCode.getItems().addAll(cs.getAllCourseCode());
        dura.getItems().addAll("1 HOUR", "1.5 HOURS", "2 HOURS", "2.5 HOURS", "3 HOURS", "3.5 HOURS", "4 HOURS");
        
    }

    private double getDuration(){
        return InputData.extractDouble(dura.getValue().trim());
    }
    private void listenToCourseCode(){
        courseCode.setOnAction(e -> loadCourseDetails());
    }
    private void loadCourseDetails(){
        weekNo.getItems().clear();
        Courses cs = new Courses();
        cs.find(courseCode.getValue().trim());
        for(int x=1; x<=Integer.parseInt(cs.getNoOfWeeks()); x++){
                weekNo.getItems().add(x);
        }
        
        //LOAD DATA IN
        courseTitleLabel.setText(cs.getCourseTitle());
        DepartmentLabel.setText(cs.getDepartment());
        courseDetails.setVisible(true);
        startNowButton.setVisible(true);
        FadeAnimation animate = new FadeAnimation(courseDetails, 300);
        animate.fadeIn();
    }
    
}
