package hellofx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
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

    @FXML
    private StackPane mediaContainer;

    private MediaPlayer mediaPlayer;

    private boolean isFullSize = false; // Indica si el reproductor está en tamaño completo

    @FXML
    public void initialize() {
        // Configuración inicial del slider de volumen
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newVal.doubleValue());
            }
        });

        // Configuración inicial del slider de progreso
        progressSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging && mediaPlayer != null) {
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
    private void onResizeButtonClicked() {
        if (isFullSize) {
            // Reducir tamaño del contenedor
            mediaContainer.setPrefWidth(640);
            mediaContainer.setPrefHeight(360);
            isFullSize = false;
        } else {
            // Aumentar tamaño del contenedor
            mediaContainer.setPrefWidth(1280);
            mediaContainer.setPrefHeight(720);
            isFullSize = true;
        }
    }

    @FXML
    private void onAbrirMenuItemClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Archivo Multimedia");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de Video", "*.mp4", "*.mkv", "*.avi"));

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

            // Ajustar MediaView al contenedor
            mediaView.fitWidthProperty().bind(mediaContainer.widthProperty());
            mediaView.fitHeightProperty().bind(mediaContainer.heightProperty());

            // Configurar volumen
            mediaPlayer.setVolume(volumeSlider.getValue());

            // Actualizar progreso del slider
            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                if (!progressSlider.isValueChanging()) {
                    progressSlider.setValue(newTime.toSeconds());
                }
            });

            mediaPlayer.setOnReady(() -> {
                progressSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
            });

            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Error al reproducir el archivo: " + e.getMessage());
        }
    }

    @FXML
    private void onSalirMenuItemClicked() {
        System.exit(0);
    }
}
