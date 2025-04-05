package photos.controller;
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
public class LogoutController {
    @FXML Button logOut;

    public void logOut(ActionEvent e){
        try{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Login.fxml"));
			Parent root = loader.load();
			Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
			stage.getScene().setRoot(root);

			stage.setTitle("Album");
			stage.show();
		}
		catch (IOException ex) {
            ex.printStackTrace(); // Optional: replace with GUI error dialog
        }
    }
}
