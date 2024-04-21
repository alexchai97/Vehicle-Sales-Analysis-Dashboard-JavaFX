module com.dashboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.dashboard to javafx.fxml;
    exports com.dashboard;
}
