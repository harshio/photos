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
public class DestinationController {
    @FXML
    private VBox albumButtons;
    @FXML Button backButton;
    public static String destinationAlbum;
    @FXML Button quitButton;
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

    public void quitApplication(ActionEvent e){
        photos.model.Users.saveUsersList();
        photos.model.Users.saveUserAlbums();
        photos.model.Users.saveUserTagTypes();
        Platform.exit();
        System.exit(0);
    }

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
