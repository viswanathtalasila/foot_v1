package sample;

import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.abs;

public class Controller implements Initializable {
    GraphicsContext gc;
    final DoubleProperty zoomProperty = new SimpleDoubleProperty(20);
    ArrayList<String> frames=new ArrayList<>();
    File folder=new File("D:\\Work\\javafx_projects\\Datasets\\");
    File[] images=folder.listFiles();
    Boolean loaded=false;
    int framepointer=0;
    String n;
    int s;
    private Boolean flag=true;
    private Boolean zoomflag=false;
    private Boolean framesStored = false;
    private Boolean playing=true;
    private Point2D initial;
    private Point2D end;

    @FXML public ImageView imageview;
    @FXML public Canvas canvas;
    @FXML public ColorPicker cp;
    @FXML public Label label;
    @FXML public Slider slider;
    @FXML public RadioButton rd1;
    @FXML public RadioButton rd2;
    @FXML public ComboBox combo;
    @FXML public ScrollPane scrollPane;
    @FXML public Button zm;
    @FXML public Pane pane1;
    @FXML public AnchorPane apane;
    @FXML public Pane pane;
    @FXML public ScrollBar scrollframes;

    ////////////////////////////////////////////////////////
    //this sub function displays the next image in sequence
    public void btn(ActionEvent event)throws IOException{  //next button
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        if(framesStored==false){
            load();
            framesStored=true;
        }
        display();  //call and display the image
        doodle();   //activate doodle controls
        //gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        framepointer+=1;
    }

    ////////////////////////////////////////////////////////
    //this sub function displays the previous image in sequence
    public void back(ActionEvent event)throws IOException{  //back button
        if(framesStored==false){
            load();
            framesStored=true;
        }
        display();  //call and display the image
        doodle();   //activate doodle controls
        if(framepointer >=1){
            framepointer -=1;  //control index from going to zero or negative
        }
    }

    ////////////////////////////////////////////////////////
    //this sub function scrolls through the images in sequence
    public void scrolling(ActionEvent event)throws IOException{  //scrolling button
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

        if(framesStored==false){
            load();
            framesStored=true;
        }
        scrollframes.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                framepointer = newValue.intValue();
                try {
                    display();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                doodle();  //call and activate doodle controls
                //gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
            }
        });
    }

    public void clear(ActionEvent event)throws IOException{
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
    }
    public void erase(ActionEvent event)throws IOException{

        canvas.setOnMousePressed(e -> {
            gc.clearRect(e.getX() - 2, e.getY() - 2, 25, 25);
        });
        canvas.setOnMouseDragged(e -> {
            gc.clearRect(e.getX() - 2, e.getY() - 2, 25, 25);
        });
        pane.setOnMouseClicked(null);
        pane.setOnMouseReleased(null);
    }
    public void rs(ActionEvent event)throws IOException{           //players
        if(rd1.isSelected()){
            teams("C:\\\\Users\\\\viswa\\\\Desktop\\\\football\\\\scene1.png");
        }
        if(rd2.isSelected()){
            teams("C:\\\\Users\\\\viswa\\\\Desktop\\\\football\\\\scene2.png");
        }
    }

    public void save(ActionEvent event)throws IOException{
        hold();
    }

    public void reset(ActionEvent event)throws IOException{
        pane.setScaleX(1);
        pane.setScaleY(1);
        pane.setLayoutX(0);
        pane.setLayoutY(0);
        pane.setOnMouseClicked(null);
        pane.setOnMouseReleased(null);
    }


    public void refresh(ActionEvent event)throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene rescene=new Scene(root);
        Stage stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(rescene);
        stage.setTitle("refreshed");
        stage.show();
    }
    public void play(ActionEvent event)throws IOException{
        videothread t1=new videothread();
        if(framesStored==false){
            load();
            framesStored=true;
        }
        if(playing==true){
            flag=true;
            t1.start();
            playing=false;
        }
        else{
            playing=true;
            flag=false;
        }
    }
    public void load(){
        for(File image:images)
        {
            n=image.getName();
            String path="D:\\Work\\javafx_projects\\Datasets\\"+n;
            frames.add(path);
        }
    }
    public void display() throws IOException {
        Mat image = Imgcodecs.imread(frames.get(framepointer));
        s=framepointer;
        //actual image is scaled to fit the imageview pane. In order to zoom correctly
        //we need to get the scaling between actual image and imageview pane size
        float scaleheight = (float) image.height() / (float) imageview.getFitHeight();
        float scalewidth =  (float) image.width() / (float) imageview.getFitWidth();
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", image, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage bufImage = ImageIO.read(in);
        WritableImage writableImage = SwingFXUtils.toFXImage(bufImage, null);
        imageview.setImage(writableImage);
        //actual image size is scaled to fit the imageview pane size
        if (zoomflag==true) {
            int zoomwidth = (int) (end.getX() - initial.getX());
            int zoomheight = (int) (end.getY() - initial.getY());
            Rectangle2D viewportRect1 = new Rectangle2D(
                    initial.getX() * scalewidth,
                    initial.getY() * scaleheight,
                    zoomwidth * scalewidth,
                    zoomheight * scaleheight);
            imageview.setViewport(viewportRect1);
            //imageview.resize(1000,600);
            imageview.setPreserveRatio(true);
            imageview.setSmooth(true);
            imageview.setCache(true);
        }
    }

    //this sub function takes care of the manual drawing doodling work
    public void doodle(){
        gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        canvas.setOnMousePressed(e -> {
            gc.beginPath();
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
            //System.out.println(e.getX());  //prints the starting X point
            //System.out.println(e.getY());  //prints the starting Y point
        });
        canvas.setOnMouseDragged(e -> {
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
            //System.out.println(e.getX());  //prints the X point as cursor is dragged
            //System.out.println(e.getY());  //prints the Y point as cursor is dragged
        });
        cp.setValue(Color.BLACK);
        cp.setOnAction(e -> {
            gc.setStroke(cp.getValue());
        });
    }


    ObservableList<String> list=FXCollections.observableArrayList("CIRCLE","RECTANGLE","TRIANGLE");

    public void initialize(URL location, ResourceBundle resources) {
        combo.setItems(list);

    }
    public void combobox(ActionEvent event)throws IOException{
        gc = canvas.getGraphicsContext2D();
        pane.setOnMouseClicked(null);
        pane.setOnMouseReleased(null);
        if(combo.getValue()=="CIRCLE"){
            canvas.setOnMousePressed(event1 -> {
                gc.strokeOval(event1.getX(),event1.getY(),50,50);
                canvas.setOnMouseDragged(null);
            });
        }
        if(combo.getValue()=="RECTANGLE"){
            canvas.setOnMousePressed(event1 -> {
                gc.strokeRect(event1.getX(),event1.getY(),50,50);
                canvas.setOnMouseDragged(null);

            });
        }
        if(combo.getValue()=="TRIANGLE"){
            canvas.setOnMousePressed(event1 -> {
                gc.strokePolygon(new double[]{event1.getX(),event1.getX()+30,event1.getX()+60},new double[]{event1.getY(),event1.getY()+30,event1.getY()},3);
                canvas.setOnMouseDragged(null);
            });
        }
    }

    public void zoom(ActionEvent event){
        canvas.setOnMousePressed(null);
        canvas.setOnMouseDragged(null);
        zoommm();

    }


    public void doodlebtn(ActionEvent e)
    {
        doodle();
        pane.setOnMouseClicked(null);
        pane.setOnMouseReleased(null);

    }

    public void zoommm(){
        pane.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                zoomflag=true;
                initial = new Point2D((event.getX()), event.getY());
                //System.out.println(initial.getX());
                //System.out.println(initial.getY());
                pane.setOnMouseReleased(new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent event) {
                        end = new Point2D((event.getX()), event.getY());
                        //System.out.println(end.getX());
                        //System.out.println(end.getY());
                    }
                });
            }
        });
    }


    public void teams(String s)throws IOException{           //players
        canvas.setOnMousePressed(e -> {
            String file = s;
            Mat image = Imgcodecs.imread(file);
            MatOfByte matOfByte = new MatOfByte();
            Imgcodecs.imencode(".jpg", image, matOfByte);
            byte[] byteArray = matOfByte.toArray();
            InputStream in = new ByteArrayInputStream(byteArray);
            BufferedImage bufImage = null;
            try {
                bufImage = ImageIO.read(in);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            WritableImage writableImage = SwingFXUtils.toFXImage(bufImage, null);
            gc.drawImage(writableImage,e.getX(),e.getY());
        });
    }

    public void hold() throws IOException {                 //save
        File file =new File(frames.get(s));
        //System.out.println(frames.get(s));
        WritableImage writableImage = new WritableImage(356, 255);
        WritableImage snapshot = pane.snapshot(new SnapshotParameters(), writableImage);
        //System.out.println(snapshot.getWidth());
        //System.out.println(snapshot.getHeight());
        ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
    }

    class videothread extends Thread{
        @Override
        public void run(){
            System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
            while(flag){
                try {
                    display();
                    doodle();
                    gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());

                } catch (IOException e) {
                    e.printStackTrace();
                }

                framepointer++;
            }
        }
    }


}
