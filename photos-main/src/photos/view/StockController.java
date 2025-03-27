package photos.view;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.File;

public class StockController {

    @FXML
    private HBox imageContainer;

    @FXML
    public void initialize() {
        for (int i = 1; i <= 5; i++) { //extended path info
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
}

