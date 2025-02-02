package hellofx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/hellofx/hello.fxml"));

        primaryStage.setTitle("Biblioteca Multimedia");
        primaryStage.setScene(new Scene(root, 1024, 768));

        // Configuración para maximizar la ventana al iniciar
        primaryStage.setMaximized(true);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
