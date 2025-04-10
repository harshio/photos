package photos.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a user's photo album.
 * Stores a collection of photos, as well as
 * the oldest and newest dates of the collection of photos
 * in the album.
 * Implements Serializable for persistent storage.
 * Authored by Chloe Wolohan.
 */
public class Album implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * The set of photos contained in this album.
     * Each photo is unique and stored in a HashSet for quick lookup.
     */
    private Set<Photo> photos;
    /**
     * The string representing the date of the oldest photo in this album.
     * Used for displaying photo range ingo.
     */
    private String oldestDate;
    /**
     * The string representing the date of the newest photo in this album.
     * Stored as a String but typically derived from Calendar for comparisons.
     */
    private String newestDate; //in the leaveAlbum method or whatever it's called in the AlbumController file, we'll include code that always calculates album info and then stores it in the hashmap and saves it
    //will use Calendar instances for the sake of comparison, but will convert back to String for storage purposes
    /**
     * Constructs a new empty Album with default values for the collection
     * of photos, the oldest date, and the newest data.
     */
    public Album() {
        this.photos = new HashSet<>();
        this.oldestDate = "Unknown";
        this.newestDate = "Unknown";
    }
    /**
     * returns the set of photos in this album.
     * @return the set of Photo objects
     */
    public Set<Photo> getPhotos() {
        return photos;
    }
    /**
     * Adds a photo to this album
     * @param photo the Photo to add
     */
    public void addPhoto(Photo photo) {
        photos.add(photo);
    }
    /**
     * Removes a photo from this album
     * @param photo the Photo to remove
     */
    public void removePhoto(Photo photo) {
        photos.remove(photo);
    }
    /**
     * Returns the string representing the oldest photo date in the album
     * @return the oldest date as a string
     */
    public String getOldestDate(){
        return oldestDate;
    }
    /**
     * Sets the string representing the oldest phone date in the album
     * @param oldestDate the oldest date to be set
     */
    public void setOldestDate(String oldestDate){
        this.oldestDate = oldestDate;
    }
    /**
     * Returns the string representing the newest photo date in the album.
     * @return the newest date as a string.
     */
    public String getNewestDate(){
        return newestDate;
    }
    /**
     * Sets the string representing the newest photo date in the album.
     * @param newestDate the newest date to be set
     */
    public void setNewestDate(String newestDate){
        this.newestDate = newestDate;
    }
    /**
     * Returns the number of photos in this album.
     * @return the album size.
     */
    public int getSize(){
        return photos.size();
    }
}
