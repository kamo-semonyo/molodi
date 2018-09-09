package molodi;

import javafx.fxml.*;
import javafx.scene.layout.*;
import java.io.*;
import molodi.files.*;
import javafx.collections.*;
import javafx.application.*;
import javafx.scene.media.*;
import javafx.scene.image.*;
import javafx.concurrent.*;
import javafx.util.*;
import javafx.scene.control.*;
import java.net.*;
import java.util.*;
import javafx.scene.input.*;
import java.util.stream.*;

 
/*
======================================
| Author: Kamohelo Semonyo
| Date: 2018-08-15
| Purpose: Class for controlling media player
|
| 
=====================================
*/ 

public class MolodiController extends Navigation{
    @FXML
    AnchorPane AnchorPane;
    @FXML
    Button playBtn;
    @FXML
    Button remove;
    @FXML
    CheckBox autoPlay;
    @FXML
    ImageView albumArt;
    @FXML
    private MediaView view;
    @FXML
    Label time;
    @FXML
    Label duration;
    @FXML
    Label playListNum;
    @FXML
    Label volValue;
    @FXML
    Label statusbar;
    @FXML
    private ListView browse;
    @FXML
    ProgressBar progress;
    @FXML
    Slider progressNobe;
    @FXML
    Slider vol;
    @FXML
    private TextField search;
    @FXML
    VBox utility;
    @FXML
    VBox visualiser;
    @FXML
    VBox volumeBox;
    
    private ObservableList<File> media;
    private ObservableList<File> playList;
    private String path;
    private double iniX;
    private double iniY;
    private final String HOME;
    private Directory directory;
    
    public MolodiController() {
        HOME = System.getProperty("user.home");
    }
    
    @Override
    public void setApp(final Molodi App) {
        Platform.runLater(() -> {
            toggleVolumeBox();
            directory = Directory.NATIVE;
            populateBrowser(new fileReader().getPath());
            progress.progressProperty().set(0.0);
            remove.setVisible(false);
            playList = FXCollections.observableArrayList();
            vol.setMax(1.0);
            vol.setValue(0.5);
            volValue.setText("50");
            autoPlay.setSelected(true);
            setDragFunction();
            setForwardAndReverse();
            return;
        });
        super.setApp(App);
    }
    //navigates to browse view
    public void gotoBrowse() {
        populateBrowser(path);
        directory = Directory.NATIVE;
        remove.setVisible(false);
    }
    // allows screen to be dragged
    public void setDragFunction() {
        AnchorPane.setOnMouseClicked(event -> {
            iniX = AnchorPane.getScene().getWindow().getX() - event.getScreenX();
            iniY = AnchorPane.getScene().getWindow().getY() - event.getScreenY();
        });
        AnchorPane.setOnMouseDragged(event -> {
            AnchorPane.getScene().getWindow().setX(event.getScreenX() + iniX);
            AnchorPane.getScene().getWindow().setY(event.getScreenY() + iniY);
        });
    }
    // goes to playlist
    public void gotoPlayList() {
        showMediaList(playList);
        directory = Directory.PLAYLIST;
        remove.setVisible(true);
    }
    // adds content to playlist
    public void addToPlayList() {
        playList.add(getMediaByName(getSelectedItem()));
        playListNum.setText("" + playList.size());
        if (playList.size() == 1) {
            playListNum.getStyleClass().add("notification");
        }
    }
    // removes content from playlist
    public void removeFromPlayList() {
        if (playList.size() == 1) {
            playList = FXCollections.observableArrayList();
            playListNum.getStyleClass().remove((Object)"notification");
            remove.setDisable(true);
        }
        else {
            playList.remove((Object)getMediaByName(getSelectedItem()));
            remove.setDisable(false);
        }
        playListNum.setText((playList.size() == 0)?"" :("" + playList.size()));
        gotoPlayList();
    }
    // loads path for application to read
    public void getMedia(final String Path) {
        path = (Path.equals("")?HOME:Path);
        (media = FXCollections.observableArrayList()).addAll((Collection)new fileReader().openFolder(path));
    }
    // gets selected item in list view
    public String getSelectedItem() {
        return browse.getSelectionModel().getSelectedItem().toString();
    }
    // goes to previous folder
    public void closeFolder() {
        if (!path.equals(HOME)) {
            final String[] temp = path.split("\\\\");
            path = "";
            for (int i = 0; i < temp.length - 1; ++i) {
                path = path + "\\" + temp[i];
            }
            populateBrowser(path);
            new fileReader().savePath(path);
        }
    }
    // decorates list view with icons
    public void filterBrowse() {
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            final ObservableList<String> filter = FXCollections.observableArrayList();
            showMediaList((ObservableList<File>)media.stream()
                    .filter(file -> file.getName().toLowerCase()
                    .contains(newValue.toString().toLowerCase()))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        });
    }
    // opens folder to view content
    public void openFolder() {
        final String filename = getSelectedItem();
        populateBrowser(getMediaByName(filename).getAbsolutePath());
        new fileReader().savePath(path);
    }
    // play button functions
    public void play() {
        if (playBtn.getId().equals("pause")) {
            view.getMediaPlayer().pause();
            togglePlayButton();
        }
        else if (view.getMediaPlayer() != null) {
            switch (view.getMediaPlayer().getStatus()) {
                case STOPPED: {
                    playFile();
                    break;
                }
                case PAUSED: {
                    view.getMediaPlayer().play();
                    togglePlayButton();
                    break;
                }
                case PLAYING: {
                    view.getMediaPlayer().pause();
                    togglePlayButton();
                    break;
                }
                case UNKNOWN: {
                    playFile();
                    break;
                }
            }
            updateProgress();
        }
        else {
            playFile();
        }
    }
    //plays media file
    public void playFile() {
        final String fileName = getSelectedItem();
        if (new fileReader().isMedia(fileName)) {
            view.setMediaPlayer(new MediaPlayer(new Media(getMediaByName(fileName).toURI().toString())));
            view.getMediaPlayer().play();
            togglePlayButton();
            view.getMediaPlayer().setOnReady(() -> {
                duration.setText(toTime(view.getMediaPlayer().getMedia().getDuration().toMinutes()));
                if (!new fileReader().isVideo(fileName)) {
                    setAlbumArt();
                }
                else {
                    albumArt.setVisible(false);
                }
                view.getMediaPlayer().setVolume(vol.getValue());
                progressNobe.setMax(view.getMediaPlayer().getMedia().getDuration().toMinutes());
                updateProgress();
            });
        }
    }
    // displays album art for mp3 files
    public void setAlbumArt() {
        albumArt.setVisible(true);
        Image art = (Image)view.getMediaPlayer().getMedia().getMetadata().get((Object)"image");
        albumArt.setImage((art == null) ? new Image(MolodiController.class.getResourceAsStream("view/molodi-plain.png")) : art);
    }
    // stops media playing
    public void stopPlay() {
        if (isMediaPlayerSet()) {
            view.getMediaPlayer().stop();
            playBtn.setId("play");
        }
    }
    // plays previous song in the list
    public void playPrevious() {
        final Integer current = browse.getSelectionModel().getSelectedIndex();
        browse.getSelectionModel().select((current == 0)?(browse.getItems().size()-1):(current - 1));
        stopPlay();
        playFile();
    }
    // plays next song in the list
    public void playNext() {
        Integer current = browse.getSelectionModel().getSelectedIndex();
        browse.getSelectionModel().select((current == browse.getItems().size()-1)?0:(current + 1));
        stopPlay();
        playFile();
    }
    // populates list with content
    public void showMediaList(ObservableList<File> list) {
        ObservableList<String> temp = FXCollections.observableArrayList();
        list.forEach(file -> temp.add(file.getName()));
        browse.setItems((ObservableList)temp);
        setListUpdate();
        setListAction();
    }
    // changes between normal and full screen
    public void toggleFullScreen() {
        app.stage.setFullScreen(!app.stage.isFullScreen());
        if (!app.stage.isFullScreen()) {
            app.stage.sizeToScene();
            visualiser.setPrefSize(visualiser.getMinWidth(), visualiser.getMinHeight());
            view.setFitHeight(265.0);
            view.setFitWidth(325.0);
            utility.setVisible(true);
            progress.setPrefWidth(240.0);
            progressNobe.setPrefWidth(240.0);
            toggleRatioPreservation();
        }
        else {
            visualiser.setPrefSize(app.stage.getWidth(), app.stage.getHeight());
            view.setFitHeight(app.stage.getHeight()-100.0);
            view.setFitWidth(app.stage.getWidth());
            utility.setVisible(false);
            progress.setPrefWidth(app.stage.getWidth()-150.0);
            progressNobe.setPrefWidth(app.stage.getWidth()-150.0);
            toggleRatioPreservation();
        }
    }
    // changes play button icon
    public void togglePlayButton() {
        playBtn.setId(playBtn.getId().equals("play") ? "pause" : "play");
    }
    // turns preservation ration on and off
    public void toggleRatioPreservation() {
        view.preserveRatioProperty().set(!view.preserveRatioProperty().get());
    }
    // shows and hides utilities
    public void toggleUtility() {
        utility.setVisible(!utility.isVisible());
        if (!utility.isVisible()) {
            app.stage.setWidth(visualiser.getWidth());
        }
        else {
            app.stage.setWidth(visualiser.getWidth() + utility.getWidth());
        }
    }
    // shows and hides volume box
    public void toggleVolumeBox() {
        volumeBox.setVisible(!volumeBox.isVisible());
    }
    // updates progress indicator
    public void updateProgress() {
        final Task task = new Task() {
            public Void call() throws Exception, InterruptedException {
                while (!isFinished() && !playBtn.getId().equals("play")) {
                    Platform.runLater(() -> {
                        time.setText(toTime(getCurrentTime()));
                        progressNobe.setValue((double)getCurrentTime());
                        updateProgressBar();
                        return;
                    });
                    Thread.sleep(1000L);
                    if (isFinished()) {
                        togglePlayButton();
                    }
                }
                if (isFinished() && autoPlay.isSelected()) {
                    playNext();
                }
                return null;
            }
        };
        new Thread((Runnable)task).start();
    }
    // updates bar
    public void updateProgressBar() {
        progress.progressProperty().set(getCurrentTime() / toDouble(duration.getText()));
    }
    // gets the current time of media
    public Double getCurrentTime() {
        return ((Duration)view.getMediaPlayer().currentTimeProperty().getValue()).toSeconds() * 0.0167;
    }
    // turns time to double
    public double toDouble(String value) {
        value = value.replace(":", ".");
        return Double.parseDouble(value);
    }
    // turns double to time
    public String toTime(final Double value) {
        return String.format("%.2f", value).replace(".", ":");
    }
    // gets media file by name
    public File getMediaByName(String name) {
        File firstfile = new File(path);
        ObservableList<File> files = FXCollections.observableArrayList();
        switch (directory) {
            case NATIVE: {
                files = media;
                break;
            }
            case PLAYLIST: {
                files = playList;
                break;
            }
        }
        for (final File file : files) {
            if (file.getName().equals(name)) {
                firstfile = file;
                break;
            }
        }
        return firstfile;
    }
    // populates browser
    public void populateBrowser(final String Path) {
        getMedia(Path);
        showMediaList(media);
        showPath();
    }
    // opens folder or plays media
    public void processFile() {
        if (getMediaByName(getSelectedItem()).isDirectory()) {
            openFolder();
        }
        else {
            stopPlay();
            playFile();
        }
    }
    // checks if a media player has been allocated
    public boolean isMediaPlayerSet() {
        return view.getMediaPlayer() != null;
    }
    // checks if media has finieshed playing
    public boolean isFinished() {
        return getCurrentTime() >= view.getMediaPlayer().getMedia().getDuration().toMinutes();
    }
    // sets list double click functionality
    public void setListAction() {
        browse.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                try {
                    processFile();
                }
                catch (Exception ex) {}
            }
        });
    }
    // sets media volume
    public void setVolume() {
        vol.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (isMediaPlayerSet()) {
                view.getMediaPlayer().setVolume((double)newValue);
                volValue.setText(String.format("%.0f", (double)newValue * 100.0));
            }
        });
    }
    // allows fast forward nd revesing media
    public void setForwardAndReverse() {
        progressNobe.setOnMouseClicked(event -> {
            if (isMediaPlayerSet()) {
                view.getMediaPlayer().seek(new Duration(progressNobe.getValue() * 60000.0));
            }
        });
    }
    // shows list icons by file type
    public void setListUpdate() {
        browse.setCellFactory((Callback)new Callback<ListView<String>, ListCell<String>>() {
            public ListCell<String> call(final ListView<String> param) {
                return new ListCell<String>() {
                    protected void updateItem(final String item, final boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText((String)null);
                            setStyle("-fx-background-image:url('add.png');");
                        }
                        else {
                            setText(item);
                            String image = MolodiController.class.getResource(new fileReader().isMedia(item)?"view/music.png" : "view/open.png").toExternalForm();
                            setStyle("-fx-background-image:url('" + image + "');");
                        }
                    }
                };
            }
        });
    }
    // displays path on view
    public void showPath() {
        statusbar.setText(path);
    }
    
    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        filterBrowse();
        setVolume();
    }
}
