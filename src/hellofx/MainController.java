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
    private Button playButton, pauseButton, stopButton, fullscreenButton;

    @FXML
    private Slider volumeSlider, progressSlider;

    @FXML
    private ListView<String> fileListView;

    @FXML
    private MediaView mediaView;

    @FXML
    private StackPane mediaContainer; // contenedor del video

    private MediaPlayer mediaPlayer;

    private boolean isFullscreen = false; // estado de pantalla completa

    private double initialWidth = 640; // tamaño inicial
    private double initialHeight = 360; // tamaño inicial

    @FXML
    public void initialize() {
        // configurar el slider de volumen
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newVal.doubleValue());
            }
        });

        // configurar el slider de progreso
        progressSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging && mediaPlayer != null) {
                mediaPlayer.seek(javafx.util.Duration.seconds(progressSlider.getValue()));
            }
        });

        // manejar seleccion en la biblioteca
        fileListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                playVideo(new File(newSelection));
            }
        });

        // inicializar el tamaño del contenedor
        mediaContainer.setPrefWidth(initialWidth);
        mediaContainer.setPrefHeight(initialHeight);
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
    private void onFullscreenButtonClicked() {
        if (isFullscreen) {
            // volver al tamaño inicial
            mediaContainer.setPrefWidth(initialWidth);
            mediaContainer.setPrefHeight(initialHeight);
            mediaView.fitWidthProperty().unbind();
            mediaView.fitHeightProperty().unbind();
            mediaView.setFitWidth(initialWidth); // establecer el tamaño inicial manualmente
            mediaView.setFitHeight(initialHeight);
    
            isFullscreen = false;
    
            // cambiar texto del botón
            fullscreenButton.setText("⛶");
        } else {
            // expandir al tamaño completo
            mediaContainer.setPrefWidth(mediaContainer.getParent().layoutBoundsProperty().get().getWidth());
            mediaContainer.setPrefHeight(mediaContainer.getParent().layoutBoundsProperty().get().getHeight() - 80); // margen para la barra
            mediaView.fitWidthProperty().bind(mediaContainer.widthProperty());
            mediaView.fitHeightProperty().bind(mediaContainer.heightProperty());
    
            isFullscreen = true;
    
            // cambiar texto del botón
            fullscreenButton.setText("⤢");
        }
    }
    

    @FXML
    private void onAbrirMenuItemClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo multimedia");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de video", "*.mp4", "*.mkv", "*.avi"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            fileListView.getItems().add(file.getAbsolutePath());
            playVideo(file);
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

            // configurar volumen
            mediaPlayer.setVolume(volumeSlider.getValue());

            // actualizar progreso del slider
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

    @FXML
    private void onResizeButtonClicked() {
        // alterna el tamaño del contenedor entre grande y pequeño (en verda no hace nada pero si lo borro me da problems ayiyiyiyiyiyiyti)
        if (mediaContainer.getPrefWidth() == 640) {
            mediaContainer.setPrefWidth(1280);
            mediaContainer.setPrefHeight(720);
        } else {
            mediaContainer.setPrefWidth(initialWidth);
            mediaContainer.setPrefHeight(initialHeight);
        }
    }
}
