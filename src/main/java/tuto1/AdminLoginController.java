package tuto1;

import com.mongodb.client.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import org.bson.Document;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminLoginController {
    @FXML
    private ScrollPane ScrollAtt;
    @FXML
    private ChoiceBox<String> Collection;

    @FXML
    private Button Submit;
    @FXML
    private Button  logoutbutt;

    @FXML
    private ChoiceBox<String> Action;

    @FXML
    private AnchorPane actionPane;

    @FXML
    private Label Label1;

    @FXML
    private Label adminname;


    @FXML
    private Label Label2;

    @FXML
    private Button Submit2;

    @FXML
    private Button return1;

    @FXML
    private VBox inputFieldsContainer;

    private final Map<String, String> collectionNameMap = new HashMap<>();
    private String selectedCollectionOriginalName;
    private MongoClient mongoClient;

    @FXML
    public void initialize() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        populateChoiceBox();
        setupSubmitButton();
        initializeActionChoiceBox();
        return1.setOnAction(event -> resetToFirstPage());

        String username = UserSession.getUsername();
        adminname.setText(username + "!");

    }
    public void handlelogout() {
        logoutbutt.setOnAction(event -> {
            DBusers.changescene(event, "/com/example/test/home.fxml", "User");
        });

    }
    private void resetToFirstPage() {
        actionPane.setVisible(false);
        ScrollAtt.setVisible(false);
        inputFieldsContainer.getChildren().clear();
        Collection.setVisible(true);
        Submit.setVisible(true);
        Label1.setVisible(true);
        Label2.setVisible(true);
    }
    private void populateChoiceBox() {
        try{
            MongoDatabase database = mongoClient.getDatabase("SmartCity");
            MongoIterable<String> collections = database.listCollectionNames();

            for (String collection : collections) {
                String personalizedName = switch (collection) {
                    case "CitiesCollection" -> "Countries | Cities";
                    case "CitiesCollection1" -> "Countries | Cities 1";
                    case "HotelsCollection" -> "Hotels";
                    case "PharmaciesCollection" -> "Pharmacies";
                    case "historicalmonuments" -> "Historical Monuments";
                    case "hospitals" -> "Hospitals";
                    case "Stadiums" -> "Stadiums";
                    case "restaurants" -> "Restaurants";
                    default -> "Unknown: " + collection;
                };
                collectionNameMap.put(personalizedName, collection);
                Collection.getItems().add(personalizedName);
            }
        } catch (Exception e) {
            System.err.println("Error while connecting to MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupSubmitButton() {
        Submit.setOnAction(event -> {
            String selectedCollection = Collection.getValue();
            if (selectedCollection != null) {
                selectedCollectionOriginalName = collectionNameMap.get(selectedCollection);
                System.out.println("Selected Collection (Original): " + selectedCollectionOriginalName);
                actionPane.setVisible(true);
                Collection.setVisible(false);
                Submit.setVisible(false);
                Label1.setVisible(false);
                Label2.setVisible(false);
            } else {
                System.out.println("No collection selected!");
            }
        });
    }

    private void initializeActionChoiceBox() {
        Action.getItems().addAll("Insert", "Update", "Delete", "Select");

        Action.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Selected Action: " + newValue);
            }
        });

        Submit2.setOnAction(event -> {
            String selectedAction = Action.getSelectionModel().getSelectedItem();

            if (selectedAction != null) {
                System.out.println("Action selected: " + selectedAction);

                switch (selectedAction) {
                    case "Insert":
                        handleInsert();
                        break;
                    case "Update":
                        handleUpdate();
                        break;
                    case "Delete":
                        handleDelete();
                        break;
                    case "Select":
                        handleSelect();
                        break;
                    default:
                        System.out.println("No valid action selected.");
                }
            } else {
                System.out.println("Please select an action first.");
            }
        });

        actionPane.setVisible(false);
    }


    private void handleInsert() {
        System.out.println("Insert operation selected.");
        inputFieldsContainer.getChildren().clear();

        try {
            MongoDatabase database = mongoClient.getDatabase("SmartCity");
            MongoCollection<Document> collection = database.getCollection(selectedCollectionOriginalName);

            Document firstDocument = collection.find().first();
            if (firstDocument != null) {
                firstDocument.keySet().forEach(field -> {
                    Label fieldLabel = new Label(field);
                    TextField fieldInput = new TextField();
                    fieldInput.setPromptText("Enter value for " + field);
                    inputFieldsContainer.getChildren().addAll(fieldLabel, fieldInput);
                    actionPane.setVisible(false);
                    ScrollAtt.setVisible(true);
                });

                Button insertButton = new Button("Insert");
                insertButton.setStyle("-fx-background-color: #16423c;-fx-text-fill: white;");

                insertButton.setOnAction(event -> {
                    Document newDocument = new Document();
                    boolean allFieldsFilled = true;

                    for (int i = 0; i < inputFieldsContainer.getChildren().size(); i++) {
                        if (inputFieldsContainer.getChildren().get(i) instanceof Label) {
                            Label label = (Label) inputFieldsContainer.getChildren().get(i);
                            TextField textField = (TextField) inputFieldsContainer.getChildren().get(i + 1);
                            String fieldValue = textField.getText();

                            if (fieldValue.isEmpty()) {
                                allFieldsFilled = false;
                                break;
                            }

                            newDocument.append(label.getText(), fieldValue);
                            i++;
                        }
                    }

                    if (allFieldsFilled) {
                        try {
                            collection.insertOne(newDocument);
                            System.out.println("New document inserted: " + newDocument.toJson());

                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                            successAlert.setTitle("Success");
                            successAlert.setHeaderText("Data Insertion Successful");
                            successAlert.setContentText("The data has been successfully inserted into the collection.");
                            successAlert.showAndWait();

                            resetToFirstPage();

                        } catch (Exception e) {
                            System.err.println("Error during MongoDB insert: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText("Incomplete Data");
                        errorAlert.setContentText("Please fill in all fields before submitting.");
                        errorAlert.showAndWait();
                    }
                });
                inputFieldsContainer.getChildren().add(insertButton);
                Button returnButton = new Button("Return");
                returnButton.setStyle("-fx-background-color: #16423c;-fx-text-fill: white;");
                returnButton.setOnAction(event -> {
                    inputFieldsContainer.getChildren().clear();
                    actionPane.setVisible(true);
                    ScrollAtt.setVisible(false);
                });

                inputFieldsContainer.getChildren().add(returnButton);

            } else {
                Label noFieldsLabel = new Label("No fields available for this collection.");
                inputFieldsContainer.getChildren().add(noFieldsLabel);
            }
        } catch (Exception e) {
            System.err.println("Error during Insert operation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleUpdate() {
        System.out.println("Update operation selected.");
        inputFieldsContainer.getChildren().clear();

        try {
            MongoDatabase database = mongoClient.getDatabase("SmartCity");
            MongoCollection<Document> collection = database.getCollection(selectedCollectionOriginalName);

            ArrayList<Document> documents = collection.find().into(new ArrayList<>());
            if (!documents.isEmpty()) {
                for (Document document : documents) {
                    Label documentLabel = new Label("Document: " + document.toJson()+"\n");
                    documentLabel.setWrapText(true);
                    documentLabel.setMaxWidth(400);
                    documentLabel.setStyle("-fx-border-color: #ccc; -fx-padding: 5px; -fx-background-color: #f9f9f9;");

                    Button updateButton = new Button("Update");
                    updateButton.setStyle("-fx-background-color: #16423c;-fx-text-fill: white;");
                    updateButton.setOnAction(event -> {
                        VBox updateFieldsBox = new VBox(5);
                        updateFieldsBox.setPadding(new Insets(5, 5, 5, 5));
                        document.keySet().forEach(field -> {
                            Label fieldLabel = new Label(field);
                            TextField fieldInput = new TextField();
                            fieldInput.setPromptText("Enter new value for " + field);
                            fieldInput.setText(document.getString(field));
                            updateFieldsBox.getChildren().addAll(fieldLabel, fieldInput);
                        });

                        Button submitUpdateButton = new Button("Submit Update");
                        submitUpdateButton.setStyle("-fx-background-color: #16423c;-fx-text-fill: white;");
                        submitUpdateButton.setOnAction(submitEvent -> {
                            Document updatedDocument = new Document();
                            boolean hasUpdateValues = false;

                            for (int i = 0; i < updateFieldsBox.getChildren().size(); i += 2) {
                                if (updateFieldsBox.getChildren().get(i) instanceof Label) {
                                    Label label = (Label) updateFieldsBox.getChildren().get(i);
                                    TextField textField = (TextField) updateFieldsBox.getChildren().get(i + 1);
                                    String fieldValue = textField.getText();

                                    if (!fieldValue.isEmpty()) {
                                        updatedDocument.append(label.getText(), fieldValue);
                                        hasUpdateValues = true;
                                    }
                                }
                            }

                            if (hasUpdateValues) {
                                try {
                                    String documentId = document.getString("_id");
                                    collection.updateOne(
                                            new Document("_id", documentId),
                                            new Document("$set", updatedDocument)
                                    );
                                    System.out.println("Document updated: " + updatedDocument.toJson());

                                    Label successLabel = new Label("Document updated successfully!");
                                    inputFieldsContainer.getChildren().add(successLabel);
                                    Button returnButton = new Button("Return");
                                    returnButton.setStyle("-fx-background-color: #16423c;-fx-text-fill: white;");
                                    returnButton.setOnAction(eventUp -> {
                                        inputFieldsContainer.getChildren().clear();
                                        actionPane.setVisible(true);
                                        ScrollAtt.setVisible(false);
                                    });

                                    inputFieldsContainer.getChildren().add(returnButton);

                                } catch (Exception e) {
                                    System.err.println("Error during MongoDB update: " + e.getMessage());
                                }
                            } else {
                                System.out.println("No fields to update provided.");
                            }
                        });

                        updateFieldsBox.getChildren().add(submitUpdateButton);
                        inputFieldsContainer.getChildren().clear();
                        inputFieldsContainer.getChildren().addAll(documentLabel, updateFieldsBox);
                        ScrollAtt.setVisible(true);
                    });

                    inputFieldsContainer.getChildren().addAll(documentLabel, updateButton);
                    ScrollAtt.setVisible(true);
                }

            } else {
                inputFieldsContainer.getChildren().add(new Label("No documents found in the collection."));
            }
            Button returnButton = new Button("Return");
            returnButton.setStyle("-fx-background-color: #16423c;-fx-text-fill: white;");
            returnButton.setOnAction(event -> {
                inputFieldsContainer.getChildren().clear();
                actionPane.setVisible(true);
                ScrollAtt.setVisible(false);
            });
            inputFieldsContainer.getChildren().add(returnButton);
        } catch (Exception e) {
            System.err.println("Error during Update operation: " + e.getMessage());
            e.printStackTrace();
        }

        inputFieldsContainer.requestLayout();
        inputFieldsContainer.layout();
    }


    private void handleDelete() {
        System.out.println("Delete operation selected.");
        inputFieldsContainer.getChildren().clear();

        try {
            MongoDatabase database = mongoClient.getDatabase("SmartCity");
            MongoCollection<Document> collection = database.getCollection(selectedCollectionOriginalName);

            ArrayList<Document> documents = collection.find().into(new ArrayList<>());
            if (!documents.isEmpty()) {
                System.out.println("Documents found: " + documents.size());

                for (Document document : documents) {
                    System.out.println("Document: " + document.toJson());
                    Label documentLabel = new Label(document.toJson());
                    documentLabel.setWrapText(true);
                    documentLabel.setMaxWidth(200);
                    documentLabel.setStyle("-fx-border-color: #ccc; -fx-padding: 5px; -fx-background-color: #f9f9f9;");
                    ScrollAtt.setVisible(true);
                    Button deleteButton = new Button("Delete");
                    deleteButton.setStyle("-fx-background-color: #16423c;-fx-text-fill: white;");
                    deleteButton.setOnAction(event -> {
                        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmationAlert.setTitle("Confirm Deletion");
                        confirmationAlert.setHeaderText("Are you sure you want to delete this document?");
                        confirmationAlert.setContentText(document.toJson());
                        confirmationAlert.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                try {
                                    collection.deleteOne(document);
                                    System.out.println("Document deleted: " + document.toJson());
                                    inputFieldsContainer.getChildren().remove(documentLabel);
                                } catch (Exception e) {
                                    System.err.println("Error during MongoDB delete: " + e.getMessage());
                                }
                            }
                        });
                    });

                    inputFieldsContainer.getChildren().addAll(documentLabel, deleteButton);
                }
            } else {
                Label noDocumentsLabel = new Label("No documents found in the collection.");
                inputFieldsContainer.getChildren().add(noDocumentsLabel);
            }

            Button returnButton = new Button("Return");
            returnButton.setStyle("-fx-background-color: #16423c;-fx-text-fill: white;");
            returnButton.setOnAction(event -> {
                inputFieldsContainer.getChildren().clear();
                actionPane.setVisible(true);
                ScrollAtt.setVisible(false);
            });
            inputFieldsContainer.getChildren().add(returnButton);

        } catch (Exception e) {
            System.err.println("Error during Delete operation: " + e.getMessage());
        }
    }

    private void handleSelect() {
        System.out.println("Select operation selected.");
        inputFieldsContainer.getChildren().clear();

        try {
            MongoDatabase database = mongoClient.getDatabase("SmartCity");
            MongoCollection<Document> collection = database.getCollection(selectedCollectionOriginalName);

            FindIterable<Document> documents = collection.find();

            if (documents.iterator().hasNext()) {
                for (Document document : documents) {
                    Label documentLabel = new Label(document.toJson());
                    documentLabel.setWrapText(true);
                    documentLabel.setPrefWidth(200);
                    documentLabel.setStyle("-fx-border-color: #ccc; -fx-padding: 5px; -fx-background-color: #f9f9f9;");
                    inputFieldsContainer.getChildren().add(documentLabel);
                }
                actionPane.setVisible(false);
                ScrollAtt.setVisible(true);
                Button returnButton = new Button("Return");
                returnButton.setStyle("-fx-background-color: #16423c;-fx-text-fill: white;");
                returnButton.setOnAction(event -> {
                    inputFieldsContainer.getChildren().clear();
                    actionPane.setVisible(true);
                    ScrollAtt.setVisible(false);
                });
                inputFieldsContainer.getChildren().add(returnButton);
            } else {
                Label noDataLabel = new Label("No data found in the collection.");
                noDataLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
                inputFieldsContainer.getChildren().add(noDataLabel);
            }

        } catch (Exception e) {
            System.err.println("Error during Select operation: " + e.getMessage());
            e.printStackTrace();

            // Show an error message in the UI
            Label errorLabel = new Label("An error occurred while fetching data.");
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
            inputFieldsContainer.getChildren().add(errorLabel);
        }
    }


}