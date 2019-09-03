/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system;


import abaezcorp.sql.generator.SqlGenerator;
import attendance.system.dbconfig.DatabaseHandler;
import attendance.system.model.Lecturer;
import attendance.system.server.AppServer;
import attendance.system.tools.AlertBox;
import attendance.system.tools.CircleAnimation;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 *
 * @author Abbasogaji
 */
public class MainController implements Initializable {
    
    @FXML
    private JFXComboBox<String> attendanceMode;
    
    @FXML
    private Label label;
    
    @FXML
    private JFXButton loginButton;
    
    @FXML
    private JFXTextField username;
    
    @FXML
    private JFXPasswordField password;
    
    @FXML
    private Label connectionStatusLabel;
    
    @FXML
    private Circle circ3;

    @FXML
    private Circle circ1;

    @FXML
    private Circle circ2;
    
    @FXML
    private Separator connectionLine;
    
    //GLOBAL CIRCLE ANIMATION OBJECT VARIABLE
    CircleAnimation anim1;
    CircleAnimation anim2;
    CircleAnimation anim3;
    
    //Database Instance
    DatabaseHandler db = DatabaseHandler.getInstance();
    Timer timer;
    TimerTask task;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // TODO
        disableLoginButton();
        startAppServer();
        checkServerStatus();
        
        loadComboBoxes();
        establishConnection();
        attendanceMode.getSelectionModel().selectedItemProperty().addListener((v, oldVal, newVal)-> {
            if(!AppServer.isConnectedToClient()){
                if(newVal.equals("Report Mode")){
                        loginButton.setDisable(false);
                }else{
                        loginButton.setDisable(true);
                }
            }
        
        });
    }    
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        
    }
    @FXML
    private void handleLoginAction(ActionEvent event){
        processLogin();
    }
    private void startAppServer(){
        timer = new Timer(true);
        Thread t1 = new Thread(() -> {
            AppServer.getInstance();
        });
        t1.setDaemon(true);
        t1.start();
        
    }
    private void checkServerStatus(){
        Thread t2 = new Thread(() -> {
            task = new TimerTask(){
                @Override
                public void run() {
                    if(AppServer.isConnectedToClient()){
                      Platform.runLater(() -> {
                        connectionEstablished();
                        loginButton.setDisable(false);
                        this.cancel();
                       });
                    }
                }
            
            };
            timer.scheduleAtFixedRate(task, 0, 1000);
            
           
        });
        t2.setDaemon(true);
        t2.start(); 
    }
    private void loadWindow(String title, String location){
        try{
           Parent root = FXMLLoader.load(getClass().getResource(location));
           Scene scene = new Scene(root);
           Stage stage = new Stage();
           stage.setScene(scene);
           stage.showAndWait();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    private void loadNewStage(String title, String location){
        
            try {
                ((Stage)loginButton.getScene().getWindow()).close();
                Parent root = FXMLLoader.load(getClass().getResource(location));
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle(title);
                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    private void processLogin(){
        if(userExists(username.getText().trim(), password.getText().trim())){
            Lecturer.getInstance(username.getText().trim());
            if(attendanceMode.getValue().equals("Lecture Mode")){
                loadNewStage("Lecture Attendance Mode", "/attendance/system/lecture/ui/main/lectureMain.fxml");
            }else if(attendanceMode.getValue().equals("Examination Mode")){
                AlertBox.createInfoAlert("Examination Mode will be available in Version 2");
            }else{
                loadNewStage("Attendance Reporting System", "/attendance/system/lecture/ui/report/lectureReport.fxml");
            }
        }else{
            AlertBox.createErrorAlert("Error logging in");
        }

    }
    private boolean userExists(String username, String password){
        SqlGenerator sql = new SqlGenerator();
        String str = sql.table("LECTURER").select().where("lecturer_id","=", username).andWhere("password", "=", password).get();
        ResultSet user = db.exeQuery(str);
        try {
            return user.next();
        } catch (SQLException ex) {
            return false;
        }
    }
    private void disableLoginButton(){
        loginButton.setDisable(true);
    }
    private void loadComboBoxes(){
        attendanceMode.getItems().addAll("Lecture Mode", "Examination Mode", "Report Mode");
        
    }
    private void establishConnection(){   
            tryingToConnectAnimation();

    }  
    private void tryingToConnectAnimation(){
            
            anim1 = new CircleAnimation(circ1, 500);
            anim1.runLeft();
            
            anim2 = new CircleAnimation(circ2, 700);
            anim2.runRightLeft();
            
            anim3 = new CircleAnimation(circ3, 900);
            anim3.runRight();
    }
    private void connectionEstablished(){
        anim1.stop();
        anim2.stop();
        anim3.stop();
        
        circ1.setVisible(false);
        circ2.setVisible(false);
        circ3.setVisible(false);

        connectionLine.setVisible(true);
        connectionStatusLabel.setText("Connection Established Success");
    }
    
}
