package attendance.system.tools;

import javafx.scene.Parent;
import javafx.stage.Stage;

public class Drag {
			private static double xOffset = 0,
			yOffset = 0;
		
	public static void makeDraggable(Parent root, Stage stage){
		root.setOnMousePressed(e ->{
			xOffset = e.getSceneX();
			yOffset = e.getSceneY();
		});
		root.setOnMouseDragged(e -> {
			stage.setX(e.getScreenX() - xOffset);
			stage.setY(e.getScreenY() - yOffset);
		});
	}
}
