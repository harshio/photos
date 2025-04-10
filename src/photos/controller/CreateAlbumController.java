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
 * Controller for the album creation interface.
 * Allows users to input a name and create a new album.
 * @author Harshi Oleti
 */
public class CreateAlbumController {
    /**
     * A text field that user types the name of
     * the new album that user wants to create.
     */
    @FXML TextField albumName;
    /**
     * A button that creates an Album object and
     * stores it in userAlbums
     */
    @FXML Button createAlbum;
    /**
     * Button that saves the user data and quits the application.
     */
    @FXML Button quitButton;
    /**
     * Creates a new album with the user-specified name if valid and not a duplicate.
     * Transitions to home page upon success.
     * @param e is the triggering event from the createAlbum button.
     */
    public void createAlbum(ActionEvent e){
        try{
            String name = albumName.getText().trim();
            if (!name.isEmpty()) {
                photos.model.Users.createAlbum(photos.model.Users.currentUser, name);
                photos.model.Users.saveUserAlbums();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Bulk.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

            //For stage.setTitle(), we'll make sure to properly if-condition this one and only this one later.
            stage.setTitle("Home Page");
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
     * Saves user data and quits the application.
     * @param e is the triggering event from the quitButton button.
     */
    public void quitApplication(ActionEvent e){
        photos.model.Users.saveUsersList();
        photos.model.Users.saveUserAlbums();
        photos.model.Users.saveUserTagTypes();
        Platform.exit();
        System.exit(0);
    }
}
