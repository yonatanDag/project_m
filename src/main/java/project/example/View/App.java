package project.example.View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
        
        // Get the controller and set the technician ID
        TechTaskController controller = loader.getController();
        controller.setTechnicianId(technicianId); // Assume you have a setter for technicianId in TechTaskController
    
        scene.setRoot(root);
    }
    

    public static void main(String[] args) {
        launch();
    }

}