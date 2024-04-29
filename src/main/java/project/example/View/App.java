package project.example.View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import project.example.Controller.ClientTaskController;
import project.example.Controller.TechTaskController;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/project/example/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    // a method to pass the technician ID to TechTasks view.
    public static void setRootTechTasks(String fxml, int technicianId) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/project/example/" + fxml + ".fxml"));
        Parent root = loader.load();

        // Get the controller and call the method to pass the technician ID
        TechTaskController controller = loader.getController();
        controller.runAlgorithmAndDisplayTasks(technicianId);

        scene.setRoot(root);
    }
    
    // Method to pass the client ID to ClientTasks view.
    public static void setRootClientTasks(String fxml, int clientId) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/project/example/" + fxml + ".fxml"));
        Parent root = loader.load();

        // Get the controller and call the method to pass the client ID
        ClientTaskController controller = loader.getController();
        controller.setClientId(clientId);

        scene.setRoot(root);
    }

    public static void main(String[] args) {
        launch();
    }

}