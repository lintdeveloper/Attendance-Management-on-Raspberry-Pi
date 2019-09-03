/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.tools;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Abbasogaji
 */
public class WindowLoader {
    private Parent root;
    
    public void loadWindow(String title, String location){
        try{
           Parent root = FXMLLoader.load(getClass().getResource(location));
           Scene scene = new Scene(root);
           Stage stage = new Stage();
           stage.setScene(scene);
           stage.initStyle(StageStyle.UNDECORATED);
           Drag.makeDraggable(root, stage);
           stage.showAndWait();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void loadWindowWithDecor(String title, String location){
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

    public void loadNewStage(Node node, String title, String location){
        
            try {
                ((Stage)node.getScene().getWindow()).close();
                Parent root = FXMLLoader.load(getClass().getResource(location));
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle(title);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    
}
