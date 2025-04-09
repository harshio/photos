package photos.controller;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
import java.util.Set;
public class OptionsController {
    @FXML Button delete;
    @FXML Button display;
    @FXML Button addTag;
    @FXML TextField newTag;
    @FXML Button dead;
    @FXML TextField dying;
    @FXML TextField newCaption;
    @FXML Button addCaption;
    @FXML TextField differentCaption;
    @FXML Button changeCaption;
    @FXML Button loadInAlbum;
    @FXML 
    private Text addCaptionMessage;
    @FXML
    private Text changeCaptionMessage;
    @FXML Button locationButton;
    @FXML Button nameButton;
    @FXML Button occasionButton;
    @FXML private VBox tagTypeBox;
    @FXML TextField brandTag;
    @FXML Button makeNewTag;
    @FXML Button move;
    @FXML Button quitButton;
    public void initialize(){
        tagTypeBox.getChildren().clear();
        tagTypeBox.getChildren().add(makeTagButton("location"));
        tagTypeBox.getChildren().add(makeTagButton("name"));
        tagTypeBox.getChildren().add(makeTagButton("occasion"));
        Set<String> tagTypes = Users.userTagTypes.get(Users.currentUser);

        if (tagTypes != null) {
            for (String tag : tagTypes) {
                Button tagButton = new Button(tag);
                tagButton.setOnAction(this::placeType); // reuse your existing method
                tagTypeBox.getChildren().add(tagButton);
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

    public void refresh() {
        tagTypeBox.getChildren().clear(); // Clear old tag buttons
    
        // Re-add default tag type buttons
        tagTypeBox.getChildren().add(makeTagButton("location"));
        tagTypeBox.getChildren().add(makeTagButton("name"));
        tagTypeBox.getChildren().add(makeTagButton("occasion"));
    
        // Load user-defined tag types
        Set<String> tagTypes = Users.userTagTypes.get(Users.currentUser);
        if (tagTypes != null) {
            for (String tag : tagTypes) {
                Button tagButton = new Button(tag);
                tagButton.setOnAction(this::placeType);
                tagTypeBox.getChildren().add(tagButton);
            }
        }
    
        // Optionally clear text fields & messages
        newTag.setText("");
        dying.setText("");
        newCaption.setText("");
        differentCaption.setText("");
        addCaptionMessage.setText("");
        changeCaptionMessage.setText("");
        brandTag.setText("");
    }
    
    public void displayPhoto(ActionEvent e){
        System.out.println("I'm a debugging message");
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/ShowOff.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

            //For stage.setTitle(), we'll make sure to properly if-condition this one and only this one later.
            stage.setTitle("Display of Photo");
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
            stage.getScene().setRoot(root);

            //For stage.setTitle(), we'll make sure to properly if-condition this one and only this one later.
            stage.setTitle("Album Page");
            stage.show();
        }
        catch (IOException ex) {
            ex.printStackTrace(); // Optional: replace with GUI error dialog
        }
    }

    public void addTag(ActionEvent e){
        String[] properTags = newTag.getText().split(",");
        String tagName = properTags[0].trim();
        String tagValue = properTags[1].trim();
        String bestFormat = tagName + ", " + tagValue;
        photos.model.Users.addTag(photos.model.Users.currentUser, photos.model.Users.currentAlbum, photos.model.Users.currentPhoto, bestFormat.trim().toLowerCase());
        photos.model.Users.saveUserAlbums();
        newTag.setText("");
    }

    public void deleteTag(ActionEvent e){
        String[] properTags = dying.getText().split(",");
        String tagName = properTags[0].trim();
        String tagValue = properTags[1].trim();
        String bestFormat = tagName + ", " + tagValue;
        photos.model.Users.removeTag(photos.model.Users.currentUser, photos.model.Users.currentAlbum, photos.model.Users.currentPhoto, bestFormat.trim().toLowerCase());
        photos.model.Users.saveUserAlbums();
        dying.setText("");
    }

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
            ex.printStackTrace(); // Optional: replace with GUI error dialog
        }
    }

    public void addCaption(ActionEvent e){
        if(photos.model.Users.getCaption(photos.model.Users.currentUser, photos.model.Users.currentAlbum, photos.model.Users.currentPhoto).equals("")){
            photos.model.Users.addCaption(photos.model.Users.currentUser, photos.model.Users.currentAlbum, photos.model.Users.currentPhoto, newCaption.getText());
            photos.model.Users.saveUserAlbums();
            addCaptionMessage.setText("");
            changeCaptionMessage.setText("");
            newCaption.setText("");
        }
        else{
            addCaptionMessage.setText("You already have a caption");
            newCaption.setText("");
        }
    }

    public void changeCaption(ActionEvent e){
        if(!photos.model.Users.getCaption(photos.model.Users.currentUser, photos.model.Users.currentAlbum, photos.model.Users.currentPhoto).equals("")){
            photos.model.Users.removeCaption(photos.model.Users.currentUser, photos.model.Users.currentAlbum, photos.model.Users.currentPhoto);
            photos.model.Users.addCaption(photos.model.Users.currentUser, photos.model.Users.currentAlbum, photos.model.Users.currentPhoto, differentCaption.getText());
            photos.model.Users.saveUserAlbums();
            addCaptionMessage.setText("");
            changeCaptionMessage.setText("");
            differentCaption.setText("");
        }
        else{
            changeCaptionMessage.setText("Please create a caption first");
            differentCaption.setText("");
        }
    }

    public void placeType(ActionEvent e){
        Button clicked = (Button) e.getSource();
        newTag.setText(clicked.getText() + ", ");
    }

    public void insertTag(ActionEvent e){
        Button insertedTag = new Button(brandTag.getText().trim());
        //Then we'll add it to the appropriate hashmap and save
        photos.model.Users.addUserTagType(photos.model.Users.currentUser, brandTag.getText().trim());
        photos.model.Users.saveUserTagTypes();
        insertedTag.setOnAction(this::placeType);
        tagTypeBox.getChildren().add(insertedTag);
        brandTag.setText("");
    }

    public void loadInDestination(ActionEvent e){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Destination.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

            //For stage.setTitle(), we'll make sure to properly if-condition this one and only this one later.
            stage.setTitle("Page of Albums to Copy/Transfer to");
            stage.show();
        }
        catch (IOException ex) {
            ex.printStackTrace(); // Optional: replace with GUI error dialog
        }
    }

    private Button makeTagButton(String tag) {
        Button tagButton = new Button(tag);
        tagButton.setOnAction(this::placeType);
        return tagButton;
    }
    
}
