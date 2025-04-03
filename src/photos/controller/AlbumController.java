package photos.controller;

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
public class AlbumController {
    @FXML Button leave;
    @FXML Button add;
    @FXML Button delete;
    @FXML
    private VBox photoList;
    @FXML
    private Button prevButton, nextButton;
    @FXML private VBox slideContainer;
    @FXML private VBox loadingPane;


    private ArrayList<VBox> slides = new ArrayList<>();
    private int currentIndex = 0;

    public void initialize() {
        slideContainer.setVisible(false);
        loadingPane.setVisible(true);

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
    
                Label dateLabel = new Label(modifiedTime);
                Label caption = new Label(Users.getCaption(Users.currentUser, Users.currentAlbum, photoPath));
    
                Image image = new Image(file.toURI().toString(), 100, 100, true, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setOnMouseClicked(e -> {
                    Users.currentPhoto = photoPath;
                    loadInOptions(e);
                });
    
                VBox slide = new VBox(5, imageView, dateLabel, caption);
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
                }

                loadingPane.setVisible(false);
                slideContainer.setVisible(true);
            });
        }).start();
    }
    
    

    public void leaveAlbum(ActionEvent e){
        //we have to calculate oldest date and newest date in here
        photos.model.Album album = photos.model.Users.userAlbums
        .get(photos.model.Users.currentUser)
        .get(photos.model.Users.currentAlbum);

        if (album == null) return;

        Set<photos.model.Photo> albumPhotos = album.getPhotos();

        if (albumPhotos == null || albumPhotos.isEmpty()) {
            System.out.println("No photos in album — skipping date calculations.");
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

        System.out.println("Checking for realDates in album: " + Users.currentAlbum);
        for (Photo p : albumPhotos) {
            System.out.println("Photo: " + p.getPath());
            for (Calendar c : p.getRealDates()) {
                System.out.println(" - RealDate: " + c.getTime());
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
            stage.getScene().setRoot(root);
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
            //Here is where we'll insert our new code for object reuse.
            Photo existingPhoto = null;
            String photoName = photoPath.substring(photoPath.lastIndexOf("/")+1);
            //We'll look through all of userAlbums and see if the final portion of the path matches the final portion of some earlier saved path
            for(Map<String, photos.model.Album> albums: photos.model.Users.userAlbums.values()){
                for(photos.model.Album a: albums.values()){
                    for(Photo p: a.getPhotos()){
                        if(p.getPath().substring(p.getPath().lastIndexOf("/")+1).equals(photoName)){
                            existingPhoto = p;
                            break;
                        }
                    }
                    if (existingPhoto != null) break;
                }
                if (existingPhoto != null) break;
            }
            //then we'll add that pre-existing photo object to this new album (note that photo duplication within an album is already banned, and should be banned)
            if(existingPhoto != null){
                photos.model.Users.addPhoto(photos.model.Users.currentUser, photos.model.Users.currentAlbum, existingPhoto.getPath());
                photos.model.Users.saveUserAlbums();
                //Then in here, we'll immediately add the photo and whatever the caption is below the photo
                //We already have the correct imageView element, so we're good there. We'll make a new VBox here
                String date = existingPhoto.getDates().iterator().next();
                Label caption = new Label(existingPhoto.getCaption());
                Label datey = new Label(date);
                VBox slide = new VBox(5, imageView, datey, caption);
                //Then we'll add it to slides
                slide.setStyle("-fx-alignment: center;");
                imageView.setOnMouseClicked(ev -> {
                    photos.model.Users.currentPhoto = photoPath;
                    loadInOptions(ev);
                });
                slides.add(slide);
                currentIndex = slides.size() - 1; // Show the new image
                slideContainer.getChildren().setAll(slide);
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
            System.out.println("Calling addRealDate for: " + photoPath + " with " + trueTime);
            photos.model.Users.addDate(photos.model.Users.currentUser, photos.model.Users.currentAlbum, photoPath, modifiedTime);
            photos.model.Users.saveUserAlbums();
            Label dateLabel = new Label(modifiedTime);
            VBox slide = new VBox(5, imageView, dateLabel);
            slide.setStyle("-fx-alignment: center;");
            imageView.setOnMouseClicked(ev -> {
                photos.model.Users.currentPhoto = photoPath;
                loadInOptions(ev);
            });
            slides.add(slide);
            currentIndex = slides.size() - 1; // Show the new image
            slideContainer.getChildren().setAll(slide);
            photos.model.Users.addPhoto(photos.model.Users.currentUser, photos.model.Users.currentAlbum, path);
            photos.model.Users.addRealDate(photos.model.Users.currentUser, photos.model.Users.currentAlbum, photoPath, trueTime);
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
            stage.getScene().setRoot(root);
            //For stage.setTitle(), we'll make sure to properly if-condition this one and only this one later.
            stage.setTitle("Dummy");
            stage.show();
        }
        catch (IOException ex) {
            ex.printStackTrace(); // Optional: replace with GUI error dialog
        }
    }

    @FXML
    private void handleNext(ActionEvent e) {
        if (currentIndex < slides.size() - 1) {
            currentIndex++;
            slideContainer.getChildren().setAll(slides.get(currentIndex));
        }
    }

    @FXML
    private void handlePrev(ActionEvent e) {
        if (currentIndex > 0) {
            currentIndex--;
            slideContainer.getChildren().setAll(slides.get(currentIndex));
        }
    }

    private void loadBulkView(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Bulk.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Bulk View");
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    

}
