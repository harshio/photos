package photos.controller;
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
public class PlacingController {
    @FXML Button copyButton;
    @FXML Button transferButton;
    @FXML Text errorMessage;
    @FXML
    private VBox returnButtons;

    private Photo foundPhoto;

    public void initialize(){
        copyButton.setOnAction(e->create(e));
        transferButton.setOnAction(e->transfer(e));
        errorMessage.setText("");
        returnButtons.getChildren().clear();
    }

    public void create(ActionEvent e){
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
                        stage.setTitle("Bulk View");
                        stage.show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
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
            stage.setTitle("Bulk View");
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

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
                        stage.setTitle("Bulk View");
                        stage.show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
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
            stage.setTitle("Bulk View");
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
