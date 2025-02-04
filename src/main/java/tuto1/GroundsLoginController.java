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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.bson.Document;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import java.net.URL;
import java.util.ResourceBundle;
import org.bson.conversions.Bson;

public class GroundsLoginController implements Initializable {
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
    private Button viewLessButton;
    @FXML
    private TextField searched;
    private Button viewMoreButton;
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
        MongoCollection<Document> collection = database.getCollection("CitiesCollection1");
        DistinctIterable<String> countries = collection.distinct("Country", String.class);
        for (String countryName : countries) {
            country.getItems().add(countryName);
        }
    }

    private void updateCities(String selectedCountry) {
        city.getItems().clear();
        MongoCollection<Document> collection = database.getCollection("CitiesCollection1");
        FindIterable<Document> cities = collection.find(new Document("Country", selectedCountry));
        for (Document doc : cities) {
            city.getItems().add(doc.getString("City"));
        }
    }
    @FXML
    private void handleSubmit() {
        String selectedCity = city.getValue().trim();
        String selectedCountry = country.getValue().trim();

        selectedCity = selectedCity.substring(0, 1).toUpperCase() + selectedCity.substring(1).toLowerCase();
        selectedCountry = selectedCountry.substring(0, 1).toUpperCase() + selectedCountry.substring(1).toLowerCase();

        if (selectedCity == null || selectedCountry == null) {
            resultLabel.setText("Please select both a country and a city.");
            return;
        }

        MongoCollection<Document> collection = database.getCollection("Stadiums");
        FindIterable<Document> stadiums = collection.find(
                new Document("Country", selectedCountry).append("City", selectedCity)
        );

        resultsContainer.getChildren().clear();
        if (stadiums.iterator().hasNext()) {
            for (Document stadium : stadiums) {
                Label nameLabel = new Label("Stadium: " + stadium.getString("Name"));
                Label capacityLabel = new Label("Capacity: " + stadium.getInteger("Capacity"));
                Text descriptionLabel = new Text();
                String description = stadium.getString("Description");
                String truncatedDescription = truncateDescription(description);
                descriptionLabel.setText("Description: " + truncatedDescription);
                viewMoreButton = new Button("View More");
                viewMoreButton.setOnAction(e -> {
                    descriptionLabel.setText("Description: " + formatDescriptionText(description)); // Show full description
                    viewMoreButton.setVisible(false); // Hide "View More"
                    viewLessButton.setVisible(true); // Show "View Less"
                });

                // "View Less" button
                viewLessButton = new Button("View Less");
                viewLessButton.setVisible(false); // Initially hidden
                viewLessButton.setOnAction(e -> {
                    descriptionLabel.setText("Description: " + truncatedDescription); // Show truncated description
                    viewMoreButton.setVisible(true); // Show "View More"
                    viewLessButton.setVisible(false); // Hide "View Less"
                });

                // Hyperlink for map URL
                String mapUrl = stadium.getString("Map");
                Hyperlink mapLink = null;
                if (mapUrl != null && !mapUrl.isEmpty()) {
                    mapLink = createHyperlink("View on Map", mapUrl);
                }

                // Retrieve and display the Image
                String imageUrl = stadium.getString("Image");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    try {
                        Image image = new Image(imageUrl);
                        ImageView imageView = new ImageView(image);
                        imageView.setFitWidth(200);
                        imageView.setPreserveRatio(true);
                        imageView.setSmooth(true);

                        resultsContainer.getChildren().add(imageView);
                    } catch (Exception e) {
                        e.printStackTrace();
                        resultLabel.setText("Error loading image.");
                    }
                }

                // Style labels
                styleLabel(nameLabel, 14, true, "#195339");
                styleLabel(capacityLabel, 12, false, "#195339");

                // Add components to the results container
                resultsContainer.getChildren().addAll(nameLabel, capacityLabel, descriptionLabel);
                resultsContainer.getChildren().add(viewMoreButton);
                resultsContainer.getChildren().add(viewLessButton);
                if (mapLink != null) {
                    resultsContainer.getChildren().add(mapLink);
                }
            }
        } else {
            Label noResultsLabel = new Label("No stadiums found for the selected country and city.");
            styleLabel(noResultsLabel, 14, true, "#195339");
            resultsContainer.getChildren().add(noResultsLabel);
        }

        test2.setVisible(true);
        test.setVisible(false);
        returnButton.setVisible(true);
    }

    private String truncateDescription(String description) {
        int maxWords = 6;
        String[] words = description.split("\\s+");
        StringBuilder truncatedDescription = new StringBuilder();
        int wordCount = 0;
        for (int i = 0; i < words.length; i++) {
            truncatedDescription.append(words[i]).append(" ");
            wordCount++;

            // If the word count exceeds the max, stop adding words
            if (wordCount == maxWords) {
                truncatedDescription.append("..."); // Add ellipsis at the end
                break;
            }
        }
        return truncatedDescription.toString();
    }

    public String formatDescriptionText(String description) {
        String[] words = description.split("\\s+");
        StringBuilder formattedDescription = new StringBuilder();
        int wordCount = 0;
        StringBuilder line = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            line.append(words[i]).append(" ");
            wordCount++;

            // If the word count exceeds 6, break the line and start a new one
            if (wordCount == 6) {
                formattedDescription.append(line.toString()).append("\n");
                line.setLength(0); // Reset line
                wordCount = 0;
            }
        }

        // Append the remaining words in the last line
        if (line.length() > 0) {
            formattedDescription.append(line.toString());
        }

        return formattedDescription.toString();
    }

    private Hyperlink createHyperlink(String text, String url) {
        Hyperlink hyperlink = new Hyperlink(text);
        hyperlink.setOnAction(e -> {
            try {
                openLink(url); // Call the openLink method to handle the action
            } catch (Exception ex) {
                ex.printStackTrace();
                resultLabel.setText("Unable to open link.");
            }
        });
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
            MongoCollection<Document> collection = database.getCollection("Stadiums");

            // Perform a case-insensitive search
            Bson filter = Filters.or(
                    Filters.regex("City", searchText, "i"),
                    Filters.regex("Country", searchText, "i"),
                    Filters.regex("Name", searchText, "i"),
                    Filters.regex("Description", searchText, "i"),
                    Filters.regex("Capacity", searchText, "i"),
                    Filters.regex("Map", searchText, "i")


            );

            FindIterable<Document> results = collection.find(filter);

            boolean hasResults = false;
            for (Document doc : results) {
                hasResults = true;
                // Extract and format data
                if (doc != null) {
                    String stadiumsname = doc.getString("Name") ;
                    int capacity = doc.getInteger("Capacity", 0);
                    String description = doc.getString("Description") ;
                    String city = doc.getString("City") ;
                    String country = doc.getString("Country") ;
                    String map = doc.getString("Map") ;
                    Label resultLabel = new Label(
                            "Name: " + stadiumsname + "\n" +
                                    "Capacity: " + capacity + "\n" +
                                    "Description: " + description + "\n" +
                                    "City: " + city + "\n" +
                                    "Country: " + country + "\n" +
                                    "Map: " + map
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
            stage.setTitle("Home User Page"); // Optional: Set the title
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