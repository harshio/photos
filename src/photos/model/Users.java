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
/**
 * Central model class for managing all user-related data, including
 * users list, albums and photos for each user, and user-defined tag types,
 * which are all serialized and deserialized here, for the purpose of data persistence.
 * @author Harshi Oleti
 */
public class Users {
    /**
     * This is the list of all registered usernames in the system.
     * It is observable and used for dynamically updating UI components.
     */
    public static ObservableList<String> usersList = FXCollections.observableArrayList("admin", "stock");
    /**
     * The name of the album currently being viewed or edited.
     * Used to track user navigation state.
     */
    public static String currentAlbum = null;
    /**
     * The path of the photo currently being viewed or selected.
     * Useful for actions like tagging or captioning.
     */
    public static String currentPhoto = null;
    /**
     * The username of the user currently logged into the system.
     */
    public static String currentUser = null;
    /**
     * A mapping of usernames to their corresponding albums.
     * Each user maps to another map, where the album name maps to an Album object.
     */
    public static Map<String, Map<String, Album>> userAlbums = new HashMap<>();
    static{
        initializeUserAlbums();
    }
    /**
     * A mapping of usernames to their custom-defined tag types.
     * Helps enforce tag type constraints for each individual user.
     */
    public static Map<String, Set<TagType>> userTagTypes = new HashMap<>();
    /**
     * Initializes the tag types map for each user.
     */
    public static void initializeUserTagTypes(){
        for (String username : usersList) {
            userTagTypes.putIfAbsent(username, new HashSet<>());
        }
    }
    /**
     * saves custom tag types for all users to disk
     */
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
    /**
     * Loads user tag types from disk.
     */
    @SuppressWarnings("unchecked")
    public static void loadUserTagTypes() {
        File file = new File("userTagTypes.ser");
        if (!file.exists()) return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            userTagTypes = (Map<String, Set<TagType>>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Page");
            alert.setContentText("Something went wrong while loading the page.\nDetails: " + e.getMessage());
            alert.showAndWait();
        }
    }
    /**
     * adds a custom tag type for a specific user
     * @param username the user to add the tag type for
     * @param tagTypeName the tag type to add
     */
    public static void addUserTagType(String username, String tagTypeName, boolean restricted){
        userTagTypes.computeIfAbsent(username, k -> new HashSet<>());
        TagType newType = new TagType(tagTypeName, restricted);
        userTagTypes.get(username).add(newType);
    }

    /**
     * Saves the users list to disk.
     */
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

    /**
     * Method to check if a tag type is restricted for a certain username
     * @param username username of user whose tags we're checking for restrictions
     * @param tagTypeName tag type we're checking to see if restricted
     * @return whether or not tag type is restricted
     */
    public static boolean isRestrictedTagType(String username, String tagTypeName) {
        Set<TagType> types = userTagTypes.get(username);
        if (types == null) return false;
        for (TagType t : types) {
            if (t.getName().equalsIgnoreCase(tagTypeName)) {
                return t.isRestricted();
            }
        }
        return false; // default if type is not defined
    }
    

    /**
     * Loads the usersList from disk
     */
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
    /**
     * Initializes user albums with empty album maps for each user.
     */
    public static void initializeUserAlbums() {
        userAlbums.clear();
        for (String user : usersList) {
            userAlbums.putIfAbsent(user, new HashMap<>()); // no albums yet
        }
    }
    /**
     * Creates a new album for a user if it doesn't already exist
     * @param username the user
     * @param albumName the album name
     */
    public static void createAlbum(String username, String albumName) {
        userAlbums.computeIfAbsent(username, k -> new HashMap<>())
                  .putIfAbsent(albumName, new Album());
    }
    /**
     * adds a reference to an existing Photo object to another album
     * @param username the user
     * @param albumName the album
     * @param photo the existing Photo object
     */
    public static void addExistingPhoto(String username, String albumName, Photo photo) {
        Album album = userAlbums.get(username).get(albumName);
        if (album != null && !album.getPhotos().contains(photo)) {
            album.getPhotos().add(photo);
        }
    }
    
    /**
     * adds a new photo to the album using the file path.
     * Reuses an existing photo if it exists and belongs to the same user.
     * @param username the user
     * @param albumName the album
     * @param photoPath the file path of the photo
     */
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

    }
    
    /**
     * Finds a photo in a specific user's albums by path
     * @param username the user
     * @param photoPath the photo path
     * @return the Photo object, or null if not found
     */
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
    
    /**
     * Finds a photo in all albums belonging to all users by path
     * @param path the file path of the photo
     * @return the Photo object, or null if not found
     */
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
    
    /**
     * adds a user-facing display date to a photo
     * @param username the user
     * @param albumName the album
     * @param photoPath the photo path
     * @param date the date string to add
     */
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
    /**
     * Adds a real calendar date for searching/sorting to a photo
     * @param username the user
     * @param albumName the album
     * @param photoPath the photo path
     * @param dateStr the date string in "yyyy-MM-dd HH:mm:ss" format
     */
    public static void addRealDate(String username, String albumName, String photoPath, String dateStr) {
        Album album = userAlbums.get(username).get(albumName);
        if (album == null){
            return;
        } 
        Set<Photo> photos = album.getPhotos();
        if (photos == null) {
            return;
        }
        if(photos.isEmpty()){
            return;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsedDate = sdf.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsedDate);
            for (Photo p : photos) {

                if (p.getPath().equals(photoPath)) {
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

    /**
     * Adds a tag to a photo
     * @param username the user
     * @param albumName the album
     * @param photoPath the photo path
     * @param tag the tag to add
     */
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
    /**
     * removes a tag from a photo
     * @param username the user
     * @param albumName the album
     * @param photoPath the photo path
     * @param tag the tag to remove
     */
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
    /**
     * adds a caption to a photo.
     * @param username the user
     * @param albumName the album
     * @param photoPath the photo path
     * @param caption the caption text
     */
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
    /**
     * Retrieves a caption from a photo
     * @param username the user
     * @param albumName the album
     * @param photoPath the photo path
     * @return the caption string, or empty if none
     */
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
    /**
     * Removes the caption from a photo
     * @param username the user
     * @param albumName the album
     * @param photoPath the photo path
     */
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
    /**
     * Adds a new user to the system.
     * @param username the new user's name
     */
    public static void addUser(String username) {
        if (!usersList.contains(username)) {
            usersList.add(username); 
            userAlbums.put(username, new HashMap<>());
        }
    }    
    /**
     * Removes a user and their albums from the system.
     * @param username the user to remove
     */
    public static void removeUser(String username) {
        if (usersList.contains(username)) {
            usersList.remove(username);
            userAlbums.remove(username);
        }
    }
    /**
     * removes a specific album from a user's collection
     * @param username the user
     * @param albumName the album to remove
     */
    public static void removeAlbum(String username, String albumName) {
        if (!userAlbums.containsKey(username)) {
            return;
        }
    
        Map<String, Album> albums = userAlbums.get(username);
        if (!albums.containsKey(albumName)) {
            return;
        }
    
        albums.remove(albumName);
    }
    /**
     * Removes a photo from the specified album.
     * @param username the user
     * @param albumName the album
     * @param photoPath the path of the photo to remove
     */
    public static void removePhoto(String username, String albumName, String photoPath) {
        Album album = userAlbums.get(username).get(albumName);
        if (album == null) return;
        Set<Photo> photos = album.getPhotos();
        if (photos == null) return;

        photos.remove(new Photo(photoPath));
    }
    /**
     * saves all user albums to disk
     */
    public static void saveUserAlbums() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("userAlbums.ser"))) {
            out.writeObject(userAlbums);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Page");
            alert.setContentText("Something went wrong while loading the page.\nDetails: " + e.getMessage());
            alert.showAndWait();
        }
    }
    /**
     * Loads all user albums from disk
     */
    @SuppressWarnings("unchecked")
    public static void loadUserAlbums() {
        File file = new File("userAlbums.ser");
        if (!file.exists()) {
            userAlbums = new HashMap<>();
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            userAlbums = (Map<String, Map<String, Album>>) in.readObject();
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
