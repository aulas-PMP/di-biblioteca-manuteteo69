package hellofx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;

import java.io.File;

public class MainController {

    @FXML
    private Button playButton, pauseButton, stopButton, fullscreenButton, speedButton;

    @FXML
    private Slider volumeSlider, progressSlider;

    @FXML
    private ListView<String> fileListView;

    @FXML
    private MediaView mediaView;

    @FXML
    private StackPane mediaContainer; // contenedor del video

    @FXML
    private VBox leftPanel, rightPanel; // paneles laterales

    private MediaPlayer mediaPlayer;

    private boolean isFullscreen = false; // estado de pantalla completa
    private boolean isDoubleSpeed = false; // estado de velocidad (x2 o normal)
    private boolean isLeftPanelCollapsed = false; // estado del panel izquierdo
    private boolean isRightPanelCollapsed = false; // estado del panel derecho

    private double initialWidth = 640; // tamaño inicial
    private double initialHeight = 360; // tamaño inicial
    @FXML
    private ImageView placeholderImage; // Imagen para fondo en archivos de audio

    @FXML
    public void initialize() {
        // Configurar el slider de volumen
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newVal.doubleValue());
            }
        });
    
        // Configurar el slider de progreso
        progressSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging && mediaPlayer != null) {
                mediaPlayer.seek(javafx.util.Duration.seconds(progressSlider.getValue()));
            }
        });
    
        // Manejar selección en la biblioteca
        fileListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                playVideo(new File(newSelection));
            }
        });

        // Cargar la imagen desde resources
    String imagePath = getClass().getResource("audio_placeholder.jpg").toExternalForm();
    placeholderImage.setImage(new javafx.scene.image.Image(imagePath));

        // Ocultar la imagen de fondo al inicio
        placeholderImage.setVisible(false);
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
            mediaView.setFitWidth(initialWidth);
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
    private void onSpeedButtonClicked() {
        toggleSpeed();
    }

    @FXML
    private void toggleSpeed() {
        if (mediaPlayer != null) {
            if (isDoubleSpeed) {
                // volver a velocidad normal
                mediaPlayer.setRate(1.0);
                speedButton.setText("x2");
                isDoubleSpeed = false;
            } else {
                // establecer velocidad x2
                mediaPlayer.setRate(2.0);
                speedButton.setText("/2");
                isDoubleSpeed = true;
            }
        }
    }

    @FXML
    private void onAbrirMenuItemClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo multimedia");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de video", "*.mp4", "*.mkv", "*.avi", "*.mp3"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            fileListView.getItems().add(file.getAbsolutePath());
            playVideo(file);
        }
    }

    @FXML
    private Label currentTimeLabel; // Muestra el tiempo actual
    @FXML
    private Label totalTimeLabel; // Muestra la duración total
    
    private void playVideo(File file) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
    
        try {
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
    
            // Configurar el volumen
            mediaPlayer.setVolume(volumeSlider.getValue());
    
            // Configurar visibilidad del video y la imagen de fondo
            mediaPlayer.setOnReady(() -> {
                String fileName = file.getName().toLowerCase();
                if (fileName.endsWith(".mp3")) {
                    // Si el archivo es de audio (mp3), mostrar la imagen y ocultar el video
                    placeholderImage.setVisible(true);
                    mediaView.setVisible(false);
                } else {
                    // Si el archivo es de video, mostrar el video y ocultar la imagen
                    placeholderImage.setVisible(false);
                    mediaView.setVisible(true);
                }
    
                // Configurar el slider de progreso y los tiempos
                progressSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
                updateTotalTimeLabel(mediaPlayer.getTotalDuration().toSeconds());
            });
    
            // Actualizar el tiempo actual del slider
            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                if (!progressSlider.isValueChanging()) {
                    progressSlider.setValue(newTime.toSeconds());
                }
                updateCurrentTimeLabel(newTime.toSeconds());
            });
    
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Error al reproducir el archivo: " + e.getMessage());
        }
    }
    
    
    // tiempo corriendo
    private void updateCurrentTimeLabel(double currentSeconds) {
        currentTimeLabel.setText(formatTime(currentSeconds));
    }
    
    // duracion total del archivo
    private void updateTotalTimeLabel(double totalSeconds) {
        totalTimeLabel.setText(formatTime(totalSeconds));
    }
    
    // duracion en minutos y s
    private String formatTime(double seconds) {
        int minutes = (int) seconds / 60;
        int secs = (int) seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }
    
    @FXML
    private void onSalirMenuItemClicked() {
        System.exit(0);
    }

    @FXML
    private void onLeftPanelToggleClicked() {
        if (isLeftPanelCollapsed) {
            leftPanel.setPrefWidth(200);
            leftPanel.getChildren().get(0).setRotate(0); // rotar el botón hacia arriba
        } else {
            leftPanel.setPrefWidth(50);
            leftPanel.getChildren().get(0).setRotate(180); // rotar el botón hacia abajo
        }
        isLeftPanelCollapsed = !isLeftPanelCollapsed;
    }

    @FXML
    private void onRightPanelToggleClicked() {
        if (isRightPanelCollapsed) {
            rightPanel.setPrefWidth(200);
            rightPanel.getChildren().get(0).setRotate(0); // rotar el botón hacia arriba
        } else {
            rightPanel.setPrefWidth(50);
            rightPanel.getChildren().get(0).setRotate(180); // rotar el botón hacia abajo
        }
        isRightPanelCollapsed = !isRightPanelCollapsed;
    }

    @FXML
    private void onResizeButtonClicked() {
        // alternar entre tamaño inicial y pantalla completa
        if (isFullscreen) {
            mediaContainer.setPrefWidth(initialWidth);
            mediaContainer.setPrefHeight(initialHeight);
            mediaView.fitWidthProperty().unbind();
            mediaView.fitHeightProperty().unbind();
            mediaView.setFitWidth(initialWidth);
            mediaView.setFitHeight(initialHeight);

            isFullscreen = false;
        } else {
            double availableWidth = mediaContainer.getParent().layoutBoundsProperty().get().getWidth();
            double availableHeight = mediaContainer.getParent().layoutBoundsProperty().get().getHeight() - 80;

            mediaContainer.setPrefWidth(availableWidth);
            mediaContainer.setPrefHeight(availableHeight);
            mediaView.fitWidthProperty().bind(mediaContainer.widthProperty());
            mediaView.fitHeightProperty().bind(mediaContainer.heightProperty());

            isFullscreen = true;
        }
    }
}


/*
Esto es una gptada de manual, es la única manera en la que conseguí que se mostrase "Título | Duración | Formato"
pero, cuando se hace de esta forma, se revienta el programa, en principio no deja añadir más de un video, si se logra,
una vez de cada 100 intentos, se bugea guay la aplicacion, no deja intercambiar de video ni tampoco seguir repoduciendo el que lo estaba haciendo.

@FXML
private void onAbrirMenuItemClicked() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Seleccionar archivo multimedia");
    fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivos de video", "*.mp4", "*.mkv", "*.avi"));

    File file = fileChooser.showOpenDialog(null);
    if (file != null) {
        try {
            Media media = new Media(file.toURI().toString());
            MediaPlayer tempPlayer = new MediaPlayer(media);

            // Esperar a que se cargue la duración del archivo
            tempPlayer.setOnReady(() -> {
                String title = file.getName(); // Obtener el título del archivo
                String format = title.substring(title.lastIndexOf('.') + 1).toUpperCase(); // Obtener el formato del archivo
                double durationInSeconds = media.getDuration().toSeconds(); // Obtener la duración en segundos
                String duration = formatTime(durationInSeconds); // Formatear la duración

                // Añadir título, duración y formato al ListView
                String item = String.format("%s | %s | %s", title, duration, format);
                fileListView.getItems().add(item);

                // Si es el primer archivo añadido, reproducirlo automáticamente
                if (fileListView.getItems().size() == 1) {
                    playVideo(file); // Reproducir el archivo
                }

                // Liberar el reproductor temporal
                tempPlayer.dispose();
            });

        } catch (Exception e) {
            System.err.println("Error al obtener la información del archivo: " + e.getMessage());
        }
    }
}

// Método modificado para reproducir el archivo seleccionado
private void playVideo(File file) {
    if (mediaPlayer != null) {
        mediaPlayer.stop();
        mediaPlayer.dispose();
    }

    try {
        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        // Configurar volumen inicial
        mediaPlayer.setVolume(volumeSlider.getValue());

        // Actualizar progreso y tiempo
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!progressSlider.isValueChanging()) {
                progressSlider.setValue(newTime.toSeconds());
            }
            updateCurrentTimeLabel(newTime.toSeconds());
        });

        mediaPlayer.setOnReady(() -> {
            double totalSeconds = mediaPlayer.getTotalDuration().toSeconds();
            progressSlider.setMax(totalSeconds);
            updateTotalTimeLabel(totalSeconds);
        });

        mediaPlayer.play();
    } catch (Exception e) {
        System.err.println("Error al reproducir el archivo: " + e.getMessage());
    }
}

// Método para actualizar el tiempo actual
private void updateCurrentTimeLabel(double currentSeconds) {
    currentTimeLabel.setText(formatTime(currentSeconds));
}

// Método para actualizar el tiempo total
private void updateTotalTimeLabel(double totalSeconds) {
    totalTimeLabel.setText(formatTime(totalSeconds));
}

// Método para formatear el tiempo
private String formatTime(double seconds) {
    int minutes = (int) seconds / 60;
    int secs = (int) seconds % 60;
    return String.format("%d:%02d", minutes, secs);
}
 */