package photos.view;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.collections.*;
import java.io.*;
//import javafx.scene.control.ListView; this import probably won't be used in this class but I'm paranoid
import java.util.ArrayList;

//We're going to define the actions of all the buttons present in the main window
//Please note that each window that is not part of the main window needs its own controller class

//A few days from now I'm gonna update the Admin Page to have a separate Return to Login Page button and remove the logic that loads the login page from the createUser and deleteUser methods
//I don't care about the other pages having a Return to Login Page button yet, because only the Admin and Login pages are near-complete at this stage
//Also tomorrow I'm gonna start and hopefully complete work on the "stock" page

public class PhotosController {
	@FXML Button login;
	@FXML TextField userName;
	@FXML Button displayAllUsers; //will have function in here called displayAllUsers()
	@FXML Button createUser; //will have function in here called createUser()
	@FXML Button deleteUser; //will have function in here called deleteUser()
	@FXML TextField newUserName;
	@FXML TextField deadUserName;
	@FXML Button loadStockPage;

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

	public static ObservableList<String> usersList = FXCollections.observableArrayList("admin", "stock");
	
	/*This is for loading pages directly from login*/
	public void goToApplication(ActionEvent e){
		try{
			//For each branch, we'll create different loader and root objects
			FXMLLoader loader;
			Parent root;
			boolean registeredUser = false;
			if(usersList.contains(userName.getText())){
				registeredUser = true;
			}
			if(userName.getText().equals("admin") && registeredUser){
				loader = new FXMLLoader(getClass().getResource("/photos/view/Admin.fxml"));
				root = loader.load();
			}
			//so far it's just pictures, need to learn how to place the photos in a clickable album of sorts
			else if(userName.getText().equals("stock") && registeredUser){
				loader = new FXMLLoader(getClass().getResource("/photos/view/TrueStockPage.fxml"));
				root = loader.load();
			}
			else if(registeredUser){
				loader = new FXMLLoader(getClass().getResource("/photos/view/Bulk.fxml"));
				root = loader.load();
			}
			else{
				loader = new FXMLLoader(getClass().getResource("/photos/view/Stupid.fxml"));
				root = loader.load();
			}

			Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
			//For stage.setTitle(), we'll make sure to properly if-condition this one and only this one later.
            stage.setTitle("Dummy");
            stage.show();
		}
		catch(IOException d){
			//Note that eventually we have to make it so that errors display in the GUI and not in the console
			d.printStackTrace();
		}
	}

	/*This is for displaying all the users in the admin page */
	public void displayAllUsers(ActionEvent e){
		try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Display.fxml"));
            Parent listRoot = loader.load();

            // Get the controller for Display.fxml
            ListViewController controller = loader.getController();

            // Pass in the users list (assumed to be initialized)
            controller.setUsersList(usersList);

            // Create and show a new stage
            Stage listStage = new Stage();
            listStage.setScene(new Scene(listRoot));
            listStage.setTitle("All Users");
            listStage.show();

        } catch (IOException ex) {
            ex.printStackTrace(); // Optional: replace with GUI error dialog
        }
	}
	/*This is so that the admin user can create new users*/
	public void createUser(ActionEvent e){
		String newUser = newUserName.getText();
		if (!newUser.isEmpty() && !usersList.contains(newUser)) {
			usersList.add(newUser);
			saveUsersList();
		}
		try{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Login.fxml"));
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
	
	/*This is so that the admin user can delete users */
	public void deleteUser(ActionEvent event) {
		String userToDelete = deadUserName.getText();
		if (!userToDelete.isEmpty() && usersList.contains(userToDelete)) {
			usersList.remove(userToDelete);
			saveUsersList();
		}
		try{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Login.fxml"));
			Parent root = loader.load();
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(root));
			//For stage.setTitle(), we'll make sure to properly if-condition this one and only this one later.
			stage.setTitle("Dummy");
			stage.show();
		}
		catch (IOException ex) {
            ex.printStackTrace(); // Optional: replace with GUI error dialog
        }
	}
	
	public static void saveUsersList() {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("usersList.ser"))) {
			out.writeObject(new ArrayList<>(usersList)); // Convert ObservableList to ArrayList
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

// Load usersList from disk
	public static void loadUsersList() {
		File file = new File("usersList.ser");
		if (!file.exists()) return; // Don't load if file doesn't exist

		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
			ArrayList<String> list = (ArrayList<String>) in.readObject();
			usersList.setAll(list); // Update ObservableList with loaded data
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	//Load stock page
	public void loadStockPage(ActionEvent event){
		try{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Stock.fxml"));
			Parent root = loader.load();
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(root));
			stage.setTitle("Album");
			stage.show();
		}
		catch (IOException ex) {
            ex.printStackTrace(); // Optional: replace with GUI error dialog
        }
	}
}
