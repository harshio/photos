package photos.controller;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import photos.model.Photo;
import photos.model.Users;
import photos.model.Album;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * Controller for the destination album selection interface.
 * Allows user to choose a target album for copying or moving a photo.
 * Skips current album to avoid copying/moving within the same album
 */
public class DestinationController {
    @FXML
    private VBox albumButtons;
    @FXML Button backButton;
    /**
     * Static field to hold the name of the destination album
     * selected by the user.
     */
    public static String destinationAlbum;
    @FXML Button quitButton;
    /**
     * Initializes the destination album selection UI by
     * populating a list of album buttons, excluding the currently
     * open album. Each button, when clicked, sets the destination
     * and transitions to the Copy/Transfer page
     */
    public void initialize(){
        Set<String> albumNames = Users.userAlbums.get(Users.currentUser).keySet();
        for(String album: albumNames){
            if(album.equals(Users.currentAlbum)) continue;
            Button b = new Button(album);
            b.setOnAction(e -> {
                destinationAlbum = b.getText();
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Placing.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) albumButtons.getScene().getWindow();
                    stage.getScene().setRoot(root);
                    stage.setTitle("Copy/Transfer Page");
                    stage.show();
                } catch (IOException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to Load Page");
                    alert.setContentText("Something went wrong while loading the page.\nDetails: " + ex.getMessage());
                    alert.showAndWait();
                }
            });
            albumButtons.getChildren().add(b);
        }
    }
    /**
     * Saves user data and quits application.
     * @param e is the triggering event from the quitButton button.
     */
    public void quitApplication(ActionEvent e){
        photos.model.Users.saveUsersList();
        photos.model.Users.saveUserAlbums();
        photos.model.Users.saveUserTagTypes();
        Platform.exit();
        System.exit(0);
    }
    /**
     * Returns to the photo options screen (Option.fxml)
     * @param e is the triggering event from the backButton button.
     */
    public void backToOptions(ActionEvent e){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Options.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Options Page");
            stage.show();
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Page");
            alert.setContentText("Something went wrong while loading the page.\nDetails: " + ex.getMessage());
            alert.showAndWait();
        }
    }
}
