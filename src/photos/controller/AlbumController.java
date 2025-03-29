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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import photos.model.Users;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.*;
//import javafx.scene.control.ListView; this import probably won't be used in this class but I'm paranoid
import java.util.ArrayList;
import java.util.Set;
public class AlbumController {
    @FXML Button leave;
    @FXML Button add;
    @FXML Button delete;
    @FXML
    private VBox photoList;

    public void initialize(){
        Set<String> albumPhotos = photos.model.Users.userAlbums
            .get(Users.currentUser)
            .get(Users.currentAlbum);

        if (albumPhotos == null) return;
        photoList.getChildren().clear();

        Platform.runLater(() -> {
            for (String path : albumPhotos) {
                final String photoPath = path; // capture correctly
                Image image = new Image(new File(photoPath).toURI().toString(), 100, 100, true, true, true);
                ImageView imageView = new ImageView(image);
            
                imageView.setOnMouseClicked(e -> {
                    photos.model.Users.currentPhoto = photoPath;
                    loadInOptions(e);
                });
            
                photoList.getChildren().add(imageView);
            }
        });
        
    }

    public void leaveAlbum(ActionEvent e){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Bulk.fxml"));
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

    public void deleteAlbum(ActionEvent e){
        Users.albumNames.remove(Users.currentAlbum);
        photos.model.Users.removeAlbum(photos.model.Users.currentUser, photos.model.Users.currentAlbum);
        photos.model.Users.saveUserAlbums();
        Users.currentAlbum = null;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Bulk.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Bulk View");
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void uploadPhoto(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a Photo");

        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        File selectedFile = fileChooser.showOpenDialog(((Node) e.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            String path = selectedFile.getAbsolutePath();
            photos.model.Users.photoPaths.add(path);

            Image image = new Image(new File(path).toURI().toString(), 100, 100, true, true, true);
            ImageView imageView = new ImageView(image);
            final String photoPath = path;
            imageView.setOnMouseClicked(ev -> {
                photos.model.Users.currentPhoto = photoPath;
                loadInOptions(ev);
            });
            photoList.getChildren().add(imageView);
            photos.model.Users.addPhoto(photos.model.Users.currentUser, photos.model.Users.currentAlbum, path);
            photos.model.Users.saveUserAlbums();
            System.out.println("Added photo: " + path);
        } else {
            System.out.println("No file selected.");
        }
    }

    public void loadInOptions(MouseEvent e){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Options.fxml"));
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
