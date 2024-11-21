package tuto1;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Button login_id;
    @FXML
    private TextField username_id;
    @FXML
    private PasswordField password_id;
    @FXML
    private Hyperlink signup_id;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        login_id.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBusers.login(event,username_id.getText(),password_id.getText());
            }
        });
        signup_id.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBusers.changescene(event,"/com/example/test/signup.fxml",username_id.getText());
            }
        });
    }

}
