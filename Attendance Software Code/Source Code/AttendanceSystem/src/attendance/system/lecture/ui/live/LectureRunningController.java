/*
 * Author:          Abbas Yunusa Ogaji
 * E-mail:          abbasogaji@yahoo.com
 * Description:     Lecture Attendance Controller used for receiving real-time information
 *
 */
package attendance.system.lecture.ui.live;

import attendance.system.dbconfig.DatabaseHandler;
import attendance.system.model.Attendance;
import attendance.system.navigation.LectureModeNavigation;
import attendance.system.server.AppServer;
import attendance.system.tools.AlertBox;
import attendance.system.tools.Configs;
import attendance.system.tools.FadeAnimation;
import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * FXML Controller class
 *
 * @author Abbasogaji
 */
public class LectureRunningController implements Initializable {
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
    private JFXButton saveAsPdfButton;
    @FXML
    private JFXButton printAttendanceButton;
    
    ObservableList<AttendanceList> tableList = FXCollections.observableArrayList(); 
    
    @FXML
    private TableView<AttendanceList> attendanceList;
    @FXML
    private TableColumn<AttendanceList, String> studentId;
    @FXML
    private TableColumn<AttendanceList, String> courseId;
    @FXML
    private TableColumn<AttendanceList, String> weekNoColumn;
    @FXML
    private TableColumn<AttendanceList, String> fullName;
    @FXML
    private TableColumn<AttendanceList, String> matricNumber;
    @FXML
    private TableColumn<AttendanceList, String> entryTime;
    
    Timer timer;
    TimerTask task;
    private final long recordingTime = System.currentTimeMillis() + 10000;
    //Database Instance
    DatabaseHandler db = DatabaseHandler.getInstance();
    private static String cid,wn;
    private static Double time;
    
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
                        AppServer.recieveLiveAttendance();
                        //System.out.println(AppServer.getAttendanceQuery());
                        //System.out.println(AppServer.isAttendanceAvailable());
                        if(AppServer.isAttendanceAvailable()){
                                //AppServer.recieveLiveAttendance();
                                if(db.exeAttendanceQuery(AppServer.getAttendanceQuery())){
                                    Platform.runLater(() -> {
                                            Attendance at = new Attendance();
                                            tableList.add(new AttendanceList(at.getStudentId(), at.getCourseId(), at.getFullName(), at.getMatricNumber(), at.getEntryTime(), wn));
                                            //System.out.println("Attendance Data has been added sucessfully");
                                     });  
                                }

                        }
                }
          

                
            
            };
            timer.scheduleAtFixedRate(task, 0, 1000);
           
        });
        tx.setDaemon(true);
        tx.start(); 
        
        
        
        Timer stopAttendanceTimer = new Timer(true);
        Thread t3 = new Thread(() -> {
            TimerTask stopAttendanceTask = new TimerTask(){
                @Override
                public void run() {
                        timer.cancel();
                        Platform.runLater(() -> {
                            timer.cancel();
                            attendanceStatusLabel.setText("finished");
                            attendanceStatusLabel.setTextFill(Color.RED);
                            saveAsPdfButton.setVisible(true);
                            printAttendanceButton.setVisible(true);
                            endProcessButton.setVisible(false);
                         });  
                        //Stop Attendance Timer
                        //Change Live Attendance Status to : Finished
                        //Show a Print or Save as PDF
                }

            };
            stopAttendanceTimer.schedule(stopAttendanceTask, recordingTime());
        });
        t3.setDaemon(true);
        t3.start();
        
        
        
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
    private void handleEndAttendanceProcess(ActionEvent event){
            tableList.clear();
            loadTableData();
            timer.cancel();
            Platform.runLater(() -> {
                timer.cancel();
                attendanceStatusLabel.setText("finished");
                attendanceStatusLabel.setTextFill(Color.RED);
                saveAsPdfButton.setVisible(true);
                printAttendanceButton.setVisible(true);
                endProcessButton.setVisible(false);
             });
    }
    @FXML
    private void handlePrintAttendanceReport(ActionEvent event) {
        printTable();
  
     }

    @FXML
    private void handleSaveAttendanceAsPdf(ActionEvent event) {
        saveExcel();  
    }
    @FXML
    private void handleLogOut(ActionEvent event) {
        System.exit(0);
    }
    
    //NON FXML FUNCTIONS WRITTEN HERE
    private void animateActivityPane(){
        FadeAnimation animate = new FadeAnimation(activityPane, 400);
        animate.fadeIn();
    }
    private void initColumn(){
        studentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        fullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        courseId.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        weekNoColumn.setCellValueFactory(new PropertyValueFactory<>("weekNo"));
        matricNumber.setCellValueFactory(new PropertyValueFactory<>("matricNumber"));
        entryTime.setCellValueFactory(new PropertyValueFactory<>("entryTime"));
        
       loadTableData();
    }
    private void loadTableData(){
        tableList.clear();
        Attendance at = new Attendance(cid, wn);
        if(at.exists()){
            for(int x=0; x<at.getAttendanceTotal(); x++){
                tableList.add(new AttendanceList(at.getAllStudentId().get(x), at.getAllCourseId().get(x), at.getAllFullName().get(x), at.getAllMatricNumber().get(x), at.getAllEntryTime().get(x), wn));

            }
        }

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

    private void saveExcel(){
         XSSFWorkbook wb = new XSSFWorkbook();
                XSSFSheet sheet = wb.createSheet(Calendar.getInstance().get(Calendar.YEAR)+"- ATTENDANCE REPORT");
                XSSFRow title = sheet.createRow(0);
                
                sheet.addMergedRegion(new CellRangeAddress(0,0,0,6));
                XSSFCellStyle style  = wb.createCellStyle();
                
                
                //STYLE FOR FIRST ROW
                style.setAlignment(CellStyle.ALIGN_CENTER);
                XSSFFont nf = wb.createFont();
                nf.setBold(true);
                nf.setFontHeightInPoints((short)14);
                style.setFont(nf);
                
                //STYLE FOR SECOND ROW
                XSSFCellStyle style_sr  = wb.createCellStyle();
                style_sr.setAlignment(CellStyle.ALIGN_CENTER);
                XSSFFont sf = wb.createFont();
                sf.setBold(true);
                style_sr.setFont(sf);
                
                
                title.setRowStyle(style);
                XSSFCell titleCell = title.createCell(0);
                titleCell.setCellValue(Calendar.getInstance().get(Calendar.YEAR)+" ATTENDANCE RECORD FOR "+cid.toUpperCase()+" WEEK NUMBER "+wn);
                titleCell.setCellStyle(style);
                
               
                XSSFRow header = sheet.createRow(1);
                header.setRowStyle(style_sr);
                
                String titles[] = {"Student ID","First Name", "Course ID", "Week", "Matric Number", "Entry Time"};
                 for(int t=0; t<titles.length;t++){
                    XSSFCell cell = header.createCell(t);
                    cell.setCellStyle(style_sr);
                    cell.setCellValue(titles[t]);
                }

                sheet.autoSizeColumn(0);
                sheet.autoSizeColumn(1);
                sheet.autoSizeColumn(2);
                sheet.autoSizeColumn(3);
                sheet.autoSizeColumn(4);
                sheet.autoSizeColumn(5);
                sheet.setColumnWidth(1, 256*25);//256-character width
                sheet.setColumnWidth(4, 256*25);
                sheet.setColumnWidth(5, 256*25);
                sheet.setZoom(100);//scale-150%
                
                Attendance at = new Attendance(cid, wn);
                    if(at.exists()){
                        for(int x=0; x<at.getAttendanceTotal(); x++){
                            XSSFRow row = sheet.createRow(x+2);
                            row.createCell(0).setCellValue(at.getAllStudentId().get(x));
                            row.createCell(1).setCellValue(at.getAllFullName().get(x));
                            row.createCell(2).setCellValue(at.getAllCourseId().get(x));
                            row.createCell(3).setCellValue(wn);
                            row.createCell(4).setCellValue(at.getAllMatricNumber().get(x));
                            row.createCell(5).setCellValue(at.getAllEntryTime().get(x));
                        }
                    }
                    String name = "ATR-"+cid.toUpperCase()+"-WEEK"+wn+"-"+Calendar.getInstance().get(Calendar.YEAR)+".xlsx";
                    
                    if(Configs.storeExcel(name, wb)){
                        AlertBox.createInfoAlert("Attendance successfully saved as: "+name+" in "+Configs.getPathToAttendanceRecord());
                    };
    }
    public void setCredentials(String cid, String wn, Double time, String notice){
        tableList.clear();
        courseIdWeekNoLabel.setText(cid+"  Week No - " +wn);
        LectureRunningController.time = time;
        LectureRunningController.cid = cid;
        LectureRunningController.wn = wn;
        if(!notice.isEmpty()){ 
            attendanceStatusLabel.setText("Recording-"+notice);
            attendanceStatusLabel.setTextFill(Color.web("#ffc107"));
        }
    }
    //WHEN I USE TWO FINGERPRINT IN JSON FILE THE TIME VARIABLE RETURNS A NULL--I DUNNO WHY YET BOTH I WILL TRY TO SOLVE THIS SOON
    private int recordingTime(){
        //DEBUGGING-CODE
        //if i dont put system.out.println() time will return null i dunno why
        System.out.println(time);
        int rt = time.intValue() * 3600 * 1000;
        return rt;

    }
  


    
    
}
