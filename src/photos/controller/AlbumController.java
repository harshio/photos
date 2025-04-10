package photos.controller;

import javafx.scene.control.Alert;
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
import photos.model.Album;
import photos.model.Photo;
import photos.model.Users;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
//import javafx.scene.control.ListView; this import probably won't be used in this class but I'm paranoid
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * Controller associated with Album.fxml.
 * Album.fxml is responsible for display of all photos in user album.
 * It does so by formatting them as a manual slideshow.
 * This class supports adding, deleting, and renaming albums,
 * as well as navigating photo slides.
 * @author Harshi Oleti
 */
public class AlbumController {
    /**
     * Button to leave the current album view and return
     * to homepage.
     */
    @FXML Button leave;
    /**
     * Button to upload/add a new photo to the current album.
     */
    @FXML Button add;
    /**
     * Button to delete the current album.
     */
    @FXML Button delete;
    /**
     * Container holding the list of photos in the current album.
     */
    @FXML
    private VBox photoList;
    /**
     * Buttons to go back to the previous and next photo in the slideshow.
     */
    @FXML
    private Button prevButton, nextButton;
    /**
     * The main container for displaying slideshow-style photo previews.
     */
    @FXML private VBox slideContainer;
    /**
     * A placeholder pane shown while photos are being loaded in the background.
     */
    @FXML private VBox loadingPane;
    /**
     * Button to quit the application and save data.
     */
    @FXML Button quitButton;
    /**
     * Button to rename the current album.
     */
    @FXML Button renameButton;
    /**
     * Text field to input the new album name while renaming
     */
    @FXML TextField newName;
    /**
     * Text area used to display error messages to the user.
     */
    @FXML Text errorMessage;
    /**
     * A dynamic list of VBox photo "slides" displayed in the slideshow.
     */
    private ArrayList<VBox> slides = new ArrayList<>();
    /**
     * The current index of the photo being shown in the slideshow.
     */
    private int currentIndex = 0;
    /**
     * A cached reference to the root node of the Options.fxml view,
     * used when transitioning to the photo options.
     */
    private Parent optionsRoot = null;
    /**
     * A cached FXMLLoader instance for loading the Options.fxml controller.
     */
    private FXMLLoader optionsLoader = null;

    /**
     * Initializes Album.fxml. Loads all photos 
     * currently stored in the currently selected
     * album and prepares the slideshow interface
     * with next and previous buttons to help navigate.
     */
    public void initialize() {
        slideContainer.setVisible(false);
        loadingPane.setVisible(true);
        errorMessage.setText("");

        photos.model.Album album = photos.model.Users.userAlbums
            .get(Users.currentUser)
            .get(Users.currentAlbum);
        if (album == null) return;
    
        Set<photos.model.Photo> albumPhotos = album.getPhotos();
        if (albumPhotos == null) return;
    
        slides.clear();
        slideContainer.getChildren().clear();
    
        new Thread(() -> {
            ArrayList<VBox> tempSlides = new ArrayList<>();
    
            for (photos.model.Photo photo : albumPhotos) {
                final String photoPath = photo.getPath();
                File file = new File(photoPath);
                String modifiedTime;
    
                try {
                    modifiedTime = "Date taken: " +
                        Files.getLastModifiedTime(file.toPath()).toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                } catch (IOException ex) {
                    modifiedTime = "Date taken: (unavailable)";
                }
    
                String trueTime = modifiedTime.substring(12);
    
                // Only update real date if not already present
                if (photo.getRealDates() == null || photo.getRealDates().isEmpty()) {
                    Users.addRealDate(Users.currentUser, Users.currentAlbum, photoPath, trueTime);
                }
                Users.addDate(Users.currentUser, Users.currentAlbum, photoPath, modifiedTime);
    
                Label caption = new Label(Users.getCaption(Users.currentUser, Users.currentAlbum, photoPath));
    
                Image image = new Image(file.toURI().toString(), 100, 100, true, true, false);
                ImageView imageView = new ImageView(image);
                imageView.setOnMouseClicked(e -> {
                    Users.currentPhoto = photoPath;
                    loadInOptions(e);
                });
    
                VBox slide = new VBox(5, imageView, caption);
                slide.setStyle("-fx-alignment: center;");
                tempSlides.add(slide);
            }
    
            // Save only once after all processing is done
            Users.saveUserAlbums();
    
            // Switch to JavaFX thread for UI update
            Platform.runLater(() -> {
                slides.addAll(tempSlides);
                if (!slides.isEmpty()) {
                    slideContainer.getChildren().setAll(slides.get(currentIndex));
                    updateNavigationButtons();
                }

                loadingPane.setVisible(false);
                slideContainer.setVisible(true);
            });
        }).start();
    }
    /**
     * Saves data and cleanly quits the application.
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
     * Navigates back to the home page and also saves the oldest and newest dates of the album.
     * @param e is the triggering event from the leave button.
     */
    public void leaveAlbum(ActionEvent e){
        //we have to calculate oldest date and newest date in here
        photos.model.Album album = photos.model.Users.userAlbums
        .get(photos.model.Users.currentUser)
        .get(photos.model.Users.currentAlbum);

        if (album == null) return;

        Set<photos.model.Photo> albumPhotos = album.getPhotos();

        if (albumPhotos == null || albumPhotos.isEmpty()) {
            loadBulkView(e); // 👈 reuse your scene load logic
            return;
        }
        

        Calendar oldest = null;
        Calendar newest = null;

        for (photos.model.Photo photo : albumPhotos) {
            Set<Calendar> realDates = photo.getRealDates();
            for (Calendar date : realDates) {
                if (oldest == null || date.before(oldest)) {
                    oldest = (Calendar) date.clone();
                }
                if (newest == null || date.after(newest)) {
                    newest = (Calendar) date.clone();
                }
            }
        }


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String oldestStr = sdf.format(oldest.getTime());
        String newestStr = sdf.format(newest.getTime());
        album.setOldestDate(oldestStr);
        album.setNewestDate(newestStr);

        photos.model.Users.saveUserAlbums();

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Bulk.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            //For stage.setTitle(), we'll make sure to properly if-condition this one and only this one later.
            stage.setTitle("Home Page");
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
     * Deletes the current album and returns to the home page.
     * @param e is the triggering event from the delete button.
     */
    public void deleteAlbum(ActionEvent e){
        photos.model.Users.removeAlbum(photos.model.Users.currentUser, photos.model.Users.currentAlbum);
        photos.model.Users.saveUserAlbums();
        Users.currentAlbum = null;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Bulk.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Home Page");
            stage.show();
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Page");
            alert.setContentText("Something went wrong while loading the page.\nDetails: " + ex.getMessage());
            alert.showAndWait();
        }
    }
    /**
     * Handles uploading a new photo to the album,
     * support both new and previously existing photos via
     * object reuse.
     * @param e is the triggering event from the add button
     */
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

            Image image = new Image(new File(path).toURI().toString(), 100, 100, true, true, false);
            ImageView imageView = new ImageView(image);
            final String photoPath = path;
            //Here is where we'll insert our new code for object reuse.
            Photo existingPhoto = Users.findPhotoInUserAlbums(Users.currentUser, photoPath);
            //then we'll add that pre-existing photo object to this new album (note that photo duplication within an album is already banned, and should be banned)
            if(existingPhoto != null){
                Users.addExistingPhoto(Users.currentUser, Users.currentAlbum, existingPhoto);
                String modifiedTime;
                try {
                    modifiedTime = "Date taken: " +
                        Files.getLastModifiedTime(new File(photoPath).toPath()).toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                } catch (IOException ex) {
                    modifiedTime = "Date taken: (unavailable)";
                }
                String trueTime = modifiedTime.substring(12);

                Users.addRealDate(Users.currentUser, Users.currentAlbum, photoPath, trueTime);
                Users.addDate(Users.currentUser, Users.currentAlbum, photoPath, modifiedTime);
                photos.model.Users.saveUserAlbums();
                //Then in here, we'll immediately add the photo and whatever the caption is below the photo
                //We already have the correct imageView element, so we're good there. We'll make a new VBox here
                for (VBox existingSlide : slides) {
                    for (javafx.scene.Node node : existingSlide.getChildren()) {
                        if (node instanceof ImageView) {
                            ImageView iv = (ImageView) node;
                            if (iv.getImage().getUrl().equals(image.getUrl())) {
                                return; // Photo already in slides
                            }
                        }
                    }
                }                
                Label caption = new Label(existingPhoto.getCaption());
                VBox slide = new VBox(5, imageView, caption);
                //Then we'll add it to slides
                slide.setStyle("-fx-alignment: center;");
                imageView.setOnMouseClicked(ev -> {
                    photos.model.Users.currentPhoto = photoPath;
                    loadInOptions(ev);
                });
                slides.add(slide);
                currentIndex = slides.size() - 1; // Show the new image
                slideContainer.getChildren().setAll(slide);
                updateNavigationButtons();
                //Then we'll have to break out the code immediately 
                return;
            }
            String modifiedTime;
            try {
                modifiedTime = "Date taken: " +
                    Files.getLastModifiedTime(new File(photoPath).toPath()).toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (IOException ex) {
                modifiedTime = "Date taken: (unavailable)";
            }
            String trueTime = modifiedTime.substring(12);
            for (VBox existingSlide : slides) {
                for (javafx.scene.Node node : existingSlide.getChildren()) {
                    if (node instanceof ImageView) {
                        ImageView iv = (ImageView) node;
                        if (iv.getImage().getUrl().equals(image.getUrl())) {
                            return; // Photo already in slides
                        }
                    }
                }
            }            
            VBox slide = new VBox(5, imageView);
            slide.setStyle("-fx-alignment: center;");
            imageView.setOnMouseClicked(ev -> {
                photos.model.Users.currentPhoto = photoPath;
                loadInOptions(ev);
            });
            slides.add(slide);
            currentIndex = slides.size() - 1; // Show the new image
            slideContainer.getChildren().setAll(slide);
            updateNavigationButtons();
            photos.model.Users.addPhoto(photos.model.Users.currentUser, photos.model.Users.currentAlbum, path);
            photos.model.Users.addRealDate(photos.model.Users.currentUser, photos.model.Users.currentAlbum, photoPath, trueTime);
            photos.model.Users.addDate(photos.model.Users.currentUser, photos.model.Users.currentAlbum, photoPath, modifiedTime);
            photos.model.Users.saveUserAlbums();
        }
    }
    /**
     * Loads the Options.fxml page for the 
     * selected photo, allowing tag and caption edits, along
     * with photo deletion, photo copy/transfer, and so on and so forth.
     * Will be elaborated on in the javadoc comments for OptionsController
     * @param e is mouse click on any of the thumbnail pics in the slideshow
     */
    public void loadInOptions(MouseEvent e){
        try {
            if (optionsRoot == null) {
                optionsLoader = new FXMLLoader(getClass().getResource("/photos/view/Options.fxml"));
                optionsRoot = optionsLoader.load();
            }
    
            // ✅ Refresh content for the currently selected photo
            OptionsController controller = optionsLoader.getController();
            controller.refresh();
    
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(optionsRoot);
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
    /**
     * Shows next photo in album slideshow
     * @param e is the triggering event from the nextButton button
     */
    @FXML
    private void handleNext(ActionEvent e) {
        if (currentIndex < slides.size() - 1) {
            currentIndex++;
            slideContainer.getChildren().setAll(slides.get(currentIndex));
            updateNavigationButtons();
        }
    }
    /**
     * Shows previous photo in album slideshow.
     * @param e is the triggering event from the prevButton button
     */
    @FXML
    private void handlePrev(ActionEvent e) {
        if (currentIndex > 0) {
            currentIndex--;
            slideContainer.getChildren().setAll(slides.get(currentIndex));
            updateNavigationButtons();
        }
    }
    /**
     * Navigates to the home page.
     * @param e is the triggering event
     */
    private void loadBulkView(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Bulk.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Home Page");
            stage.show();
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Page");
            alert.setContentText("Something went wrong while loading the page.\nDetails: " + ex.getMessage());
            alert.showAndWait();
        }
    }
    /**
     * Updates visibility of next/prev buttons based on slideshow position
     */
    private void updateNavigationButtons() {
        boolean multipleSlides = slides.size() > 1;
        prevButton.setVisible(currentIndex > 0 && multipleSlides);
        nextButton.setVisible(currentIndex < slides.size() - 1 && multipleSlides);
    } 
    /**
     * Renames current album if new name doesn't already exist.
     * Updates user model and current album reference.
     * @param e is the triggering event from the renameButton
     */
    public void renameAlbum(ActionEvent e){
        String user = photos.model.Users.currentUser;
        String oldName = photos.model.Users.currentAlbum;
        String newAlbumName = newName.getText().trim();

        Map<String, Album> albums = photos.model.Users.userAlbums.get(user);

        if (albums.containsKey(newAlbumName)) {
            //code to produce an error message
            errorMessage.setText("An album with this name already exists in your album selection.");
            return;
        }

        // Proceed with renaming
        Album album = albums.get(oldName);
        albums.put(newAlbumName, album);
        albums.remove(oldName);
        photos.model.Users.currentAlbum = newAlbumName;
        photos.model.Users.saveUserAlbums();
        newName.setText("");
        errorMessage.setText("");

    }
}
