package photos.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import photos.model.Users;
import javafx.application.Platform;
import javafx.collections.*;
import java.io.*;
//import javafx.scene.control.ListView; this import probably won't be used in this class but I'm paranoid
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
/**
 * Controller for the home page.
 * Internally called BulkController because it's
 * the entry point for the bulk
 * of the application. It allows users to
 * open, view, and create albums, log out, search,
 * and quit the application.
 * Authored by Chloe Wolohan.
 */
public class BulkController {
    /**
     * Button that triggers the creation of a new album.
     */
    @FXML Button createButton;
    /**
     * VBox container that holds dynamically generated album buttons.
     */
    @FXML
    private VBox buttonContainer;
    /**
     * Button that logs the user out and returns to the login screen.
     */
    @FXML Button logOut;
    /**
     * Button that takes the user to the search interface.
     */
    @FXML Button searchButton;
    /**
     * Button that saves user data and quits the application.
     */
    @FXML Button quitButton;
    /**Initializes the album list by creating buttons
     * for each album.
     */
    public void initialize(){
        Map<String, photos.model.Album> albums = Users.userAlbums.get(Users.currentUser);
        if (albums == null) return;

        buttonContainer.getChildren().clear(); // clear previous buttons (if any)

        for (Map.Entry<String, photos.model.Album> entry : albums.entrySet()) {
            String albumName = entry.getKey();
            photos.model.Album album = entry.getValue();
        
        
            Button albumButton = new Button(albumName); // just the album name

            Label dateLabel = new Label("(" + album.getOldestDate() + " → " + album.getNewestDate() + ")");
            dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");

            Label number = new Label("Photos stored: "+album.getSize());
            dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");

            VBox albumBox = new VBox(5); // or whatever spacing you prefer
            albumBox.getChildren().addAll(albumButton, dateLabel, number);
            albumButton.setOnAction(e -> {
                Users.currentAlbum = albumName;
                loadInAlbum(e); // Replace with your album-opening method
            });

            buttonContainer.getChildren().add(albumBox);
        }
        
    }
    /**
     * Saves user data and quits the application.
     * @param e is the triggering event from the quitButton button
     */
    public void quitApplication(ActionEvent e){
        photos.model.Users.saveUsersList();
        photos.model.Users.saveUserAlbums();
        photos.model.Users.saveUserTagTypes();
        Platform.exit();
        System.exit(0);
    }
    /**
     * Opens the search interface.
     * @param e is the triggering event from the searchButton button.
     */
    public void loadInSearch(ActionEvent e){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Search.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

            //For stage.setTitle(), we'll make sure to properly if-condition this one and only this one later.
            stage.setTitle("Search Page");
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
     * Logs out the user and returns to the login page.
     * @param e is the triggering event from the logOut button.
     */
    public void logOut(ActionEvent e){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Logout.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

            //For stage.setTitle(), we'll make sure to properly if-condition this one and only this one later.
            stage.setTitle("Logout Page");
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
     * Opens the album creation page.
     * @param e is the triggering event from the createButton button.
     */
    public void createAlbum(ActionEvent e){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/CreateAlbum.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

            //For stage.setTitle(), we'll make sure to properly if-condition this one and only this one later.
            stage.setTitle("Album Creation Page");
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
     * Opens a selected album and loads in Album.fxml
     * to view said album.
     * @param e is the triggering event from any dynamically placed album button in the buttonContainer VBox
     */
    public void loadInAlbum(ActionEvent e){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Album.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

            //For stage.setTitle(), we'll make sure to properly if-condition this one and only this one later.
            stage.setTitle("Album Page");
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
}
