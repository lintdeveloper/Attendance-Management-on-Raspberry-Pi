/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.lecture.ui.report.receive;

import attendance.system.dbconfig.DatabaseHandler;
import attendance.system.model.Attendance;
import attendance.system.navigation.LectureModeNavigation;
import attendance.system.navigation.NavController;
import attendance.system.server.AppServer;
import attendance.system.tools.FadeAnimation;
import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterAttributes;
import javafx.print.PrinterJob;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Abbasogaji
 */
public class ReceiveReportController extends NavController implements Initializable {
 @FXML
    private AnchorPane mainPane;
    @FXML
    private AnchorPane activityPane;
    @FXML
    private AnchorPane attendancePage;
    @FXML
    private Label courseIdWeekNoLabel;
    @FXML
    private Label attendanceStatusLabel;
    @FXML
    private JFXButton endProcessButton;
    @FXML
    private JFXButton printAttendanceButton;
    
    ObservableList<ReportList> tableList = FXCollections.observableArrayList(); 
    
    @FXML
    private TableView<ReportList> attendanceList;
    @FXML
    private TableColumn<ReportList, String> studentId;
    @FXML
    private TableColumn<ReportList, String> courseId;
    @FXML
    private TableColumn<ReportList, String> weekNoColumn;
    @FXML
    private TableColumn<ReportList, String> fullName;
    @FXML
    private TableColumn<ReportList, String> matricNumber;
    @FXML
    private TableColumn<ReportList, String> entryTime;
    
    Timer timer;
    TimerTask task;
    //Database Instance
    DatabaseHandler db = DatabaseHandler.getInstance();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        animateActivityPane();
        initColumn();
        checkAttendanceTaken();
    } 
    private void checkAttendanceTaken(){
        timer = new Timer(true);
        Thread ta = new Thread(() -> {
            AppServer.getInstance();
        });
        ta.setDaemon(true);
        ta.start();
        
        Thread tx = new Thread(() -> {
            task = new TimerTask(){
                @Override
                public void run() {
                        AppServer.recieveAttendanceReport();
                        if(AppServer.isReportAvailable()){
                                if(db.exeAttendanceQuery(AppServer.getReportQuery())){
                                    Platform.runLater(() -> {
                                            Attendance at = new Attendance();
                                            tableList.add(new ReportList(at.getStudentId(), at.getCourseId(), at.getFullName(), at.getMatricNumber(), at.getEntryTime(), getWeekNoFromQuery()));
                                            System.out.println("Attendance Data has been added sucessfully");
                                     });  
                                }
                        }
                }
          
            };
            timer.scheduleAtFixedRate(task, 0, 200);
           
        });
        tx.setDaemon(true);
        tx.start(); 
        
        
    }
    @FXML
    private void handleEndAttendanceProcess(ActionEvent event){
            //tableList.clear();
            //loadTableData();
            timer.cancel();
            Platform.runLater(() -> {
                timer.cancel();
                attendanceStatusLabel.setText("finished");
                attendanceStatusLabel.setTextFill(Color.RED);
                printAttendanceButton.setVisible(true);
                endProcessButton.setVisible(false);
             });
    }
    @FXML
    private void handlePrintAttendanceReport(ActionEvent event) {
        printTable();
  
     }
    private void initColumn(){
        studentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        fullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        courseId.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        weekNoColumn.setCellValueFactory(new PropertyValueFactory<>("weekNo"));
        matricNumber.setCellValueFactory(new PropertyValueFactory<>("matricNumber"));
        entryTime.setCellValueFactory(new PropertyValueFactory<>("entryTime"));
        attendanceList.setItems(tableList);
    }
    
    private void printTable(){
       PrinterJob printerJob = PrinterJob.createPrinterJob();
       Printer printer = Printer.getDefaultPrinter();
       PageLayout pageLayout  = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);
       PrinterAttributes attr = printer.getPrinterAttributes();
        Scale scale = new Scale(0.6, 0.6);
        attendancePage.getTransforms().add(scale);
    
       if(printerJob.showPrintDialog(new Stage().getOwner()) && printerJob.printPage(pageLayout, attendancePage)){
            printerJob.endJob();
            attendancePage.getTransforms().clear();
       }
    attendancePage.getTransforms().clear();
    }
  
    private String getWeekNoFromQuery(){
        return AppServer.getReportQuery().substring(145, 146);
    }

    
    
  
    
}
