package photos.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Album implements Serializable {
    private static final long serialVersionUID = 1L;

    private Set<Photo> photos;
    private String oldestDate;
    private String newestDate; //in the leaveAlbum method or whatever it's called in the AlbumController file, we'll include code that always calculates album info and then stores it in the hashmap and saves it
    //will use Calendar instances for the sake of comparison, but will convert back to String for storage purposes

    public Album() {
        this.photos = new HashSet<>();
        this.oldestDate = "";
        this.newestDate = "";
    }
    
    public Set<Photo> getPhotos() {
        return photos;
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
    }

    public void removePhoto(Photo photo) {
        photos.remove(photo);
    }

    public String getOldestDate(){
        return oldestDate;
    }

    public void setOldestDate(String oldestDate){
        this.oldestDate = oldestDate;
    }

    public String getNewestDate(){
        return newestDate;
    }

    public void setNewestDate(String newestDate){
        this.newestDate = newestDate;
    }

    public int getSize(){
        return photos.size();
    }
}
