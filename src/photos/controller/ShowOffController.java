package photos.controller;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
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
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;


//import javafx.scene.control.ListView; this import probably won't be used in this class but I'm paranoid
import java.util.ArrayList;
import java.util.Set;
public class ShowOffController {
    @FXML
    private ImageView largeImageView;

    @FXML
    private VBox tagBox;

    public void initialize() {
        System.out.println("ShowOffController initialized");
        System.out.println("Users.currentPhoto = " + Users.currentPhoto);

        String photoPath = Users.currentPhoto;
        if (photoPath != null && !photoPath.isEmpty()) {
            File file = new File(photoPath);
            if (file.exists()) {
                Image fullImage = new Image(file.toURI().toString());
                largeImageView.setImage(fullImage);
            }
        }

        Set<photos.model.Photo> albumPhotos = photos.model.Users.userAlbums
            .get(Users.currentUser)
            .get(Users.currentAlbum);
        
        for(photos.model.Photo photo: albumPhotos){
            if(photo.getPath().equals(Users.currentPhoto)){
                Set<String> tags = photo.getTags();
                for(String tag: tags){
                    Label tagLabel = new Label(tag);
                    tagBox.getChildren().add(tagLabel);
                }
                break;
            }
        }
    }
}
