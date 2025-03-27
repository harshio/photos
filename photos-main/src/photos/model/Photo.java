package photos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.text.html.HTML.Tag;

public class Photo implements Serializable{
    private static final long serialVersionUID = 1L; 

    private String filePath;   
    private Date date;       
    private String caption;   
    private List<Tag> tags;    

    // Constructor
    public Photo(String filePath, Date date, String caption) {
        this.filePath = filePath;
        this.date = date;
        this.caption = caption;
        this.tags = new ArrayList<>();
    }

    // Getters and Setters
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }

    @Override
    public String toString() {
        return "Photo{" +
               "filePath='" + filePath + '\'' +
               ", date=" + date +
               ", caption='" + caption + '\'' +
               ", tags=" + tags +
               '}';
    }
}
