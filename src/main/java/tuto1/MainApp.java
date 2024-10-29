package tuto1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Smart City Application");

        // Start with the login view
        showLoginView();
    }

    public void showLoginView() throws Exception {
        // Use a relative path to load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/test/login.fxml"));
        Parent root = loader.load();

        // Assuming you have a controller with a setMainApp method
        LoginController controller = loader.getController();
        controller.setMainApp(this); // Pass the reference of MainApp to the controller

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
