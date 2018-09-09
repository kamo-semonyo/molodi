package molodi;

import javafx.util.*;
import javafx.animation.*;
import java.net.*;
import java.util.*;

 
/*
======================================
| Author: Kamohelo Semonyo
| Date: 2018-08-15
| Purpose: Class for splash view
|
| 
=====================================
*/ 

public class SplashController extends Navigation{
    @Override
    public void setApp(final Molodi App) {
        super.setApp(App);
        PauseTransition pause = new PauseTransition(Duration.seconds(1.0));
        pause.setOnFinished(e -> this.gotoMain());
        pause.play();
    }
    
    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
    }
}
