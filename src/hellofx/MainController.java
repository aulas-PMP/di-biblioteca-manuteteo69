package hellofx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class MainController {

    @FXML
    private Button playButton;

    @FXML
    private Button pauseButton;

    @FXML
    private Button stopButton;

    @FXML
    private ListView<String> fileListView;

    @FXML
    public void initialize() {
        System.out.println("Interfaz inicializada.");
    }

    @FXML
    private void onPlayButtonClicked() {
        System.out.println("Reproducción iniciada.");
    }

    @FXML
    private void onPauseButtonClicked() {
        System.out.println("Reproducción pausada.");
    }

    @FXML
    private void onStopButtonClicked() {
        System.out.println("Reproducción detenida.");
    }

    @FXML
    private void onAbrirMenuItemClicked() {
        System.out.println("Abrir archivo.");
    }

    @FXML
    private void onSalirMenuItemClicked() {
        System.exit(0);
    }
}
