package photos.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.File;

public class StockController {

    @FXML
    private HBox imageContainer;
    @FXML Button quitButton;

    @FXML
    public void initialize() {
        for (int i = 1; i <= 5; i++) {
            File imageFile = new File("data/stock" + i + ".jpg");
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(150);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(true);

                imageContainer.getChildren().add(imageView);
            } else {
                System.out.println("Image file not found: " + imageFile.getAbsolutePath());
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
}

