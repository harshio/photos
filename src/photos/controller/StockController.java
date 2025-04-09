package photos.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class StockController {

    @FXML
    private HBox imageContainer;
    @FXML Button quitButton;
    @FXML Button returnButton;

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

    public void loadInStockPage(ActionEvent e){
        try{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/TrueStockPage.fxml"));
			Parent root = loader.load();
			Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
			stage.getScene().setRoot(root);

			stage.setTitle("Home Page");
			stage.show();
		}
		catch (IOException ex) {
            ex.printStackTrace(); // Optional: replace with GUI error dialog
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

