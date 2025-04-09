package photos.controller;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.collections.*;
import java.io.*;
//import javafx.scene.control.ListView; this import probably won't be used in this class but I'm paranoid
import java.util.ArrayList;
/**
 * Controller for the logout confirmation interface.
 * Allows user to return to the login screen or quit the
 * application entirely.
 */
public class LogoutController {
    @FXML Button logOut;
	@FXML Button quitButton;
	/**
	 * Logs the user out by navigating back to the login screen.
	 * @param e is the triggering event
	 */
    public void logOut(ActionEvent e){
        try{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Login.fxml"));
			Parent root = loader.load();
			Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
			stage.getScene().setRoot(root);

			stage.setTitle("Login Page");
			stage.show();
		}
		catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Failed to Load Page");
			alert.setContentText("Something went wrong while loading the page.\nDetails: " + ex.getMessage());
			alert.showAndWait(); 
        }
    }
	/**
	 * Saves the users' data and subsequently quits the application.
	 * @param e is the triggering event.
	 */
	public void quitApplication(ActionEvent e){
        photos.model.Users.saveUsersList();
        photos.model.Users.saveUserAlbums();
        photos.model.Users.saveUserTagTypes();
        Platform.exit();
        System.exit(0);
    }
}
