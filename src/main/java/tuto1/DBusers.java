package tuto1;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.sql.*;

public class DBusers {
    public static void  changescene(ActionEvent event , String fxmlfilename,String username ) {
        try {
            FXMLLoader loader = new FXMLLoader(DBusers.class.getResource(fxmlfilename));
            Parent root = loader.load();
            //HomePageController homeController = loader.getController();
            //homeController.setUsername(username);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error loading the scene.");
            alert.getDialogPane().getStylesheets().add(DBusers.class.getResource("/com/example/test/design.css").toExternalForm());
            alert.show();
        }
    }

    public static void signup(ActionEvent event,String lastname ,String firstname ,String email, String username, String password){
        Connection con = null;
        PreparedStatement insertstmt  =null;
        PreparedStatement checkstmt =null;
        ResultSet result=null;
        try{
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_project","root","gumi2004");
            checkstmt = con.prepareStatement("SELECT * FROM users WHERE username= ?");
            checkstmt.setString(1,username);
            result=checkstmt.executeQuery();
            if(result.isBeforeFirst() ){
                System.out.println("Username already exists ");
                Alert alert =new Alert(Alert.AlertType.ERROR);
                alert.setContentText("You cannot use this username ");
                alert.getDialogPane().getStylesheets().add(DBusers.class.getResource("/com/example/test/design.css").toExternalForm());

                alert.show();
            }else{
                insertstmt =con.prepareStatement("INSERT INTO users (username,userpassword,lastname,firstname,email) VALUES (?, ?, ?, ?, ?) ");
                insertstmt.setString(1,username);
                insertstmt.setString(2,password);
                insertstmt.setString(3,lastname);
                insertstmt.setString(4,firstname);
                insertstmt.setString(5,email);
                insertstmt.executeUpdate();
                changescene(event,"/com/example/test/login.fxml",username);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try{
                if (result != null) result.close();
                if (checkstmt != null) checkstmt.close();
                if (insertstmt != null) insertstmt.close();
                if (con != null) con.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
    public static void login(ActionEvent event, String username,String password){
        Connection con = null;
        PreparedStatement stmt  =null;
        ResultSet result=null;
        try{
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_project","root","gumi2004");
            stmt=con.prepareStatement("SELECT userpassword FROM users WHERE username = ?");
            stmt.setString(1,username);
            result=stmt.executeQuery();
            if(!result.isBeforeFirst() ){
                System.out.println("User not found in database");
                Alert alert =new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Username or password is incorrect ");
                alert.getDialogPane().getStylesheets().add(DBusers.class.getResource("/com/example/test/design.css").toExternalForm());
                alert.show();
            }else{
                while(result.next()){
                    String pass=result.getString("userpassword");
                    if(pass.equals(password)){
                        changescene(event, "/com/example/test/home.fxml",username);                    }else{
                        Alert alert =new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Password is incorrect ");
                        alert.getDialogPane().getStylesheets().add(DBusers.class.getResource("/com/example/test/design.css").toExternalForm());
                        alert.show();
                    }
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try{
                if (result != null) result.close();
                if (stmt!= null) stmt.close();
                if (con != null) con.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

    }
}
