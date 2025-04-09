package photos.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
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
    //we'll have to include one more hash map here for storing a set of tagtype strings that were added in by the user
    //we'll probably have to make another .ser file lol, cus I'm not refactoring the code again
    public static ObservableList<String> photoPaths = FXCollections.observableArrayList();
    public static Map<String, Map<String, Album>> userAlbums = new HashMap<>();
    static{
        initializeUserAlbums();
    }
    public static Map<String, Set<String>> userTagTypes = new HashMap<>();
    public static void initializeUserTagTypes(){
        for (String username : usersList) {
            userTagTypes.putIfAbsent(username, new HashSet<>());
        }
    }

    public static void saveUserTagTypes() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("userTagTypes.ser"))) {
            out.writeObject(userTagTypes);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Page");
            alert.setContentText("Something went wrong while loading the page.\nDetails: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadUserTagTypes() {
        File file = new File("userTagTypes.ser");
        if (!file.exists()) return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            userTagTypes = (Map<String, Set<String>>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Page");
            alert.setContentText("Something went wrong while loading the page.\nDetails: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public static void addUserTagType(String username, String tagType){
        if (username == null || tagType == null || tagType.isBlank()) return;

        userTagTypes.computeIfAbsent(username, k -> new HashSet<>())
                .add(tagType.toLowerCase());
    }


    public static void saveUsersList() {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("usersList.ser"))) {
			out.writeObject(new ArrayList<>(usersList)); // Convert ObservableList to ArrayList
		} catch (IOException e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Page");
            alert.setContentText("Something went wrong while loading the page.\nDetails: " + e.getMessage());
            alert.showAndWait();
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
			Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Page");
            alert.setContentText("Something went wrong while loading the page.\nDetails: " + e.getMessage());
            alert.showAndWait();
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
                  .putIfAbsent(albumName, new Album());
    }

    public static void addExistingPhoto(String username, String albumName, Photo photo) {
        Album album = userAlbums.get(username).get(albumName);
        if (album != null && !album.getPhotos().contains(photo)) {
            album.getPhotos().add(photo);
        }
    }
    

    public static void addPhoto(String username, String albumName, String photoPath) {
        Album album = userAlbums.get(username).get(albumName);
        if (album == null) return;

        Set<Photo> photos = album.getPhotos();
        if (photos == null) return;

        Photo existing = findPhotoByPath(photoPath);
        Photo toAdd;

        if (existing != null && username.equals(existing.getOwner())) {
            toAdd = existing; // reuse only if same user
        } else {
            toAdd = new Photo(photoPath);
            toAdd.setOwner(username); // set owner for new photo
        }

        if (photos.contains(toAdd)) return;
        photos.add(toAdd);
        System.out.println("Adding photo to album: " + toAdd.getPath());
        System.out.println("Photo owner: " + toAdd.getOwner());
        System.out.println("Album contains photo: " + photos.contains(toAdd));
        System.out.println("Album size after add: " + photos.size());

    }
    

    public static Photo findPhotoInUserAlbums(String username, String photoPath) {
        Map<String, Album> albums = userAlbums.get(username);
        if (albums == null) return null;
        for (Album album : albums.values()) {
            for (Photo photo : album.getPhotos()) {
                if (photo.getPath().equals(photoPath)) {
                    return photo;
                }
            }
        }
        return null;
    }
    

    public static Photo findPhotoByPath(String path) {
        for (Map<String, Album> albums : userAlbums.values()) {
            for (Album album : albums.values()) {
                for (Photo photo : album.getPhotos()) {
                    if (photo.getPath().equals(path)) {
                        return photo;
                    }
                }
            }
        }
        return null;
    }
    

    public static void addDate(String username, String albumName, String photoPath, String date){
        Album album = userAlbums.get(username).get(albumName);
        if (album == null) return;
        Set<Photo> photos = album.getPhotos();

        if (photos == null) return;
    
        for (Photo p : photos) {
            if (p.getPath().equals(photoPath)) {
                p.addDate(date);
                break;
            }
        }
    }

    public static void addRealDate(String username, String albumName, String photoPath, String dateStr) {
        Album album = userAlbums.get(username).get(albumName);
        if (album == null){
            System.out.println("Album is empty?");
            return;
        } 
        Set<Photo> photos = album.getPhotos();
        if (photos == null) {
            System.out.println("Photo set is empty?");
            return;
        }
        if(photos.isEmpty()){
            System.out.println("Real but empty?");
            return;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsedDate = sdf.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsedDate);
            for (Photo p : photos) {
                System.out.println("Comparing: " + p.getPath() + " == " + photoPath);
                System.out.println("Looking for photo path match...");
                System.out.println(" → p.getPath(): " + p.getPath());
                System.out.println(" → photoPath : " + photoPath);

                if (p.getPath().equals(photoPath)) {
                    System.out.println("Match found! Adding calendar.");
                    p.addRealDate(calendar); // ✅ now using the Calendar-based method
                    break;
                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Page");
            alert.setContentText("Something went wrong while loading the page.\nDetails: " + e.getMessage());
            alert.showAndWait();
        }
    }


    public static void addTag(String username, String albumName, String photoPath, String tag){
        Album album = userAlbums.get(username).get(albumName);
        if (album == null) return;
        Set<Photo> photos = album.getPhotos();

        if (photos == null) return;

        for(Photo p : photos){
            if(p.getPath().equals(photoPath)){
                p.addTag(tag);
                break;
            }
        }
    }

    public static void removeTag(String username, String albumName, String photoPath, String tag){
        Album album = userAlbums.get(username).get(albumName);
        if (album == null) return;
        Set<Photo> photos = album.getPhotos();

        if (photos == null) return;

        for(Photo p : photos){
            if(p.getPath().equals(photoPath)){
                Set<String> tags = p.getTags();
                if(tags == null) return;

                for(String t: tags){
                    if(t.equals(tag)){
                        p.removeTag(tag);
                        break;
                    }
                }
                break;
            }
        }
    }

    public static void addCaption(String username, String albumName, String photoPath, String caption){
        Album album = userAlbums.get(username).get(albumName);
        if (album == null) return;
        Set<Photo> photos = album.getPhotos();

        if (photos == null) return;

        for(Photo p : photos){
            if(p.getPath().equals(photoPath)){
                p.setCaption(caption);
                break;
            }
        }
    }

    public static String getCaption(String username, String albumName, String photoPath){
        Album album = userAlbums.get(username).get(albumName);
        if (album == null) return "";
        Set<Photo> photos = album.getPhotos();

        if (photos == null) return "";

        for(Photo p : photos){
            if(p.getPath().equals(photoPath)){
                return p.getCaption();
            }
        }
        return "";
    }

    public static void removeCaption(String username, String albumName, String photoPath){
        Album album = userAlbums.get(username).get(albumName);
        if (album == null) return;
        Set<Photo> photos = album.getPhotos();

        if (photos == null) return;

        for(Photo p : photos){
            if(p.getPath().equals(photoPath)){
                p.removeCaption();
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
    
        Map<String, Album> albums = userAlbums.get(username);
        if (!albums.containsKey(albumName)) {
            System.out.println("Album '" + albumName + "' does not exist for user '" + username + "'.");
            return;
        }
    
        albums.remove(albumName);
        System.out.println("Album '" + albumName + "' removed for user '" + username + "'.");
    }
    
    public static void removePhoto(String username, String albumName, String photoPath) {
        Album album = userAlbums.get(username).get(albumName);
        if (album == null) return;
        Set<Photo> photos = album.getPhotos();
        if (photos == null) return;

        photos.remove(new Photo(photoPath));
    }

    public static void saveUserAlbums() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("userAlbums.ser"))) {
            out.writeObject(userAlbums);
            System.out.println("userAlbums saved successfully.");
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Page");
            alert.setContentText("Something went wrong while loading the page.\nDetails: " + e.getMessage());
            alert.showAndWait();
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
            userAlbums = (Map<String, Map<String, Album>>) in.readObject();
            System.out.println("userAlbums loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Page");
            alert.setContentText("Something went wrong while loading the page.\nDetails: " + e.getMessage());
            alert.showAndWait();
            userAlbums = new HashMap<>();
        }
    }

    
}
