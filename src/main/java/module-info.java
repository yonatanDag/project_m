module project.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens project.example.View to javafx.fxml;
    // Opens the project.example.Model package to javafx.base which is needed for property access.
    opens project.example.Model to javafx.base, javafx.fxml;
    
    exports project.example.View;
}
