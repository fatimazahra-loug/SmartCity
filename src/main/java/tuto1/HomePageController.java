package tuto1;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class  HomePageController implements Initializable {
    @FXML
    private Button loginbutton_id;
    @FXML
    private Button signupbutton_id;
    @FXML
    private Label userlabel;
    @FXML
    private Label  mondiallabel;
    @FXML
    private Label  tolabel;
    @FXML
    private Label  quoteid;
    @FXML
    private Button logoutbutt ;


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
        logoutbutt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DBusers.changescene(actionEvent, "/com/example/test/home.fxml", "User");
            }
        });

    }
    public void setinfos(String username) {
            userlabel.setText( username + "!");
            tolabel.setVisible(false);
            mondiallabel.setVisible(false);
            quoteid.setText("Discover the joy of travel with Mundial City!");
            quoteid.setStyle("-fx-font-size: 20px; -fx-font-family: 'Georgia'; ");
            quoteid.setLayoutY(110);
            loginbutton_id.setVisible(false);
            signupbutton_id.setVisible(false);
            logoutbutt.setVisible(true);
    }
}
