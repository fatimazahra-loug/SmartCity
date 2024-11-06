module com.example.test {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    exports tuto1;

    opens tuto1 to javafx.fxml;
}