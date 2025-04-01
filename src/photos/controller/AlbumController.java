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
import photos.model.Users;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.nio.file.Files;
//import javafx.scene.control.ListView; this import probably won't be used in this class but I'm paranoid
import java.util.ArrayList;
import java.util.Set;
public class AlbumController {
    @FXML Button leave;
    @FXML Button add;
    @FXML Button delete;
    @FXML
    private VBox photoList;
    @FXML
    private StackPane slideContainer;
    @FXML
    private Button prevButton, nextButton;

    private ArrayList<VBox> slides = new ArrayList<>();
    private int currentIndex = 0;


    public void initialize(){
        Set<photos.model.Photo> albumPhotos = photos.model.Users.userAlbums
            .get(Users.currentUser)
            .get(Users.currentAlbum);

        if (albumPhotos == null) return;
        slides.clear();

        Platform.runLater(() -> {
            for (photos.model.Photo photo : albumPhotos) {
                final String photoPath = photo.getPath(); // capture correctly
                Image image = new Image(new File(photoPath).toURI().toString(), 100, 100, true, true, true);
                ImageView imageView = new ImageView(image);

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
                photos.model.Users.addDate(photos.model.Users.currentUser, photos.model.Users.currentAlbum, photoPath, modifiedTime);
                photos.model.Users.saveUserAlbums();
                Label dateLabel = new Label(modifiedTime);

                // Wrap in VBox
                VBox slide = new VBox(5, imageView, dateLabel);
                slide.setStyle("-fx-alignment: center;");
            
                imageView.setOnMouseClicked(e -> {
                    photos.model.Users.currentPhoto = photoPath;
                    loadInOptions(e);
                });
            
                slides.add(slide);
            }

            if (!slides.isEmpty()) {
                slideContainer.getChildren().setAll(slides.get(currentIndex));
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
            String modifiedTime;
            try {
                modifiedTime = "Date taken: " +
                    Files.getLastModifiedTime(new File(photoPath).toPath()).toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (IOException ex) {
                modifiedTime = "Date taken: (unavailable)";
            }
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


}
