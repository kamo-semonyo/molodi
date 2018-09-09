 package molodi;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.logging.Level;
 import java.util.logging.Logger;
 import javafx.application.Application;
 import javafx.fxml.FXMLLoader;
 import javafx.fxml.Initializable;
 import javafx.fxml.JavaFXBuilderFactory;
 import javafx.scene.Scene;
 import javafx.scene.control.Alert;
 import javafx.scene.control.ButtonType;
 import javafx.scene.image.Image;
 import javafx.scene.layout.AnchorPane;
 import javafx.stage.Stage;
 import javafx.stage.StageStyle;
 
 
/*
======================================
| Author: Kamohelo Semonyo
| Date: 2018-08-15
| Purpose: Main class for excuting program
|
| 
=====================================
*/ 
 
 public class Molodi extends Application{
   
   Stage stage;
   
   public void gotoMain(){
     changeView("view/molodi.fxml", "molodi.MolodiController");
   }
   
   public void gotoSplash() {
     changeView("view/splash.fxml", "molodi.SplashController");
   }
   
   private Initializable replaceScene(String sceneName) throws IOException {
     FXMLLoader loader = new FXMLLoader();
     InputStream in = Molodi.class.getResourceAsStream(sceneName);
     loader.setBuilderFactory(new JavaFXBuilderFactory());
     loader.setLocation(Molodi.class.getResource(sceneName));
             AnchorPane page;
     try
     {
       page = (AnchorPane)loader.load(in);
     } finally {
       in.close(); }
       Scene scene = new Scene(page);
       this.stage.setScene(scene);
       this.stage.sizeToScene();
       this.stage.centerOnScreen();
       return (Initializable)loader.getController();
   }
   
   private void changeView(String scene, String controllewr)
   {
     try
     {
       Class<? extends Navigation> theClass = Class.forName(controllewr).asSubclass(Navigation.class);
       Navigation menu = (Navigation)theClass.cast(replaceScene(scene));
       menu.setApp(this);
     }
     catch (IOException|ClassNotFoundException ex) {
       Logger.getLogger(Navigation.class.getName()).log(Level.SEVERE, null, ex);
       new Alert(Alert.AlertType.ERROR, "Navigation Error occured system will shutdown"
               , new ButtonType[] { ButtonType.OK }).showAndWait();
       System.exit(0);
     }
   }
   
   public void start(Stage stage) throws Exception
   {
     this.stage = stage;
     this.stage.initStyle(StageStyle.UNDECORATED);
     this.stage.getIcons().add(new Image(Molodi.class.getResourceAsStream("view/molodi.png")));
     gotoSplash();
     this.stage.show();
   }
   
   public static void main(String[] args)
   {
     launch(args);
   }
 }
