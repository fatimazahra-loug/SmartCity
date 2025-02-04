package tuto1;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.bson.Document;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.bson.conversions.Bson;

import java.net.URL;
import java.util.ResourceBundle;

public class PharmacyLoginController implements Initializable {
    @FXML
    private ChoiceBox<String> country;
    @FXML
    private ChoiceBox<String> city;
    @FXML
    private Label resultLabel;
    @FXML
    private VBox resultsContainer;
    @FXML
    private Button returnButton;
    @FXML
    private AnchorPane test;
    @FXML
    private AnchorPane test2;
    private MongoClient mongoClient;
    private MongoDatabase database;
    @FXML
    private TextField searched;
    @FXML
    private Label usernameid;
    @FXML
    private Button logoutbutt;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectToMongoDB();
        loadCountries();
        country.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateCities(newValue));
        String username = UserSession.getUsername();
        usernameid.setText(username + "!");


    }
    public void handlelogout() {
        logoutbutt.setOnAction(event -> {
            DBusers.changescene(event, "/com/example/test/home.fxml", "User");
        });

    }
    private void connectToMongoDB() {
        try {
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("SmartCity");
            System.out.println("Connected to MongoDB");
        } catch (Exception e) {
            e.printStackTrace();
            resultLabel.setText("Error connecting to database.");
        }
    }

    private void loadCountries() {
        MongoCollection<Document> collection = database.getCollection("CitiesCollection");
        DistinctIterable<String> countries = collection.distinct("Country", String.class);
        for (String countryName : countries) {
            country.getItems().add(countryName);
            System.out.println("Loaded country: " + countryName);

        }
    }

    private void updateCities(String selectedCountry) {
        city.getItems().clear();
        MongoCollection<Document> collection = database.getCollection("CitiesCollection");
        FindIterable<Document> cities = collection.find(new Document("Country", selectedCountry));
        for (Document doc : cities) {
            city.getItems().add(doc.getString("City"));
            System.out.println("Loaded city: " + doc.getString("City"));

        }
    }
    @FXML
    private void handleSubmit() {
        String selectedCity = city.getValue();
        String selectedCountry = country.getValue();

//        selectedCity = selectedCity.substring(0, 1).toUpperCase() + selectedCity.substring(1).toLowerCase(); // Capitalize first letter
//        selectedCountry = selectedCountry.substring(0, 1).toUpperCase() + selectedCountry.substring(1).toLowerCase(); // Capitalize first letter

        if (selectedCity == null || selectedCountry == null || selectedCity.isEmpty() || selectedCountry.isEmpty()) {
            resultLabel.setText("Please select both a country and a city.");
            return;
        }

        MongoCollection<Document> collection = database.getCollection("PharmaciesCollection");
        FindIterable<Document> Pharmacies = collection.find(
                new Document("Country", selectedCountry).append("City", selectedCity)
        );

        resultsContainer.getChildren().clear();
        if (Pharmacies.iterator().hasNext()) {
            for (Document Pharmacy : Pharmacies) {
                Label nameLabel = new Label("Pharmacy: " + Pharmacy.getString("title"));
                Label AddressLabel = new Label("Address: " + Pharmacy.getString("address"));
                Label PhoneLabel = new Label("Phone: " + Pharmacy.getString("phoneNumber"));


                // Hyperlink for map URL
                Double latitude = Pharmacy.getDouble("latitude");
                Double longitude = Pharmacy.getDouble("longitude");
                Hyperlink mapLink = createMapLink("View on Map", latitude,longitude);


                // Style labels
                styleLabel(nameLabel, 14, true, "black");
                styleLabel(AddressLabel, 12, false, "black");
                styleLabel(PhoneLabel,12,true,"black");

                // Add components to the results container
                nameLabel.setStyle(
                        "-fx-font-size: 14; " +
                                " -fx-font-weight: bold"+
                                "-fx-padding: 10; " +
                                "-fx-text-fill: black; " +
                                "-fx-background-color: #d3e48d; " +
                                "-fx-border-radius: 5; " +
                                "-fx-border-color: #ccc; " +
                                "-fx-border-width: 1;"
                );
                resultsContainer.getChildren().addAll(nameLabel,AddressLabel,PhoneLabel,mapLink);
            }
        } else {
            Label noResultsLabel = new Label("No Pharmacies found for the selected country and city.");
            styleLabel(noResultsLabel, 14, true, "#195339");
            resultsContainer.getChildren().add(noResultsLabel);
        }

        test2.setVisible(true);
        test.setVisible(false);
        returnButton.setVisible(true);
    }

    public void handleSearch() {
        String searchText = searched.getText().trim();

        if (searchText.isEmpty()) {
            resultLabel.setText("Please enter a valid search query.");
            return;
        }

        // Clear previous results
        resultsContainer.getChildren().clear();

        // MongoDB search logic
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("SmartCity");
            MongoCollection<Document> collection = database.getCollection("PharmaciesCollection");

            // Perform a case-insensitive search
            Bson filter = Filters.or(
                    Filters.regex("Country", searchText, "i"),
                    Filters.regex("City", searchText, "i"),
                    Filters.regex("title", searchText, "i"),
                    Filters.regex("rating", searchText, "i"),
                    Filters.regex("address", searchText, "i"),
                    Filters.regex("phoneNumber", searchText, "i"),
                    Filters.regex("latitude", searchText, "i"),
                    Filters.regex("longitude", searchText, "i")

            );

            FindIterable<Document> results = collection.find(filter);

            boolean hasResults = false;
            for (Document doc : results) {
                hasResults = true;

                // Extract and format hotel details
                String country = doc.getString("Country");
                String city = doc.getString("City");
                String pharmacieName = doc.getString("title") ;
                Double rating = doc.getDouble("rating") ;
                String address = doc.getString("address") ;
                String phoneNumber = doc.getString("phoneNumber");
                Double lat = doc.getDouble("latitude") ;
                Double lon = doc.getDouble("longitude") ;

                Label resultLabel = new Label(
                        "Country: "+country+"\n"+
                                "City: "+city+"\n"+
                                "Pharmacie Name: " + pharmacieName + "\n" +
                                "Rating: " + rating + "\n" +
                                "Address: " + address + "\n" +
                                "Phone Number: " + phoneNumber + "\n" +
                                "Latitude: " +lat+"\n" +
                                "Longitude: " +lon+"\n"
                );

                resultLabel.setWrapText(true);
                resultLabel.setStyle(
                        "-fx-font-size: 14; " +
                                "-fx-text-fill: black; " + // Text color
                                "-fx-padding: 10; " +
                                "-fx-background-color: #d3e48d; " +
                                "-fx-border-radius: 5; " +
                                "-fx-border-color: #ccc; " +
                                "-fx-border-width: 1;"
                );
                resultsContainer.getChildren().add(resultLabel);
            }

            // Display a message if no results are found
            if (!hasResults) {
                resultsContainer.getChildren().add(new Label("No results found."));
            }
        } catch (Exception e) {
            resultLabel.setText("Error occurred while searching: " + e.getMessage());
            e.printStackTrace();
        }

        // Make the results container visible
        test2.setVisible(true);
        returnButton.setVisible(true);
    }
    private Hyperlink createMapLink(String str, Double latitude, double longitude) {
        Hyperlink hyperlink = new Hyperlink(str);
        if (latitude != null) {  // Check if latitude is not null (no need to check longitude since it's a primitive)
            // Generate the Google Maps URL using latitude and longitude
            String mapUrl = "https://www.google.com/maps?q=" + latitude + "," + longitude;

            hyperlink.setOnAction(e -> openLink(mapUrl));  // Open the map in the browser
        } else {
            hyperlink.setDisable(true);  // Disable the link if latitude is not valid
        }

        hyperlink.setStyle("-fx-font-size: 12; -fx-text-fill: blue;");
        return hyperlink;
    }


    private void styleLabel(Label label, int fontSize, boolean bold, String color) {
        label.setFont(new Font(fontSize));
        if (bold) {
            label.setStyle("-fx-font-weight: bold;");
        }
        label.setTextFill(Paint.valueOf(color));
    }

    @FXML
    private void handleReturn() {
        test.setVisible(true);
        test2.setVisible(false);
        returnButton.setVisible(false);
    }

    private void openLink(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url)); // Opens the URL in the default browser
        } catch (Exception e) {
            e.printStackTrace();
            resultLabel.setText("Unable to open link.");
        }
    }
    @FXML
    public void handleReturnHome(ActionEvent event) {
        try {
            // Load the home page FXML
            Parent homePage = FXMLLoader.load(getClass().getResource("/com/example/test/homeUser.fxml"));

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(homePage);
            stage.setScene(scene);
            stage.setTitle("Home Page"); // Optional: Set the title
            stage.show();
        } catch (Exception e) {
            e.printStackTrace(); // Log any exceptions
        }
    }

    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}