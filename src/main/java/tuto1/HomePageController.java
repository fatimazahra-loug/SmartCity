package tuto1;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class  HomePageController implements Initializable {
    @FXML
    private Button loginbutton_id;
    @FXML
    private Button signupbutton_id;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginbutton_id.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBusers.changescene(event, "/com/example/test/login.fxml", "User ");
            }
        });
        signupbutton_id.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DBusers.changescene(actionEvent, "/com/example/test/signup.fxml", "User");
            }
        });
    }
}
