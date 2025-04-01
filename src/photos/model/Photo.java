package photos.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String path;
    private Set<String> dates;

    public Photo(String path) {
        this.path = path;
        this.dates = new HashSet<>();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Set<String> getDates() {
        return dates;
    }

    public void addDate(String date) {
        dates.add(date);
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

