package photos.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
/**
 * Controller for the stock album view (Stock.fxml)
 * Displays predefined stock photos.
 * Provides navigation back to the main stock page and supports
 * quitting the application.
 * Authored by Harshi Oleti.
 */
public class StockController {
    /**
     * HBox container containing five stock photos
     * placed next to each other.
     */
    @FXML
    private HBox imageContainer;
    /**
     * Button that saves user data and quits application.
     */
    @FXML Button quitButton;
    /**
     * Button that loads back in TrueStockPage.fxml
     */
    @FXML Button returnButton;
    /**
     * Initializes the stock photo view by loading and displaying
     * stock images stored in the data directory.
     */
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
            }
        }
    }
    /**
     * Navigates to TrueStockPage.fxml that displays the stock album option button
     * @param e is the triggering event from the returnButton button
     */
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Page");
            alert.setContentText("Something went wrong while loading the page.\nDetails: " + ex.getMessage());
            alert.showAndWait();
        }
    }
    /**
     * saves user data to disk and quits application.
     * @param e is the triggering event from the quitButton button
     */
    public void quitApplication(ActionEvent e){
        photos.model.Users.saveUsersList();
        photos.model.Users.saveUserAlbums();
        photos.model.Users.saveUserTagTypes();
        Platform.exit();
        System.exit(0);
    }
}

