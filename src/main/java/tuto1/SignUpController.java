package tuto1;
import javafx.scene.control.Alert;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {

    @FXML
    private Button signup_id;
    @FXML
    private TextField lastname_id;
    @FXML
    private TextField firstname_id;
    @FXML
    private TextField email_id ;
    @FXML
    private TextField username_id;
    @FXML
    private PasswordField password_id;
    @FXML
    private PasswordField confirm_id;
    @FXML
    private Hyperlink login_id;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        signup_id.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!username_id.getText().trim().isEmpty() && !password_id.getText().trim().isEmpty() && !firstname_id.getText().trim().isEmpty() && !lastname_id.getText().trim().isEmpty() && !email_id.getText().trim().isEmpty() && !confirm_id.getText().trim().isEmpty()) {
                    if(password_id== confirm_id) {
                        DBusers.signup(event, lastname_id.getText(), firstname_id.getText(), email_id.getText(), username_id.getText(), password_id.getText());
                    }else{
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Confirm Password is incorrect");
                        alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/example/test/design.css").toExternalForm());
                        alert.show();
                    }
                } else {
                    System.out.println("Please fill in all information ");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Fill in all information to SignUp");
                    alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/example/test/design.css").toExternalForm());
                    alert.show();
                }
            }

        });
        login_id.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                DBusers.changescene(event,"/com/example/test/login.fxml",username_id.getText());            }
        });
    }

}
