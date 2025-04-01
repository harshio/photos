package photos.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


//If we were to write something like System.out.println(usersList); in here, we would get the full contents of usersList as stored in its .ser file. We wouldn't have to write models.Users.usersList or whatever. So, when we're rewriting usersList as an array in a method, it'd be easier to do it in here.

public class Users {
    public static ObservableList<String> usersList = FXCollections.observableArrayList("admin", "stock");
    public static ObservableList<String> albumNames = FXCollections.observableArrayList();
    public static String currentAlbum = null;
    public static String currentPhoto = null;
    public static String currentUser = null;
    public static ObservableList<String> photoPaths = FXCollections.observableArrayList();
    public static Map<String, Map<String, Set<Photo>>> userAlbums = new HashMap<>();
    static{
        initializeUserAlbums();
    }
    //Well lookee here, we have all the stuff we need to create and eventually serialize that nested hashmap right here


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
//making our hashmap (first stage: making sure albums don't all get the same photos, second stage: serialization)
    public static void initializeUserAlbums() {
        userAlbums.clear();
        for (String user : usersList) {
            userAlbums.putIfAbsent(user, new HashMap<>()); // no albums yet
        }
    }

    public static void createAlbum(String username, String albumName) {
        userAlbums.computeIfAbsent(username, k -> new HashMap<>())
                  .putIfAbsent(albumName, new HashSet<>());
    }

    public static void addPhoto(String username, String albumName, String photoPath) {
        Set<Photo> photos = userAlbums.get(username).get(albumName);
        if (photos == null) return;

        photos.add(new Photo(photoPath));
    }

    public static void addDate(String username, String albumName, String photoPath, String date){
        Set<Photo> photos = userAlbums.get(username).get(albumName);
        if (photos == null) return;
    
        for (Photo p : photos) {
            if (p.getPath().equals(photoPath)) {
                p.addDate(date);
                break;
            }
        }
    }

    public static void addUser(String username) {
        if (!usersList.contains(username)) {
            usersList.add(username); 
            userAlbums.put(username, new HashMap<>());
        }
    }    

    public static void removeUser(String username) {
        if (usersList.contains(username)) {
            usersList.remove(username);
            userAlbums.remove(username);
            System.out.println("User '" + username + "' removed successfully.");
        } else {
            System.out.println("User '" + username + "' does not exist.");
        }
    }
    
    public static void removeAlbum(String username, String albumName) {
        if (!userAlbums.containsKey(username)) {
            System.out.println("User '" + username + "' does not exist.");
            return;
        }
    
        Map<String, Set<Photo>> albums = userAlbums.get(username);
        if (!albums.containsKey(albumName)) {
            System.out.println("Album '" + albumName + "' does not exist for user '" + username + "'.");
            return;
        }
    
        albums.remove(albumName);
        System.out.println("Album '" + albumName + "' removed for user '" + username + "'.");
    }
    
    public static void removePhoto(String username, String albumName, String photoPath) {
        Set<Photo> photos = userAlbums.get(username).get(albumName);
        if (photos == null) return;

        photos.remove(new Photo(photoPath));
    }

    public static void saveUserAlbums() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("userAlbums.ser"))) {
            out.writeObject(userAlbums);
            System.out.println("userAlbums saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving userAlbums.");
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadUserAlbums() {
        File file = new File("userAlbums.ser");
        if (!file.exists()) {
            userAlbums = new HashMap<>();
            System.out.println("No saved userAlbums found. Starting fresh.");
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            userAlbums = (Map<String, Map<String, Set<Photo>>>) in.readObject();
            System.out.println("userAlbums loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            userAlbums = new HashMap<>();
            System.out.println("Error loading userAlbums. Starting with an empty structure.");
        }
    }

    
}
