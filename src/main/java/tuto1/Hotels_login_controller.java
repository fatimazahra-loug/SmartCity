package tuto1;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;

import java.net.URL;
import java.util.ResourceBundle;

public class Hotels_login_controller implements Initializable {
    @FXML
    private ChoiceBox<String> country; // Remove 'final' and let JavaFX inject this field

    private final String[] countries = {"Morocco", "Portugal", "Spain"};

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Ensure the ChoiceBox is not null before adding items
        if (country != null) {
            country.getItems().addAll(countries);
        }
    }
}
