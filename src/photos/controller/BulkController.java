package photos.controller;

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
import javafx.collections.*;
import java.io.*;
//import javafx.scene.control.ListView; this import probably won't be used in this class but I'm paranoid
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
public class BulkController {
    @FXML Button createButton;
    @FXML
    private VBox buttonContainer;
    @FXML Button logOut;
    @FXML Button searchButton;
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

    public void loadInSearch(ActionEvent e){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Search.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

            //For stage.setTitle(), we'll make sure to properly if-condition this one and only this one later.
            stage.setTitle("Dummy");
            stage.show();
        }
        catch (IOException ex) {
            ex.printStackTrace(); // Optional: replace with GUI error dialog
        }
    }

    public void logOut(ActionEvent e){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Logout.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

            //For stage.setTitle(), we'll make sure to properly if-condition this one and only this one later.
            stage.setTitle("Dummy");
            stage.show();
        }
        catch (IOException ex) {
            ex.printStackTrace(); // Optional: replace with GUI error dialog
        }
    }
    public void createAlbum(ActionEvent e){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/CreateAlbum.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

            //For stage.setTitle(), we'll make sure to properly if-condition this one and only this one later.
            stage.setTitle("Dummy");
            stage.show();
        }
        catch (IOException ex) {
            ex.printStackTrace(); // Optional: replace with GUI error dialog
        }
    }

    public void loadInAlbum(ActionEvent e){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Album.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

            //For stage.setTitle(), we'll make sure to properly if-condition this one and only this one later.
            stage.setTitle("Dummy");
            stage.show();
        }
        catch (IOException ex) {
            ex.printStackTrace(); // Optional: replace with GUI error dialog
        }
    }
}
