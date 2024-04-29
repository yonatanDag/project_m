module project.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // If you're using any classes in the Controller package with JavaFX properties or collections, open it to javafx.base.
    opens project.example.Controller to javafx.fxml, javafx.base;
    opens project.example.Model to javafx.base, javafx.fxml;
    
    // Export the View package if it still contains any JavaFX components such as your main application class
    exports project.example.View;
    // Export the Controller package if it's to be accessible by other modules, not necessary if it's only used within this module
    exports project.example.Controller;
}
