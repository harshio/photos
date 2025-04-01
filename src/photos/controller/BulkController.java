package photos.controller;

import javafx.scene.control.Button;
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
    public void initialize(){
        Map<String, Set<photos.model.Photo>> albums = Users.userAlbums.get(Users.currentUser);
        if (albums == null) return;

        buttonContainer.getChildren().clear(); // clear previous buttons (if any)

        for (String albumName : albums.keySet()) {
            Button albumButton = new Button(albumName);
            albumButton.setOnAction(e -> {
                Users.currentAlbum = albumName;
                loadInAlbum(e); // Replace with your album-opening method
            });

            buttonContainer.getChildren().add(albumButton);
        }
        
    }
    public void createAlbum(ActionEvent e){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/CreateAlbum.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
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
            stage.setScene(new Scene(root));
            //For stage.setTitle(), we'll make sure to properly if-condition this one and only this one later.
            stage.setTitle("Dummy");
            stage.show();
        }
        catch (IOException ex) {
            ex.printStackTrace(); // Optional: replace with GUI error dialog
        }
    }
}
