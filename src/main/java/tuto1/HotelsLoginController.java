package tuto1;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.bson.Document;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import org.bson.conversions.Bson;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class HotelsLoginController implements Initializable {
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
    @FXML
    private TextField searched;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private Object IconView;
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
        }
    }

    private void updateCities(String selectedCountry) {
        city.getItems().clear();
        MongoCollection<Document> collection = database.getCollection("CitiesCollection");
        FindIterable<Document> cities = collection.find(new Document("Country", selectedCountry));
        for (Document doc : cities) {
            city.getItems().add(doc.getString("City"));
        }
    }

    @FXML
    private void handleSubmit() {
        String selectedCountry = country.getValue();
        String selectedCity = city.getValue();

        MongoCollection<Document> collection = database.getCollection("HotelsCollection");
        FindIterable<Document> hotels = collection.find(new Document("Country", selectedCountry).append("City", selectedCity));

        resultsContainer.getChildren().clear();

        if (hotels.iterator().hasNext()) {
            for (Document hotel : hotels) {
                Label hotelLabel = new Label("Hotel: " + hotel.getString("HotelName"));
                Label addressLabel = new Label("Address: " + hotel.getString("Address"));
                Label phoneLabel = new Label("Contact: " + hotel.getString("PhoneNumber") + " | " + hotel.getString("FaxNumber"));
                Hyperlink siteLink = createHyperlink("Hotel Website", hotel.getString("HotelWebsiteUrl"), "src/main/resources/com/example/test/assets/WebSIte.png");
                Hyperlink mapLink = createHyperlink("View on Map", hotel.getString("Map"), "src/main/resources/com/example/test/assets/map.png");
                styleLabel(hotelLabel, 14, true, "#195339");
                styleLabel(addressLabel, 12, false, "black");
                styleLabel(phoneLabel, 12, false, "black");


                hotelLabel.setStyle(
                        "-fx-font-size: 14; " +
                                " -fx-font-weight: bold"+
                                "-fx-padding: 10; " +
                                "-fx-text-fill: black; " +
                                "-fx-background-color: #d3e48d; " +
                                "-fx-border-radius: 5; " +
                                "-fx-border-color: #ccc; " +
                                "-fx-border-width: 1;"
                );

                resultsContainer.getChildren().addAll(hotelLabel, addressLabel, phoneLabel, siteLink, mapLink);
                String fullDescription = hotel.getString("Description");
                String truncatedDescription = fullDescription.length() > 100
                        ? fullDescription.substring(0, 100) + "..."
                        : fullDescription;
                Label descriptionLabel = new Label("Description: " + truncatedDescription);
                styleLabel(descriptionLabel, 12, false, "black");

                if (fullDescription.length() > 100) {
                    Hyperlink readMoreLink = new Hyperlink("Read More");
                    readMoreLink.setOnAction(e -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Full Description");
                        alert.setHeaderText("Description for " + hotel.getString("HotelName"));
                        alert.setContentText(fullDescription);
                        alert.showAndWait();
                    });
                    HBox descriptionBox = new HBox(5, descriptionLabel, readMoreLink);
                    resultsContainer.getChildren().add(descriptionBox);
                } else {
                    resultsContainer.getChildren().add(descriptionLabel);
                }

            }
        } else {
            Label noResultsLabel = new Label("No hotels found.");
            styleLabel(noResultsLabel, 14, true, "red");
            resultsContainer.getChildren().add(noResultsLabel);
        }

        test2.setVisible(true);
        test.setVisible(false);
        returnButton.setVisible(true);
    }
    private Hyperlink createHyperlink(String text, String url, String iconPath) {
        Hyperlink hyperlink = new Hyperlink(text);
        try {
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)));
            ImageView iconView = new ImageView(icon);
            iconView.setFitWidth(12);
            iconView.setFitHeight(12);
            hyperlink.setGraphic((Node) iconView);
        } catch (Exception e) {
            System.err.println("Error loading hyperlink icon: " + e.getMessage());
        }
        if (url != null && !url.isEmpty()) {
            hyperlink.setOnAction(e -> openLink(url));
        } else {
            hyperlink.setDisable(true);
        }
        hyperlink.setStyle("-fx-font-size: 12; -fx-text-fill: blue;");
        return hyperlink;
    }



    private void styleLabel(Label label, int fontSize, boolean isBold, String color) {
        String style = "-fx-font-size: " + fontSize + "px; -fx-text-fill: " + color + ";";
        if (isBold) {
            style += "-fx-font-weight: bold;";
        }
        label.setStyle(style);
    }

    @FXML
    private void handleReturn() {
        test.setVisible(true);
        test2.setVisible(false);
        returnButton.setVisible(false);
    }

    private void openLink(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            e.printStackTrace();
            resultLabel.setText("Unable to open link.");
        }
    }

    public void handleSearch() {
        String searchText = searched.getText().trim();

        if (searchText.isEmpty()) {
            resultLabel.setText("Please enter a valid search query.");
            return;
        }
        resultsContainer.getChildren().clear();
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("SmartCity");
            MongoCollection<Document> collection = database.getCollection("HotelsCollection");
            Bson filter = Filters.or(
                    Filters.regex("countyName", searchText, "i"),
                    Filters.regex("citName", searchText, "i"),
                    Filters.regex("HotelName", searchText, "i"),
                    Filters.regex("HotelRating", searchText, "i"),
                    Filters.regex("Address", searchText, "i"),
                    Filters.regex("Description", searchText, "i"),
                    Filters.regex("FaxNumber", searchText, "i"),
                    Filters.regex("HotelFacilities", searchText, "i"),
                    Filters.regex("PhoneNumber", searchText, "i"),
                    Filters.regex("HotelWebsiteUrl", searchText, "i"),
                    Filters.regex("Map", searchText, "i")
            );

            FindIterable<Document> results = collection.find(filter);

            boolean hasResults = false;
            for (Document doc : results) {
                hasResults = true;
                String country = doc.getString("countyName");
                String city = doc.getString("citName");
                String hotelName = doc.getString("HotelName") ;
                String rating = doc.getString("HotelRating") ;
                String address = doc.getString("Address") ;
                String description = doc.getString("Description") ;
                String phoneNumber = doc.getString("PhoneNumber");
                String websiteUrl = doc.getString("HotelWebsiteUrl") ;

                Label resultLabel = new Label(
                        "Country: "+country+"\n"+
                                "City: "+city+"\n"+
                                "Hotel Name: " + hotelName + "\n" +
                                "Rating: " + rating + "\n" +
                                "Address: " + address + "\n" +
                                "Description: " + description + "\n" +
                                "Phone Number: " + phoneNumber + "\n" +
                                "Website: " + websiteUrl
                );

                resultLabel.setWrapText(true);
                resultLabel.setStyle(
                        "-fx-font-size: 14; " +
                                "-fx-text-fill: black; " +
                                "-fx-padding: 10; " +
                                "-fx-background-color: #d3e48d; " +
                                "-fx-border-radius: 5; " +
                                "-fx-border-color: #ccc; " +
                                "-fx-border-width: 1;"
                );
                resultsContainer.getChildren().add(resultLabel);
            }
            if (!hasResults) {
                resultsContainer.getChildren().add(new Label("No results found."));
            }
        } catch (Exception e) {
            resultLabel.setText("Error occurred while searching: " + e.getMessage());
            e.printStackTrace();
        }
        test2.setVisible(true);
        returnButton.setVisible(true);
    }
    @FXML
    public void handleReturnHome(ActionEvent event) {
        try {
            Parent homePage = FXMLLoader.load(getClass().getResource("/com/example/test/homeUser.fxml"));
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
