package photos.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Users {
    public static ObservableList<String> usersList = FXCollections.observableArrayList("admin", "stock");

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
		if (!file.exists()) return; // Don't load if file doesn't exist, which it shouldn't at the beginning of the first starting of the application

		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
			ArrayList<String> list = (ArrayList<String>) in.readObject();
			usersList.setAll(list); // Update ObservableList with loaded data
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
