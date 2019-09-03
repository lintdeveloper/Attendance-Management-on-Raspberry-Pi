/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.lecture.ui.report.send;

import abaezcorp.sql.generator.SqlGenerator;
import attendance.system.dbconfig.DatabaseHandler;
import attendance.system.model.AttendanceReport;
import attendance.system.model.Courses;
import attendance.system.model.DefaultersList;
import attendance.system.navigation.LectureModeNavigation;
import attendance.system.navigation.NavController;
import attendance.system.tools.AlertBox;
import attendance.system.tools.Configs;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
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


/**
 * FXML Controller class
 *
 * @author Abbasogaji
 */
public class SendReportController extends NavController implements Initializable {
  /**
     * Initializes the controller class.
     */
    @FXML
    private AnchorPane mainPane;
    @FXML
    private AnchorPane activityPane;
    @FXML
    private JFXComboBox<String> receipentType;
    @FXML
    private JFXComboBox<String> courseWeekNo;
    @FXML
    private JFXButton sendButton;
    DatabaseHandler db = DatabaseHandler.getInstance();
    
    private static int msgCounts = 0;
    @FXML
    private Label infoHeader;
    @FXML
    private Label infoNote;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        animateActivityPane();
        loadComboBox();
        checkReceipient();
    }

    @FXML
    private void handleSendReport(ActionEvent event) {
        if(receipentType.getValue().equals("Parent")){
            checkIfReportSendForWeek();
        }else{
            saveDefaulterListForHod();
        }
    }
    private void loadComboBox(){
        receipentType.getItems().addAll("Parent", "HOD");
        for(int x=1; x<=12; x++){
                String y = Integer.toString(x);
                courseWeekNo.getItems().add(y);
        }
    }
    private void checkReceipient(){
                receipentType.setOnAction( e -> changeCourseWeekToCourseList());
    }
    
    private void changeCourseWeekToCourseList(){
        if(receipentType.getValue().equals("HOD")){
            courseWeekNo.setPromptText("Select Course");
            courseWeekNo.getItems().clear();
            Courses cs = new Courses();
            cs.all();
            courseWeekNo.getItems().addAll(cs.getAllCourseCode());
            sendButton.setText("Save Report");
            infoHeader.setText("Save Defaulters List");
            infoNote.setText("You are about to save attendance defaultes list in :\n" + 
                    "C-DRIVE:\\Documents\\FUTMINNA E-ATTENDANCE DOCS\\EXAM SCREENING AND DEFAULERS LIST");

            
        }else{
            infoNote.setText("Note that for you to be able to send attendance report\n" +
" you will need to have an internet connection \n" +
"and available credit in your E-bulk SMS Account");
            infoHeader.setText("Send Attendance Report");
            sendButton.setText("Send SMS");
            courseWeekNo.setPromptText("Select Week No");
            courseWeekNo.getItems().clear();
            for(int x=1; x<=12; x++){
                String y = Integer.toString(x);
                courseWeekNo.getItems().add(y);
            }
        }
    }
    
    private void checkIfReportSendForWeek(){
        msgCounts = 0;
        SqlGenerator str = new SqlGenerator();
        int totalMessage = 0;
        String sql = str.table("ATTENDANCE_REPORT_STATUS").select().whereEqual("week_number", courseWeekNo.getValue())
                .andWhereEqual("report_status", "1").get();
         
        try {
            if(db.exeQuery(sql).next()){
                AlertBox.createErrorAlert("Attendance Report already Sent before");
            }else{
                AttendanceReport at = new AttendanceReport(Integer.parseInt(courseWeekNo.getValue()));
                for(int x = 0 ; x<at.getAllStudentId().size(); x++){
                           String message =  "NAME: "+at.getAllStudentName().get(x)+" \n MATRIC: "+at.getAllStudentMatric().get(x)+"\n"+at.getAllStudentText().get(at.getAllStudentId().get(x));
                            try {
                                message = URLEncoder.encode(message ,"UTF-8");
                            } catch (UnsupportedEncodingException ex) {
                                Logger.getLogger(SendReportController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                           if(at.getAllStudentText().get(at.getAllStudentId().get(x)) != null){
                           String url = "http://api.ebulksms.com:8080/sendsms?" +
                                "username=abbasogaji@gmail.com&apikey=297ea8059fb7eec59067f09c11f674bbc8974a18&sender=FUTMX-ATR"
                                   + "&messagetext="+message+"&flash=0"
                                   + "&recipients="+at.getAllPhoneNumber().get(x)+"";
                           //NOW WE SEND EACH MESSAGE LOOP BY LOOP
                            sendHttpRequest(url);
                           }
                }
                totalMessage = at.getAllStudentId().size();
                //SEND THE MESSEAGE USING E-BULK SMS API
                //sendGetRequest();
                if(msgCounts>0){
                    AlertBox.createInfoAlert(msgCounts+"/"+totalMessage+" Messages Sent Succesfully");
                    String sql2 = str.table("ATTENDANCE_REPORT_STATUS").insert("course_id", "week_number", "report_status").values("AT111",courseWeekNo.getValue(), "1").get();
                    db.exeAction(sql2);
                }else{
                    AlertBox.createErrorAlert("Message sending failed");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SendReportController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void sendHttpRequest(String url){
                try{
                    sendGET(url);
                }catch(IOException e){
                    //System.err.println(e);
                }
    }
    private void sendGET(String url) throws IOException{
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection)obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept-Charset", "UTF-8");
        int responseCode = con.getResponseCode();
        if(responseCode == HttpURLConnection.HTTP_OK){
            msgCounts++;
        }
    
    }

    private void saveDefaulterListForHod(){
        DefaultersList dl = new DefaultersList(courseWeekNo.getValue());
            if(DefaultersList.checkIfCourseCompleted()){
                XSSFWorkbook wb = new XSSFWorkbook();
                XSSFSheet sheet = wb.createSheet(Calendar.getInstance().get(Calendar.YEAR)+"- EXAMINATION SCREENING LIST");
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
                titleCell.setCellValue(Calendar.getInstance().get(Calendar.YEAR)+" EXAMINATION SCREENING/DEFAULTERS LIST FOR "+courseWeekNo.getValue().toUpperCase());
                titleCell.setCellStyle(style);


                XSSFRow header = sheet.createRow(1);
                header.setRowStyle(style_sr);

                String titles[] = {"First Name", "Matric Number", "% AT", "Exam Status"};
                 for(int t=0; t<titles.length;t++){
                    XSSFCell cell = header.createCell(t);
                    cell.setCellStyle(style_sr);
                    cell.setCellValue(titles[t]);
                }
                sheet.autoSizeColumn(0);
                sheet.autoSizeColumn(1);
                sheet.autoSizeColumn(2);
                sheet.autoSizeColumn(3);
                sheet.setColumnWidth(0, 256*25);//256-character width
                sheet.setColumnWidth(1, 256*25);
                sheet.setZoom(100);
                for(int x=0; x<dl.getAllStudentId().size(); x++){
                    XSSFRow row = sheet.createRow(x+2);
                    row.createCell(0).setCellValue(dl.getAllStudentName().get(x));
                    row.createCell(1).setCellValue(dl.getAllStudentMatric().get(x));
                    row.createCell(2).setCellValue(dl.getPercentagePerStudent().get(dl.getAllStudentId().get(x)));
                    row.createCell(3).setCellValue(dl.getExamStatus().get(dl.getAllStudentId().get(x)));
                }
                    String name = courseWeekNo.getValue()+"-EXAM-SCREENING-DEFAULTERS-LIST-"+Calendar.getInstance().get(Calendar.YEAR)+".xlsx";
                    
                    if(Configs.storeDefaulters(name, wb)){
                        AlertBox.createInfoAlert("Exam Defaulters List successfully saved as: "+name+" in "+Configs.getPathToExamDefaultersList());
                    }; 

            }else{
                AlertBox.createInfoAlert("Course Week Not Completed");
            }
        
    }

}
