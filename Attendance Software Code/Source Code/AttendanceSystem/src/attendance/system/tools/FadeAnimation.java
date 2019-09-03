/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.tools;

/**
 *
 * @author Abbasogaji
 */
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;
public class FadeAnimation {

	private FadeTransition fade;
	public FadeAnimation(Node node){
		fade = new FadeTransition(Duration.millis(2000), node);

	}
	public FadeAnimation(Node node, int dtn){
		fade = new FadeTransition(Duration.millis(dtn), node);
	}
	public void fadeIn(){
		fade.setFromValue(0.0);
		fade.setToValue(1.0);
		fade.setCycleCount(1);
		fade.setAutoReverse(false);
		fade.play();
	}
	public void fadeOut(){
		fade.setFromValue(1.0);
		fade.setToValue(0.0);
		fade.setCycleCount(1);
		fade.setAutoReverse(false);
		fade.play();
	}


}
