package photos.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
/**
 * This is where the photo application begins.
 * It initializes the users' data and launches the application.
 * @author Chloe Wolohan
 */
public class Photos extends Application {
	/**
	 * This is where the application starts.
	 * It loads the users' data from the disk and initializes it.
	 * @param primaryStage is the primary window for the JavaFX application.
	 * @throws Exception if loading of either data from the disk or from the fxml files fails
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
        photos.model.Users.loadUsersList(); //seems our app literally starts by reading from the disk
        photos.model.Users.loadUserAlbums();
		photos.model.Users.loadUserTagTypes();
		photos.model.Users.initializeUserTagTypes();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/photos/view/Login.fxml"));
		GridPane root = (GridPane)loader.load();
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Login Page");
 		primaryStage.show();
	}
	/**
	 * Application is launched here
	 * @param args are unused command lines
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
