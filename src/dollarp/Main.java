/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dollarp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import utilities.DataHandling;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.jfoenix.controls.JFXButton;

/**
 * @author Vigneet Sompura
 * Initializer for User Interface.
 */
public class Main extends Application {
    
    
    @Override
    public void start(Stage stage) throws Exception {
        
        stage.setTitle("$P Recognizer");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLMain.fxml"));
        Parent root = (Parent) loader.load();
        Scene scene = new Scene(root);
        Canvas basecanvas = (Canvas) loader.getNamespace().get("basecanvas");
        Pane canvasPane = (Pane) loader.getNamespace().get("canvaspane");
        basecanvas.setCursor(Cursor.CROSSHAIR);
        DropShadow ds = new DropShadow();
        ds.setOffsetY(0.5);
        ds.setOffsetX(0.5);
        ds.setColor(Color.GRAY);
        basecanvas.setEffect(ds);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
       
    }

    /**
     * @param args the command line arguments
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
    	java.nio.file.Path p = java.nio.file.Paths.get("");
    	System.out.println(p.toAbsolutePath().toString());
    	File f = new File("Data32.obj");
    	if(!f.exists()) {
    		DataHandling.writeObject(DataHandling.preprocessXML("Dataset\\",32), "Data32.obj");
    	}
        launch(args);
    }
    
}
