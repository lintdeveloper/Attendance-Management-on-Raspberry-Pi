
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.lecture.ui.report;


import attendance.system.navigation.LectureModeNavigation;
import attendance.system.navigation.NavController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Abbasogaji
 */
public class LectureReportController extends NavController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private AnchorPane mainPane;
    @FXML
    private AnchorPane activityPane;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        animateActivityPane();
    }
    @FXML
    private void handleLoadSMSpage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LectureModeNavigation.SEND_REPORT_PAGE));
            AnchorPane homepage = loader.load();
            mainPane.getChildren().setAll(homepage);
        } catch (IOException e) {
        }
    }

    @FXML
    private void handleLoadRecieveAttendancePage(ActionEvent event) {
    
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LectureModeNavigation.RECEIVE_REPORT_PAGE));
            AnchorPane homepage = loader.load();
            mainPane.getChildren().setAll(homepage);
        } catch (IOException e) {
            
        }
    }


}
