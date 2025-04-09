package photos.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.collections.*;
import java.io.*;
//import javafx.scene.control.ListView; this import probably won't be used in this class but I'm paranoid
import java.util.ArrayList;

//We're going to define the actions of all the buttons present in the main window
//Please note that each window that is not part of the main window needs its own controller class

//A few days from now I'm gonna update the Admin Page to have a separate Return to Login Page button and remove the logic that loads the login page from the createUser and deleteUser methods
//I don't care about the other pages having a Return to Login Page button yet, because only the Admin and Login pages are near-complete at this stage
//Also tomorrow I'm gonna start and hopefully complete work on the "stock" page
/**
 * Controller for the login, admin, and stock pages of the Photos application.
 * Handles user login routing and admin user management, and navigation to stock and user pages.
 */
public class PhotosController {
	@FXML Button login;
	@FXML TextField userName;
	@FXML Button displayAllUsers; //will have function in here called displayAllUsers()
	@FXML Button createUser; //will have function in here called createUser()
	@FXML Button deleteUser; //will have function in here called deleteUser()
	@FXML TextField newUserName;
	@FXML TextField deadUserName;
	@FXML Button loadStockPage;
	@FXML Button quitButton;
	@FXML Button returnButton;

	/*We can use userName.getText() to retrieve the string typed in by the user.
	 * If it's "admin", we'll load the scene for that specific case, which is basically
	 * an XML page that stalks the users of the app. If it's "stock", it shows us a single
	 * stock album of 5 low-resolution photos. Otherwise, it compares the user-inputted
	 * string with all the strings in some global string list of users, 
	 * and if it's equivalent to one,
	 * then the scene for the regular application will be loaded. Otherwise,
	 * we'll probably load some failure page.
	 * We can take care of the admin and stock stuff today, and tackle the normal
	 * application in the coming weeks. For admin, we're probably gonna have to add
	 * a method in this class for creating and deleting users, which is essentially just
	 * adding and deleting their corresponding strings from usersList.
	*/

	//public static ObservableList<String> usersList = FXCollections.observableArrayList("admin", "stock");
	
	/*This is for loading pages directly from login*/
	/**
	 * Handles the login logic and routes users to to the appropriate interface
	 * (admin, stock, or normal home page) depending on input.
	 * @param e is the triggering event from the login button.
	 */
	public void goToApplication(ActionEvent e){
		try{
			//For each branch, we'll create different loader and root objects
			FXMLLoader loader;
			Parent root;
			boolean registeredUser = false;
			if(photos.model.Users.usersList.contains(userName.getText().trim())){
				registeredUser = true;
			}
			if(userName.getText().trim().equals("admin") && registeredUser){
				loader = new FXMLLoader(getClass().getResource("/photos/view/Admin.fxml"));
				root = loader.load();
			}
			else if(userName.getText().trim().equals("stock") && registeredUser){
				loader = new FXMLLoader(getClass().getResource("/photos/view/TrueStockPage.fxml"));
				root = loader.load();
			}
			else if(registeredUser){
				photos.model.Users.currentUser = userName.getText().trim();
				loader = new FXMLLoader(getClass().getResource("/photos/view/Bulk.fxml"));
				root = loader.load();
			}
			else{
				userName.setText("");
				return;
			}

			Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

			//For stage.setTitle(), we'll make sure to properly if-condition this one and only this one later.
            stage.setTitle("Home Page");
            stage.show();
		}
		catch(IOException d){
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Failed to Load Page");
			alert.setContentText("Something went wrong while loading the page.\nDetails: " + d.getMessage());
			alert.showAndWait();
		}
	}
	/**
	 * Saves all user data and exits the application cleanly.
	 * @param e is the triggering event from the quit application button.
	 */
	public void quitApplication(ActionEvent e){
        photos.model.Users.saveUsersList();
        photos.model.Users.saveUserAlbums();
        photos.model.Users.saveUserTagTypes();
        Platform.exit();
        System.exit(0);
    }
	/**
	 * Returns from the current view to the login page.
	 * @param e is the triggering event.
	 */
	public void returnLog(ActionEvent e){
		try{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Login.fxml"));
			Parent root = loader.load();
			Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
			stage.getScene().setRoot(root);

			stage.setTitle("Login Page");
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

	/*This is for displaying all the users in the admin page */
	/**
	 * Displays all users in a modal ListView via the Display.fxml
	 * view. It is only accessible to the admin user.
	 * @param e is the triggering event from the displayAllUsers button
	 */
	public void displayAllUsers(ActionEvent e){
		try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Display.fxml"));
            Parent listRoot = loader.load();

            // Get the controller for Display.fxml
            ListViewController controller = loader.getController();

            // Pass in the users list (assumed to be initialized)
            controller.setUsersList(photos.model.Users.usersList);

			Stage parentStage = (Stage) ((Node) e.getSource()).getScene().getWindow();

            // Create and show a new stage
            Stage listStage = new Stage();
			listStage.initOwner(parentStage);                      // 👈 this connects the two windows
        	listStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            listStage.setScene(new Scene(listRoot));
            listStage.setTitle("All Users");
            listStage.showAndWait();

        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Failed to Load Page");
			alert.setContentText("Something went wrong while loading the page.\nDetails: " + ex.getMessage());
			alert.showAndWait();
        }
	}
	/*This is so that the admin user can create new users*/
	/**
	 * Creates a new user with the name entered in the associated text field.
	 * Only allowed for admin user. Ignores blank or duplicate names.
	 * @param e is the triggering event from the createUser button
	 */
	public void createUser(ActionEvent e){
		String newUser = newUserName.getText().trim();
		if (!newUser.isEmpty() && !photos.model.Users.usersList.contains(newUser)) {
			photos.model.Users.addUser(newUser);
			photos.model.Users.saveUsersList();
			photos.model.Users.saveUserAlbums();
		}
		newUserName.setText("");
		deadUserName.setText("");
	}
	/**
	 * Deletes the user specified in the associated text field.
	 * Only allowed for admin user. Ignores blank or invalid names.
	 * @param event is the triggering event from the deleteUser button.
	 */
	public void deleteUser(ActionEvent event) {
		String userToDelete = deadUserName.getText().trim();
		if (!userToDelete.isEmpty() && photos.model.Users.usersList.contains(userToDelete)) {
			photos.model.Users.removeUser(userToDelete);
			photos.model.Users.saveUsersList();
			photos.model.Users.saveUserAlbums();
		}
		newUserName.setText("");
		deadUserName.setText("");
	}

	//Load stock page
	/**
	 * It loads the stock album page associated with the Stock.fxml view.
	 * Should be loaded from TrueStockPage.fxml
	 * @param event is the triggering event from the loadStockPage button
	 */
	public void loadStockPage(ActionEvent event){
		try{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Stock.fxml"));
			Parent root = loader.load();
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.getScene().setRoot(root);

			stage.setTitle("Stock Album");
			stage.show();
		}
		catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Failed to Load Page");
			alert.setContentText("Something went wrong while loading the page.\nDetails: " + ex.getMessage());
			alert.showAndWait();// Optional: replace with GUI error dialog
        }
	}
}
