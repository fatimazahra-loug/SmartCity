package tuto1;

import javafx.event.ActionEvent;

import java.sql.*;

public class DBusers {
    public static void signup(ActionEvent event,String lastname,String firstname,String emailadress, String username, String password, String confirm){
        Connection conn =null;
        PreparedStatement psInsert=null;
        PreparedStatement psCheckUserExists =null;
        ResultSet result =null;
        try {
            conn= DriverManager.getConnection("jdbc:mysql://localhost:3306/java_project ","root","gumi2004");
            psCheckUserExists =conn.prepareStatement("SELECT * FROM users WHERE username= ?");
            psCheckUserExists.setString(1,username);
            result=psCheckUserExists.executeQuery();
            if (result.isBeforeFirst()){}
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
