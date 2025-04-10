package photos.controller;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
 * Controller for placing a photo into another album by
 * copying or transferring it. This screen is shown after the user
 * selects a destination album. Prevents duplicate placement and
 * allows the user to return if the photo already exists.
 * @author Harshi Oleti
 */
public class PlacingController {
    /**
     * Button that allows user to
     * copy photo in currently open album to
     * earlier-specified destination album.
     */
    @FXML Button copyButton;
    /**
     * Button that allows user to
     * transfer/move photo in currently open album to
     * earlier-specified destination album.
     */
    @FXML Button transferButton;
    /**
     * Error message in case user messes up, usually when a duplicate
     * photo is already present in destination album.
     */
    @FXML Text errorMessage;
    /**
     * VBox that gets a dynamically placed return button when a duplicate
     * photo is already present in destination album.
     */
    @FXML
    private VBox returnButtons;
    /**
     * Button that saves user data and subsequently quits application.
     */
    @FXML Button quitButton;
    /**
     * Pre-existing photo object that is placed in the destination album.
     */
    private Photo foundPhoto;
    /**
     * Initializes the controller by setting up button actions
     * and clearing the return button area.
     */
    public void initialize(){
        copyButton.setOnAction(e->copy(e));
        transferButton.setOnAction(e->transfer(e));
        errorMessage.setText("");
        returnButtons.getChildren().clear();
    }
    /**
     * Handles the "copy" action, adding the current photo to the
     * destination album if it does not already exist there. Successfully
     * prevents duplication.
     * @param e is the triggering event from the copyButton button.
     */
    public void copy(ActionEvent e){
        Set<Photo> destPhotos = Users.userAlbums.get(Users.currentUser).get(DestinationController.destinationAlbum).getPhotos();
        for(Photo p: destPhotos){
            if(p.getPath().equals(Users.currentPhoto)){
                errorMessage.setText("Duplicate photo already exists in this album. Please leave this page by hitting this newly generated return button.");
                copyButton.setOnAction(null);
                transferButton.setOnAction(null);
                Button returnButton = new Button("Return");
                returnButton.setOnAction(d -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Destination.fxml"));
                        Parent root = loader.load();
                        Stage stage = (Stage) copyButton.getScene().getWindow();
                        stage.getScene().setRoot(root);
                        stage.setTitle("Page of Albums to Copy/Transfer to");
                        stage.show();
                    } catch (IOException ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Failed to Load Page");
                        alert.setContentText("Something went wrong while loading the page.\nDetails: " + ex.getMessage());
                        alert.showAndWait();
                    }
                });
                returnButtons.getChildren().add(returnButton);
                return;
            }
        }
        Set<Photo> currentPhotos = Users.userAlbums.get(Users.currentUser).get(Users.currentAlbum).getPhotos(); 
        for(Photo p: currentPhotos){
            if(p.getPath().equals(Users.currentPhoto)){
                foundPhoto = p;
                break;
            }
        }

        Users.addExistingPhoto(Users.currentUser, DestinationController.destinationAlbum, foundPhoto);
        Users.saveUserAlbums();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Destination.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) copyButton.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Page of Albums to Copy/Transfer to");
            stage.show();
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Page");
            alert.setContentText("Something went wrong while loading the page.\nDetails: " + ex.getMessage());
            alert.showAndWait();
        }
    }
    /**
     * saves user data to disk and quits application.
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
     * Handles the "transfer" action, moving the current photo from the 
     * current album to the destination album. Also prevents duplicates.
     * @param e is the triggering event from the transferButton button
     */
    public void transfer(ActionEvent e){
        Set<Photo> destPhotos = Users.userAlbums.get(Users.currentUser).get(DestinationController.destinationAlbum).getPhotos();
        for(Photo p: destPhotos){
            if(p.getPath().equals(Users.currentPhoto)){
                errorMessage.setText("Duplicate photo already exists in this album. Please leave this page by hitting this newly generated return button.");
                copyButton.setOnAction(null);
                transferButton.setOnAction(null);
                Button returnButton = new Button("Return");
                returnButton.setOnAction(d -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Destination.fxml"));
                        Parent root = loader.load();
                        Stage stage = (Stage) copyButton.getScene().getWindow();
                        stage.getScene().setRoot(root);
                        stage.setTitle("Page of Albums to Copy/Transfer to");
                        stage.show();
                    } catch (IOException ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Failed to Load Page");
                        alert.setContentText("Something went wrong while loading the page.\nDetails: " + ex.getMessage());
                        alert.showAndWait();
                    }
                });
                returnButtons.getChildren().add(returnButton);
                return;
            }
        }
        Set<Photo> currentPhotos = Users.userAlbums.get(Users.currentUser).get(Users.currentAlbum).getPhotos(); 
        for(Photo p: currentPhotos){
            if(p.getPath().equals(Users.currentPhoto)){
                foundPhoto = p;
                break;
            }
        }

        Users.addExistingPhoto(Users.currentUser, DestinationController.destinationAlbum, foundPhoto);
        Users.removePhoto(Users.currentUser, Users.currentAlbum, Users.currentPhoto);
        Users.saveUserAlbums();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Album.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) copyButton.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Album Page");
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
