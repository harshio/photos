package photos.controller;

import javafx.scene.control.Alert;
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
/**
 * Controller for the photo options view,
 * or Options.fxml. This controller provides
 * functionality to add/remove captions and tags,
 * view a photo in a fuller, separate display, and
 * copy or transfer/move a photo to another album.
 */
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
    /**
     * Initializes the tag type buttons dynamically based on the user's
     * available tag types. Called automatically when the scene is loaded.
     */
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
    /**
     * Quits the application after saving all user data
     * @param e is the triggering event from the quitButton button
     */
    public void quitApplication(ActionEvent e){
        photos.model.Users.saveUsersList();
        photos.model.Users.saveUserAlbums();
        photos.model.Users.saveUserTagTypes();
        Platform.exit();
        System.exit(0);
    }
    /**
     * Resets input fields and regenerates tag type buttons
     */
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
    /**
     * Loads the fuller, separate photo display view.
     * @param e is the triggering event from the display button
     */
    public void displayPhoto(ActionEvent e){
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Page");
            alert.setContentText("Something went wrong while loading the page.\nDetails: " + ex.getMessage());
            alert.showAndWait(); 
        }
    }
    /**
     * Deletes the currently viewed
     * photo from the album and returns to the
     * album page
     * @param e is the triggering event from the delete button
     */
    public void deletePhoto(ActionEvent e){
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Page");
            alert.setContentText("Something went wrong while loading the page.\nDetails: " + ex.getMessage());
            alert.showAndWait();
        }
    }
    /**
     * Adds a tag to the current photo using the input from the tag field.
     * @param e is the triggering event from the addTag button
     */
    public void addTag(ActionEvent e){
        String[] properTags = newTag.getText().split(",");
        String tagName = properTags[0].trim();
        String tagValue = properTags[1].trim();
        String bestFormat = tagName + ", " + tagValue;
        photos.model.Users.addTag(photos.model.Users.currentUser, photos.model.Users.currentAlbum, photos.model.Users.currentPhoto, bestFormat.trim().toLowerCase());
        photos.model.Users.saveUserAlbums();
        newTag.setText("");
    }
    /**
     * Removes a tag from the current photo usinng the input from the tag field.
     * @param e is the triggering event from the dead button
     */
    public void deleteTag(ActionEvent e){
        String[] properTags = dying.getText().split(",");
        String tagName = properTags[0].trim();
        String tagValue = properTags[1].trim();
        String bestFormat = tagName + ", " + tagValue;
        photos.model.Users.removeTag(photos.model.Users.currentUser, photos.model.Users.currentAlbum, photos.model.Users.currentPhoto, bestFormat.trim().toLowerCase());
        photos.model.Users.saveUserAlbums();
        dying.setText("");
    }
    /**
     * Returns to the album page from the options screen.
     * @param e is the triggering event from the loadInAlbum button.
     */
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Page");
            alert.setContentText("Something went wrong while loading the page.\nDetails: " + ex.getMessage());
            alert.showAndWait();
        }
    }
    /**
     * Adds a caption to the currently selected photo,
     * only if it doesn't already have one.
     * @param e is the triggering event from the addCaption button.
     */
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
    /**
     * changes the caption of the current photo, only if one
     * already exists of course.
     * @param e is the triggering event from the changeCaption button.
     */
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
    /**
     * Places the selected tag type in the newTag field prefix for
     * user convenience.
     * @param e is the triggering event.
     */
    public void placeType(ActionEvent e){
        Button clicked = (Button) e.getSource();
        newTag.setText(clicked.getText() + ", ");
    }
    /**
     * adds a custom tag type to the user's tag list and UI.
     * @param e is the triggering event from the makeNewTag button.
     */
    public void insertTag(ActionEvent e){
        Button insertedTag = new Button(brandTag.getText().trim());
        //Then we'll add it to the appropriate hashmap and save
        photos.model.Users.addUserTagType(photos.model.Users.currentUser, brandTag.getText().trim());
        photos.model.Users.saveUserTagTypes();
        insertedTag.setOnAction(this::placeType);
        tagTypeBox.getChildren().add(insertedTag);
        brandTag.setText("");
    }
    /**
     * Loads the destination album selection page for copying
     * or transferring the currently selected photo.
     * @param e is the triggering event from the move button.
     */
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Page");
            alert.setContentText("Something went wrong while loading the page.\nDetails: " + ex.getMessage());
            alert.showAndWait();
        }
    }
    /**
     * Creates a new tag type button with an associated action
     * @param tag is the name of the tag type
     * @return a configured Button object
     */
    private Button makeTagButton(String tag) {
        Button tagButton = new Button(tag);
        tagButton.setOnAction(this::placeType);
        return tagButton;
    }
    
}
