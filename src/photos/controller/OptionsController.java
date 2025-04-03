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
    public void displayPhoto(ActionEvent e){
        System.out.println("I'm a debugging message");
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/ShowOff.fxml"));
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
            stage.setTitle("Dummy");
            stage.show();
        }
        catch (IOException ex) {
            ex.printStackTrace(); // Optional: replace with GUI error dialog
        }
    }

    public void addTag(ActionEvent e){
        photos.model.Users.addTag(photos.model.Users.currentUser, photos.model.Users.currentAlbum, photos.model.Users.currentPhoto, newTag.getText());
        photos.model.Users.saveUserAlbums();
        newTag.setText("Write whatever tag you want here.");
    }

    public void deleteTag(ActionEvent e){
        photos.model.Users.removeTag(photos.model.Users.currentUser, photos.model.Users.currentAlbum, photos.model.Users.currentPhoto, dying.getText());
        photos.model.Users.saveUserAlbums();
        dying.setText("Write down the tag you want to be deleted here.");
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

    public void addCaption(ActionEvent e){
        if(photos.model.Users.getCaption(photos.model.Users.currentUser, photos.model.Users.currentAlbum, photos.model.Users.currentPhoto).equals("")){
            photos.model.Users.addCaption(photos.model.Users.currentUser, photos.model.Users.currentAlbum, photos.model.Users.currentPhoto, newCaption.getText());
            photos.model.Users.saveUserAlbums();
            addCaptionMessage.setText("");
            newCaption.setText("Write whatever caption you want here.");
        }
        else{
            addCaptionMessage.setText("You already have a caption");
            newCaption.setText("Write whatever caption you want here.");
        }
    }

    public void changeCaption(ActionEvent e){
        if(!photos.model.Users.getCaption(photos.model.Users.currentUser, photos.model.Users.currentAlbum, photos.model.Users.currentPhoto).equals("")){
            photos.model.Users.removeCaption(photos.model.Users.currentUser, photos.model.Users.currentAlbum, photos.model.Users.currentPhoto);
            photos.model.Users.addCaption(photos.model.Users.currentUser, photos.model.Users.currentAlbum, photos.model.Users.currentPhoto, differentCaption.getText());
            photos.model.Users.saveUserAlbums();
            changeCaptionMessage.setText("");
            differentCaption.setText("Write the new caption here.");
        }
        else{
            changeCaptionMessage.setText("Please create a caption first");
            differentCaption.setText("Write the new caption here.");
        }
    }

    public void placeType(ActionEvent e){
        Button clicked = (Button) e.getSource();
        newTag.setText(clicked.getText() + ", ");
    }

    public void insertTag(ActionEvent e){
        Button insertedTag = new Button(brandTag.getText());
        //Then we'll add it to the appropriate hashmap and save
        photos.model.Users.addUserTagType(photos.model.Users.currentUser, brandTag.getText());
        photos.model.Users.saveUserTagTypes();
        insertedTag.setOnAction(this::placeType);
        tagTypeBox.getChildren().add(insertedTag);
        brandTag.setText("e.g. emotion");
    }

    private Button makeTagButton(String tag) {
        Button tagButton = new Button(tag);
        tagButton.setOnAction(this::placeType);
        return tagButton;
    }
    
}
