package hellofx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;

import java.io.File;

public class MainController {

    @FXML
    private Button playButton, pauseButton, stopButton;

    @FXML
    private Slider volumeSlider, progressSlider;

    @FXML
    private ListView<String> fileListView;

    @FXML
    private MediaView mediaView;

    private MediaPlayer mediaPlayer;

    private boolean isSliderBeingDragged = false; // variable para controlar si el slider está siendo arrastrado

    @FXML
    public void initialize() {
        // configuracion inicial del slider de volumen
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newVal.doubleValue());
            }
        });

        // configuracion para el slider de progreso
        progressSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            isSliderBeingDragged = isChanging;
            if (!isChanging && mediaPlayer != null) {
                mediaPlayer.seek(javafx.util.Duration.seconds(progressSlider.getValue()));
            }
        });

        progressSlider.setOnMouseReleased(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(javafx.util.Duration.seconds(progressSlider.getValue()));
            }
        });
    }

    @FXML
    private void onPlayButtonClicked() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    @FXML
    private void onPauseButtonClicked() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @FXML
    private void onStopButtonClicked() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @FXML
    private void onAbrirMenuItemClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("seleccionar archivo multimedia");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("archivos de video", "*.mp4", "*.mkv", "*.avi"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            fileListView.getItems().add(file.getAbsolutePath());
            playVideo(file);
        }
    }

    @FXML
    private void onListViewClicked() {
        String selectedFile = fileListView.getSelectionModel().getSelectedItem();
        if (selectedFile != null) {
            playVideo(new File(selectedFile));
        }
    }

    private void playVideo(File file) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        try {
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);

            // sincronizar el slider con el tiempo del video
            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                if (!isSliderBeingDragged) { // actualizar solo si no se está arrastrando el slider
                    progressSlider.setValue(newTime.toSeconds());
                }
            });

            mediaPlayer.setOnReady(() -> {
                progressSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
            });

            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("error al reproducir el archivo: " + e.getMessage());
        }
    }

    @FXML
    private void onSalirMenuItemClicked() {
        System.exit(0);
    }
}
