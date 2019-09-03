/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.tools;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 *
 * @author Abbasogaji
 */
public class AlertBox {
    
	public static void createInfoAlert(String msg){
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}
	public static void createInfoAlert(String title, String msg){
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}
	public static void createErrorAlert(String msg){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}
	public static void createErrorAlert(String title, String msg){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}
	public static boolean createConfirmationAlert(String msg){
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setHeaderText(null);
		alert.setContentText(msg);


		Optional<ButtonType> response = alert.showAndWait();

		if(response.get() == ButtonType.OK){
			return true;
		}else{
			return false;
		}

	}
	public static boolean createConfirmationAlert(String title, String msg){
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(msg);

		Optional<ButtonType> response = alert.showAndWait();

		if(response.get() == ButtonType.OK){
			return true;
		}else{
			return false;
		}

	}
}
