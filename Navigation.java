 package molodi;
 
 import java.net.URL;
 import java.util.ResourceBundle;
 import javafx.fxml.Initializable;
 import javafx.scene.control.Alert;
 import javafx.scene.control.ButtonType;
 
 
/*
======================================
| Author: Kamohelo Semonyo
| Date: 2018-08-15
| Purpose: Navigation class for common functions
|
| 
=====================================
*/ 
public class Navigation implements Initializable{
   
    protected Molodi app;
   
   public void setApp(Molodi App){
    this.app = App;
   }
   
   public void close() {
     System.exit(0);
   }
   
   public void minimise() {
     this.app.stage.setIconified(true);
   }
   
   public void showError(String text) {
     new Alert(Alert.AlertType.ERROR, text, new ButtonType[] { ButtonType.OK }).showAndWait();
   }
   
   public void gotoMain() {
     this.app.gotoMain();
   }
   
   public void initialize(URL location, ResourceBundle resources) {}
 
}


/* Location:              C:\Users\hp-pc\Documents\NetBeansProjects\Molodi\dist\Molodi.jar!\molodi\Navigation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */