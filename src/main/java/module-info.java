module project.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens project.example.View to javafx.fxml;
    exports project.example.View;
}
