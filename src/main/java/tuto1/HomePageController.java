package tuto1;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class  HomePageController implements Initializable {
    @FXML
    private Button loginbutton_id;
    @FXML
    private Button signupbutton_id;
    @FXML
    private MediaPlayer mediaPlayer;
    @FXML
    private MediaView mediaView;
    @FXML
    private MenuItem Hospitals;
    @FXML
    private MenuItem HistoricalSites;
    @FXML
    private MenuItem Restaurants;
    @FXML
    private MenuItem Grounds;
    @FXML
    private MenuItem Pharmacies;
    @FXML
    private MenuItem Hotels;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextArea messageArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String videoPath = getClass().getResource("/com/example/test/assets/VideoHome.mp4").toExternalForm();
        Media media = new Media(videoPath);
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.setAutoPlay(true);

    }
    public void handlelogin(){
        loginbutton_id.setOnAction(event -> {
            mediaPlayer.stop();
            DBusers.changescene(event, "/com/example/test/login.fxml", "User");
        });
    }
    public void handlesignup(){
        signupbutton_id.setOnAction(event -> {
            mediaPlayer.stop();
            DBusers.changescene(event, "/com/example/test/signup.fxml", "User");
        });
    }
    public void handleMenu(ActionEvent event) {
        try {
            String fxmlFile = null;

            if (event.getSource().equals(Hospitals)) {
                mediaPlayer.stop();
                fxmlFile = "/com/example/test/hospitals.fxml";
            } else if (event.getSource().equals(Restaurants)) {
                mediaPlayer.stop();
                fxmlFile = "/com/example/test/restaurants.fxml";
            } else if (event.getSource().equals(Grounds)) {
                mediaPlayer.stop();
                fxmlFile = "/com/example/test/grounds.fxml";
            } else if (event.getSource().equals(Hotels)) {
                mediaPlayer.stop();
                fxmlFile = "/com/example/test/hotels.fxml";
            } else if (event.getSource().equals(Pharmacies)) {
                mediaPlayer.stop();
                fxmlFile = "/com/example/test/pharmacies.fxml";
            } else if (event.getSource().equals(HistoricalSites)) {
                mediaPlayer.stop();
                fxmlFile = "/com/example/test/historical_sites.fxml";
            }

            if (fxmlFile != null) {
                Parent selectedPage = FXMLLoader.load(getClass().getResource(fxmlFile));

                Stage stage = (Stage) mediaView.getScene().getWindow();
                Scene scene = new Scene(selectedPage);
                stage.setScene(scene);
                stage.setTitle("Page - " + ((MenuItem) event.getSource()).getText());
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
@FXML
public void handleSubmitButton() {
    String name = nameField.getText();
    String email = emailField.getText();
    String message = messageArea.getText();
    if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
        showAlert("Error", "All fields must be filled out!");
        return;
    }

    if (!isValidEmail(email)) {
        showAlert("Error", "Please enter a valid email address!");
        return;
    }

    String subject = "Contact Form Submission from " + name;
    String messageContent = "Name: " + name + "\nEmail: " + email + "\n\nMessage:\n" + message;

    try {
        EmailSender.sendEmail(email, "khantachrajae@gmail.com", subject, messageContent);

        nameField.clear();
        emailField.clear();
        messageArea.clear();
        showAlert("Success", "Your message has been sent!");
    } catch (Exception e) {
        showAlert("Error", "Failed to send the message. Please try again later.");
    }
}

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    public void handleReturnHome(ActionEvent event) {
        try {
            Parent homePage = FXMLLoader.load(getClass().getResource("/com/example/test/homeUser.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(homePage);
            stage.setScene(scene);
            stage.setTitle("Home User Page");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}