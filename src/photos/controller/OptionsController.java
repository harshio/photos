package photos.controller;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.collections.*;
import java.io.*;
//import javafx.scene.control.ListView; this import probably won't be used in this class but I'm paranoid
import java.util.ArrayList;
public class OptionsController {
    @FXML Button delete;
    @FXML Button display;

    public void displayPhoto(ActionEvent e){
        System.out.println("I'm a debugging message");
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/ShowOff.fxml"));
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

    public void deletePhoto(ActionEvent e){
        photos.model.Users.photoPaths.remove(photos.model.Users.currentPhoto);
        photos.model.Users.removePhoto(photos.model.Users.currentUser, photos.model.Users.currentAlbum, photos.model.Users.currentPhoto);
        photos.model.Users.saveUserAlbums();
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
