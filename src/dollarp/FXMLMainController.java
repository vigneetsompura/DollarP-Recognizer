package dollarp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.opencsv.CSVWriter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import utilities.DataHandling;
import utilities.DollarP;
import utilities.Point;
import utilities.Result;
import utilities.Template;
import utilities.Tests;
import utilities.User;

/**
 * @author Vigneet Sompura
 * 
 * Controller for UI
 */


public class FXMLMainController implements Initializable {
    
    final Line sampleLine = new Line(0, 0, 140, 0);
    private ArrayList<Point> points = new ArrayList<Point>();
    private GraphicsContext gC;
    private ArrayList<Template> t;
    
    @FXML
    private Label resultContainer;
    
    @FXML
    private Canvas basecanvas;
    
    @FXML
    private Button button;
    
    @FXML
    private ScrollPane output;
    
    
    @FXML
    public void canvasMousePressed(MouseEvent event) {
        
        if(event.getButton()== MouseButton.PRIMARY){
            gC.beginPath();
            gC.moveTo(event.getX(), event.getY());
            points.add(new Point((int) event.getX(), (int) event.getY()));
            gC.lineTo(event.getX(), event.getY());
            gC.stroke();
            gC.closePath();
            updateOutput();
        }else if(event.getButton()==MouseButton.SECONDARY){
            clearCanvas();
        }
        
    }
    
    public void clearCanvas(){
        gC.clearRect(0, 0, basecanvas.getWidth(), basecanvas.getHeight());
        points.clear();
        updateOutput();
    }
    
    
    public void recognize() {
    	Result r = DollarP.recognize(new Template(points), t);
    	resultContainer.setText("Result:  "+r.getTemp().getType());
    	VBox v = new VBox();
        v.setSpacing(10);
        v.setPadding(new Insets(10));
        v.getChildren().add(new Label("NBestList"));
        v.getChildren().add(new Label("<GestureType>-<TemplateID>:<Score>"));
        for(int i=0;i< r.getStr().size();i++){
        	String p = r.getStr().get(i);
            v.getChildren().add(new Label(p));
        }
        output.setContent(v);
    }
    
    public void runUDTests() throws InterruptedException {
    	
    	VBox v = new VBox();
        v.setSpacing(10);
        v.setPadding(new Insets(10));
        output.setContent(v);
    	ArrayList<User> data = (ArrayList<User>) DataHandling.ReadObject("Data32.obj");
    	Tests[] t = (Tests[]) new Tests[data.size()];
    	DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
    	v.getChildren().add(new Label("Test Started!"));
    	output.setContent(v);
		for(int i=0; i<data.size();i++){
			t[i] = new Tests(i, data);
			t[i].setPriority(Thread.MIN_PRIORITY);
			t[i].start();
			v.getChildren().add(new Label("Thread "+i+" started" ));
			output.setContent(v);
		}
		
		for(int i=0; i<data.size(); i++) {
			t[i].join();
			v.getChildren().add(new Label("Thread "+i+" complete!" ));
			output.setContent(v);
		}
		
		try {
			String logfilepath = "log"+dtf2.format(LocalDateTime.now())+".csv";
			java.nio.file.Path p = java.nio.file.Paths.get("");
			String currDirectory = p.toAbsolutePath().toString();
			File file = new File(logfilepath);
			FileWriter outputfile = new FileWriter(file);
			CSVWriter writer = new CSVWriter(outputfile);
			v.getChildren().add(new Label("Writing Log File"));
			output.setContent(v);
			writer.writeNext(new String[] {"Recognition Log: [Vigneet Sompura] // [$P] // [MMG] // USER-DEPENDENT RANDOM-100"});
			writer.writeNext(new String[] {"User","GestureType","RandomIteration#","#ofTrainingExamples","sizeOfTrainingSet", "TrainingSetContents","Candidate","RecoginitionResult","Correct?","Score","ResultBestMatchInstance","NBestList"});
			for(int i = 0;i<data.size();i++) {
				List<String[]> logdata = t[i].log;
		        writer.writeAll(logdata);
			}
			writer.close();
			v.getChildren().add(new Label("Done writing log file ("+currDirectory+"\\"+logfilepath +")"));
			output.setContent(v);

			
			String summarypath = "summary"+dtf2.format(LocalDateTime.now())+".csv";
			CSVWriter csv = new CSVWriter(new FileWriter(new File(summarypath)));
			v.getChildren().add(new Label("Writing summary"));
			output.setContent(v);
			csv.writeNext(new String[] {"User/TrainingSetSize","1","2","3","4","5","6","7","8","9"});
			for(int i = 0; i<data.size();i++) {
				String[] s = new String[10];
				s[0] = data.get(i).getUserID();
				for(int j = 0; j<9;j++) {
					s[j+1] = t[i].accuracy[j]+"";
				}
				csv.writeNext(s);
			}
			csv.close();
			v.getChildren().add(new Label("Done writing log file ("+currDirectory+"\\"+summarypath +")"));
			output.setContent(v);
		}catch(Exception e) {
			e.printStackTrace();
		}
    }
    
    @FXML
    public void canvasMouseReleased(MouseEvent event){
        gC.setStroke(randomColor());
    }
    
    @FXML
    public void canvasMouseDragged(MouseEvent event) {
        if(event.getButton()== MouseButton.PRIMARY){
            gC.lineTo(event.getX(), event.getY());
            points.add(new Point((int) event.getX(), (int) event.getY()));
            gC.stroke();
        }
        
        updateOutput();
    }
    
    public ArrayList<User> getData() throws ParserConfigurationException, SAXException, IOException{
    	ArrayList<User> u = null;
    	try {
    		u = (ArrayList<User>) DataHandling.ReadObject("Data32.obj");
    	}catch(Exception e) {
    		e.printStackTrace();
    		DataHandling.writeObject(DataHandling.preprocessXML("Dataset\\",32), "Data32.obj");
    		u = getData();
    	}
		return u;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	Random random = new Random();
    	ArrayList<User> u = null;
		try {
			u = getData();
		} catch (Exception e) {
			e.printStackTrace();
		} 

    	t = new ArrayList<Template>();
    	for(String type: u.get(0).getGestures().keySet()) {
    		for(int i=0;i<10;i++) {
	    		User user = u.get(random.nextInt(u.size()));
	    		ArrayList<Template> temp = user.getGestures().get(type);
	    		t.add(temp.get(random.nextInt(temp.size())));
    		}
    	}
        gC = basecanvas.getGraphicsContext2D();
        gC.setLineWidth(3);
        gC.setStroke(randomColor());
    }    
    
    public void updateOutput(){
        VBox v = new VBox();
        v.setSpacing(10);
        v.setPadding(new Insets(10));
        for(Point p: points){
            v.getChildren().add(new Label("("+p.getX()+","+ p.getY()+")"));
        }
        output.setContent(v);
    }
    
    public Color randomColor(){
        Random rand = new Random();
        return Color.web("rgb("+rand.nextInt(256)+", "+rand.nextInt(256)+", "+rand.nextInt(256)+")");
    }
    
}
