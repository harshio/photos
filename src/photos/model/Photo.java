package photos.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String path;
    private Set<String> dates; //I guess this didn't need to be a set but it's not like users can add dates so it doesn't matter
    //storing date display messages
    private Set<String> tags;
    private String caption;
    private Set<Calendar> realDates; //storing dates we'll use for comparison

    public Photo(String path) {
        this.path = path;
        this.dates = new HashSet<>();
        this.tags = new HashSet<>();
        this.caption = "";
        this.realDates = new HashSet<>();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void removeCaption(){
        this.caption = "";
    }

    public Set<String> getDates() {
        return dates;
    }

    public void addDate(String date) {
        dates.add(date);
    }

    public Set<Calendar> getRealDates(){
        return realDates;
    }

    public void addRealDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            Date date1 = sdf.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date1);
            calendar.set(Calendar.MILLISECOND, 0);
            realDates.add(calendar);
        }
        catch(ParseException e){
            System.out.println("Failed to parse date: " + date);
            e.printStackTrace();
        }
    }

    public Set<String> getTags() {
        return tags;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public void removeTag(String tag){
        tags.remove(tag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Photo)) return false;
        Photo photo = (Photo) o;
        return path.equals(photo.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public String toString() {
        return "Photo{" +
                "path='" + path + '\'' +
                ", dates=" + dates +
                '}';
    }
}

