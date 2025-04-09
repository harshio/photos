package photos.controller;
import javafx.scene.control.Alert;
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
import javafx.application.Platform;
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
    @FXML Button quitButton;

    public void initialize() {       

        if (!tagBox.getChildren().isEmpty()) {
            tagBox.getChildren().clear();
        } 

        String photoPath = Users.currentPhoto;
        if (photoPath != null && !photoPath.isEmpty()) {
            File file = new File(photoPath);
            if (file.exists()) {
                Image fullImage = new Image(file.toURI().toString());
                largeImageView.setImage(fullImage);
            }
        }

        photos.model.Album album = photos.model.Users.userAlbums
            .get(Users.currentUser)
            .get(Users.currentAlbum);
        if (album == null) return;
        Set<photos.model.Photo> albumPhotos = album.getPhotos();

        
        for(photos.model.Photo photo: albumPhotos){
            if(photo.getPath().equals(Users.currentPhoto)){
                Set<String> tags = photo.getTags();
                for(String tag: tags){
                    Label tagLabel = new Label(tag);
                    tagBox.getChildren().add(tagLabel);
                }
                if(photo.getCaption() != null && !photo.getCaption().isEmpty()){
                    tagBox.getChildren().add(new Label(photo.getCaption()));
                }
                tagBox.getChildren().add(new Label(photo.getDates().iterator().next()));
                break;
            }
        }
    }

    public void quitApplication(ActionEvent e){
        photos.model.Users.saveUsersList();
        photos.model.Users.saveUserAlbums();
        photos.model.Users.saveUserTagTypes();
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void loadOptionsView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Options.fxml"));
            Parent root = loader.load();

            // Refresh if needed
            OptionsController controller = loader.getController();
            controller.refresh();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Options Page");
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Page");
            alert.setContentText("Something went wrong while loading the page.\nDetails: " + e.getMessage());
            alert.showAndWait();
        }
    }

}
