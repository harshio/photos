package photos.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a photo object with path,
 * captions, tags and date of capture. Each
 * photo tracks its owner, display dates, and real dates (Calendar)
 * for the sake of comparison. 
 * Implements Serializable for data persistence.
 * Authored by Chloe Wolohan.
 */
public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * The file path of the photo.
     * Used as a unique identifier and reference to its storage location.
     */
    private String path;
    /**
     * The username of the person who owns the photo.
     */
    private String owner;
    /**
     * A set of human-readable date strings associated with the photo.
     * Typically formatted dates for UI display. 100% of the time, this
     * Set of String objects has 1 date string in it.
     */
    private Set<String> dates;
    /**
     * The set of tags attached to this photo.
     */
    private Set<String> tags;
    /**
     * The user-defined caption for the photo.
     */
    private String caption;
    /**
     * A set of real capture dates used for date-based comparisons.
     * Stored as Calendar objects.
     */
    private Set<Calendar> realDates; //storing dates we'll use for comparison
    /**
     * Constructs a new photo with the given file path.
     * Initializes the attributes listed above.
     * @param path the file path of the photo
     */
    public Photo(String path) {
        this.path = path;
        this.dates = new HashSet<>();
        this.tags = new HashSet<>();
        this.caption = "";
        this.realDates = new HashSet<>();
        this.owner = "";
    }
    /**
     * sets the owner of this photo.
     * @param owner the username of the owner.
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }
    /**
     * returns the owner of this photo.
     * @return the username of the owner
     */
    public String getOwner() {
        return owner;
    }
    
    /**
     * returns the file path of the photo
     * @return the photo path
     */
    public String getPath() {
        return path;
    }
    /**
     * updates the file path of the photo
     * @param path the new photo path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * returns the caption of the photo
     * @return the caption string
     */
    public String getCaption() {
        return caption;
    }
    /**
     * sets or updates the caption of the photo.
     * @param caption the new caption
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }
    /**
     * clears the current caption
     */
    public void removeCaption(){
        this.caption = "";
    }
    /**
     * returns the set of display dates associated with the photo.
     * @return a set of display date strings
     */
    public Set<String> getDates() {
        return dates;
    }
    /**
     * adds a new display date to the photo
     * @param date the date string to add
     */
    public void addDate(String date) {
        dates.add(date);
    }

    /**
     * Adds a calendar date to the set of real comparison dates.
     * Automatically clears milliseconds for comparison consistency.
     * @param calendar the Calendar date to add
     */
    public void addRealDate(Calendar calendar) {
        calendar.set(Calendar.MILLISECOND, 0);
        realDates.add(calendar);
    }

    /**
     * returns the set of Calendar dates used for date range comparisons.
     * @return a set of Calendar objects
     */
    public Set<Calendar> getRealDates(){
        return realDates;
    }
    /**
     * returns the set of tags associated with the photo
     * @return a set of tag strings
     */
    public Set<String> getTags() {
        return tags;
    }
    /**
     * adds a new tag to the photo.
     * @param tag the tag string to add
     */
    public void addTag(String tag) {
        tags.add(tag);
    }
    /**
     * removes a tag from the photo
     * @param tag the tag string to remove
     */
    public void removeTag(String tag){
        tags.remove(tag);
    }
    /**
     * Checks if this photo is equal to another photo based on file path.
     * @param o the object to compare
     * @return true if the paths are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Photo)) return false;
        Photo photo = (Photo) o;
        return path.equals(photo.path);
    }
    /**
     * Computes a hash code for the photo based on its path.
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return path.hashCode();
    }
    /**
     * Returns a string representation of the photo.
     * @return a string containing the path and dates
     */
    @Override
    public String toString() {
        return "Photo{" +
                "path='" + path + '\'' +
                ", dates=" + dates +
                '}';
    }
}

