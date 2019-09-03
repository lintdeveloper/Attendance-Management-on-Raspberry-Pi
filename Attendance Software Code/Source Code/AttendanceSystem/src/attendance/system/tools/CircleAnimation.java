/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.tools;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 *
 * @author Abbasogaji
 */
public class CircleAnimation {
    
    private Node node;
    private int delay;
    private TranslateTransition trans;
    
    public CircleAnimation(){
        
    } 
    public CircleAnimation(Node node, int delay){
        this.node = node;
        this.delay = delay;
    } 
    
    public void runRight(){
        trans = new TranslateTransition();
        trans.setDelay(Duration.millis(delay));
        trans.setDuration(Duration.millis(500));
        trans.setToX(80);
        trans.setAutoReverse(true);
        trans.setCycleCount(Animation.INDEFINITE);
        trans.setNode(node);
        trans.play();
    }
    public void runRightLeft(){
        trans = new TranslateTransition();
        trans.setDelay(Duration.millis(delay));
        trans.setDuration(Duration.millis(500));
        trans.setFromX(50);
        trans.setToX(-50);
        trans.setAutoReverse(true);
        trans.setCycleCount(Animation.INDEFINITE);
        trans.setNode(node);
        trans.play();
    }
    public void runLeft(){
        trans = new TranslateTransition();
        trans.setDelay(Duration.millis(delay));
        trans.setDuration(Duration.millis(500));
        trans.setToX(-80);
        trans.setAutoReverse(true);
        trans.setCycleCount(Animation.INDEFINITE);
        trans.setNode(node);
        trans.play();
    }
     public void runLeftRight(){
        trans = new TranslateTransition();
        trans.setDelay(Duration.millis(delay));
        trans.setDuration(Duration.millis(500));
        trans.setFromX(-50);
        trans.setToX(50);
        trans.setAutoReverse(true);
        trans.setCycleCount(Animation.INDEFINITE);
        trans.setNode(node);
        trans.play();
    }
     public void stop(){
        trans.stop();
     }
     
     // BELOW ARE STATIC METHODS
    public static void runX(Node node, int delay){
        TranslateTransition trans = new TranslateTransition();
        trans.setDelay(Duration.millis(delay));
        trans.setDuration(Duration.millis(500));
        trans.setToX(50);
        trans.setAutoReverse(true);
        trans.setCycleCount(Animation.INDEFINITE);
        trans.setNode(node);
        trans.play();
    }
    public static void runY(Node node, int delay){
        TranslateTransition trans = new TranslateTransition();
        trans.setDelay(Duration.millis(delay));
        trans.setDuration(Duration.millis(500));
        trans.setToY(50);
        trans.setAutoReverse(true);
        trans.setCycleCount(Animation.INDEFINITE);
        trans.setNode(node);
        trans.play();
    }
    public static void runRight(Node node, int delay){
        TranslateTransition trans = new TranslateTransition();
        trans.setDelay(Duration.millis(delay));
        trans.setDuration(Duration.millis(500));
        trans.setToX(80);
        trans.setAutoReverse(true);
        trans.setCycleCount(Animation.INDEFINITE);
        trans.setNode(node);
        trans.play();
    }
    public static void runRightLeft(Node node, int delay){
        TranslateTransition trans = new TranslateTransition();
        trans.setDelay(Duration.millis(delay));
        trans.setDuration(Duration.millis(500));
        trans.setFromX(50);
        trans.setToX(-50);
        trans.setAutoReverse(true);
        trans.setCycleCount(Animation.INDEFINITE);
        trans.setNode(node);
        trans.play();
    }
    public static void runLeft(Node node, int delay){
        TranslateTransition trans = new TranslateTransition();
        trans.setDelay(Duration.millis(delay));
        trans.setDuration(Duration.millis(500));
        trans.setToX(-80);
        trans.setAutoReverse(true);
        trans.setCycleCount(Animation.INDEFINITE);
        trans.setNode(node);
        trans.play();
    }
     public static void runLeftRight(Node node, int delay){
        TranslateTransition trans = new TranslateTransition();
        trans.setDelay(Duration.millis(delay));
        trans.setDuration(Duration.millis(500));
        trans.setFromX(-50);
        trans.setToX(50);
        trans.setAutoReverse(true);
        trans.setCycleCount(Animation.INDEFINITE);
        trans.setNode(node);
        trans.play();
    }
     
}
