package photos.controller;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import photos.model.Album;
import photos.model.Photo;
import photos.model.Users;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//import javafx.scene.control.ListView; this import probably won't be used in this class but I'm paranoid
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * Controller for the search interface.
 * Allows users to search for photos by tag
 * or date range, display matching photos as a
 * slideshow, similar to how it is in the album view,
 * and optionally save them into a brand new album.
 * @author Harshi Oleti
 */
public class SearchController {
    /**
     * Button that lets user search with one
     * or two tags.
     */
    @FXML Button searchTag;
    /**
     * Button that lets user search by date range.
     */
    @FXML Button searchDate;
    /**
     * Error message for when user does an invalid tag query
     */
    @FXML Text errorTag;
    /**
     * Error message for when user does invalid date query.
     */
    @FXML Text errorDate;
    /**
     * Text field for where user inputs tag query.
     */
    @FXML TextField tagQuery;
    /**
     * Text field for where user inputs date query.
     */
    @FXML TextField dateQuery;
    /**
     * VBox for containing the slideshow.
     */
    @FXML
    private VBox slidesAndButton;
    /**
     * Button that loads in home page.
     */
    @FXML Button returnButton;
    /**
     * Current set of search results that is stored
     * so code can know which set to make into an album
     * if button is pressed.
     */
    private Set<Photo> searchResults = new HashSet<>();
    /**
     * Global int that tracks the amount of search comp albums that are made.
     */
    private int albumNumber = 1;
    /**
     * Dynamic list of VBox's used to help create the slideshow effect
     */
    private ArrayList<VBox> slides = new ArrayList<>();
    /**
     * Global int that is used to display a certain "slide" (VBox) from the slideshow
     */
    private int currentIndex = 0;
    /**
     * Button that is used to create an album from search results.
     */
    @FXML
    private VBox createButton;
    /**
     * Button that saves user date and quits application.
     */
    @FXML Button quitButton;
    /**
     * Initializes the search UI by clearing inputs, messages, and
     * previous search results
     */
    public void initialize(){
        slidesAndButton.getChildren().clear();
        createButton.getChildren().clear();
        slides.clear();
        searchResults.clear();
        errorTag.setText("");
        errorDate.setText("");
        tagQuery.setText("");
        dateQuery.setText("");
    }
    /**
     * saves user data and quits application
     * @param e is the triggering event from the quitButton button
     */
    public void quitApplication(ActionEvent e){
        photos.model.Users.saveUsersList();
        photos.model.Users.saveUserAlbums();
        photos.model.Users.saveUserTagTypes();
        Platform.exit();
        System.exit(0);
    }
    /**
     * Parses a start date from user input and returns a Calendar object.
     * Sets the time to the beginning of the day.
     * @param dateStr the date string in MM/dd/yyyy format
     * @return a Calendar set to the parsed start date
     * @throws ParseException if the input is not a valid date
     */
    private Calendar parseDateStart(String dateStr) throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        sdf.setLenient(false);
        Date parsedDate = sdf.parse(dateStr);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parsedDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }
    /**
     * Parses an end date from user input and returns a Calendar object.
     * Sets the time to the end of the day.
     * @param dateStr the date string in MM/dd/yyyy format
     * @return a Calendar set to the parsed end date
     * @throws ParseException if the input is not a valid date
     */
    private Calendar parseDateEnd(String dateStr) throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        sdf.setLenient(false);
        Date parsedDate = sdf.parse(dateStr);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parsedDate);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }
    /**
     * Searches all user albums for photos taken within a specified date range.
     * Validates date format, updates the search results, and displays them. 
     * @param e is the triggering event from the searchDate button
     */
    public void searchByDate(ActionEvent e){
        searchResults.clear();
        errorTag.setText("");
        errorDate.setText("");
        tagQuery.setText("");
        String dateRequest = dateQuery.getText().trim();
        //Let's do what we did with tag and list off a bunch of failure branches
        if(!dateRequest.matches("\\d{2}/\\d{2}/\\d{4}\\s*-\\s*\\d{2}/\\d{2}/\\d{4}")){
            errorDate.setText("Invalid Date Format. Look at the prompt text in the box closer");
            dateQuery.setText("");
            return;
        }
        //Lol there's actually only one failure branch for this method since we matched with a complex regex
        //Remember that despite the somewhat misleading name, it's actually searching by a date range, not searching by a date
        String[] dates = dateRequest.split("-");
        String earlyDate = dates[0].trim();
        String lateDate = dates[1].trim();
        Calendar firstCal;
        Calendar lastCal;
        try{
            firstCal = parseDateStart(earlyDate);
            lastCal = parseDateEnd(lateDate);
        } catch(ParseException d){
            errorDate.setText("Invalid Date. You most likely wrote a date implying an invalid number amount of months in a year or days in a month.");
            dateQuery.setText("");
            return;
        }
        //All that's left is iterating through Users.userAlbums.get(currentUser) and add Photo objects whose realDates are after firstCal and lastCal to searchResults
        for(Album album: Users.userAlbums.get(Users.currentUser).values()){
            for(Photo photo: album.getPhotos()){
                Calendar date = photo.getRealDates().iterator().next();
                if (!date.before(firstCal) && !date.after(lastCal)) {
                    searchResults.add(photo);
                }                
            }
        }
        //I lied. We have to do the slideshow stuff here too.
        if(searchResults.isEmpty()){
            slides.clear();
            slidesAndButton.getChildren().clear();
            createButton.getChildren().clear();
            Label message = new Label("No photos were found within this date range.");
            slidesAndButton.getChildren().add(message);
            dateQuery.setText("");
        }
        else{
            //Iterate through searchResults and place them in slides in the VBox
            dateQuery.setText("");
            slides.clear();
            currentIndex = 0;
            slidesAndButton.getChildren().clear();
            createButton.getChildren().clear();

            for (Photo photo : searchResults) {
                ImageView imageView = new ImageView(new Image(new File(photo.getPath()).toURI().toString()));
                imageView.setFitWidth(100);
                imageView.setPreserveRatio(true);

                Label caption = new Label(photo.getCaption());
                VBox slide = new VBox(10, imageView, caption);
                slide.setStyle("-fx-alignment: center;");

                slides.add(slide);
            }

            if (!slides.isEmpty()) {
                Button prev = new Button("Previous");
                Button next = new Button("Next");

                prev.setOnAction(ev -> {
                    if (currentIndex > 0) {
                        currentIndex--;
                        slidesAndButton.getChildren().set(0, slides.get(currentIndex));
                    }
                });

                next.setOnAction(ev -> {
                    if (currentIndex < slides.size() - 1) {
                        currentIndex++;
                        slidesAndButton.getChildren().set(0, slides.get(currentIndex));
                    }
                });

                HBox navBar = new HBox(10, prev, next);
                navBar.setStyle("-fx-alignment: center;");

                slidesAndButton.getChildren().addAll(slides.get(currentIndex), navBar);
            }
            //Now we'll dynamically place the Button
            Button weirdAlbum = new Button("Create Album from these search results");
            //We'll setOnAction later when we make the create album button. This is a comment for a currently non-existent line
            weirdAlbum.setOnAction(c -> createAlbum(c));
            createButton.getChildren().add(weirdAlbum);
        }
    }
    /**
     * Search for all albums associated with the current user for photos
     * matching a single tag, or a combination of two tags using "and"/"or" logic.
     * Validates the query format and updates the view.
     * @param e is the triggering event from the searchTag button.
     */
    public void searchByTag(ActionEvent e){
        searchResults.clear();
        errorTag.setText("");
        errorDate.setText("");
        dateQuery.setText("");
        String tagRequest = tagQuery.getText().trim().toLowerCase();
        //If there's 0 "="s, you fail (write errorTag.setText("Invalid String") write tagQuery.setText("") write return;)
        if(!tagRequest.contains("=")){
            errorTag.setText("Invalid String 1");
            tagQuery.setText("");
            return;
        }
        //Weird bizzare stuff
        if(tagRequest.contains(" and ") && tagRequest.contains(" or ")){
            errorTag.setText("Invalid String 1");
            tagQuery.setText("");
            return;
        }
        //If there's more than 2 "="s, you fail
        if(tagRequest.chars().filter(c -> c == '=').count() > 2){
            errorTag.setText("Invalid String 2");
            tagQuery.setText("");
            return;
        }
        //If there's 2 "="s, and there's no 'and' and there's no 'or' in the line, you fail
        if((tagRequest.chars().filter(c -> c == '=').count() == 2) && !tagRequest.contains("and") && !tagRequest.contains("or")){
            errorTag.setText("Invalid String 3");
            tagQuery.setText("");
            return;
        }
        //If there's 'and' or an 'or', and there isn't 2 "="s, you fail
        if((tagRequest.contains(" and ") || tagRequest.contains(" or ")) && (tagRequest.chars().filter(c -> c == '=').count() != 2)){
            errorTag.setText("Invalid String 4");
            tagQuery.setText("");
            return;
        }
        //If there's 1 "=", pull from tagQuery and use unique combination of calls to findTaggedPhotos, with substrings from tagQuery as arguments
        if(tagRequest.chars().filter(c -> c == '=').count() == 1){
            String[] partsOfTag = tagRequest.split("=");
            if(partsOfTag.length != 2){ 
                errorTag.setText("Invalid String 5");
                tagQuery.setText("");
                return; //you should fail if there's one equals sign and it's at the end
            }
            String possibleName = partsOfTag[0].trim();
            String possibleValue = partsOfTag[1].trim();
            searchResults = findTaggedPhotos(possibleName, possibleValue);
        }
        //If 'and' exclusive-or 'or' is true, pull from tagQuery and use unique combination of calls to findTaggedPhotos, with substrings from tagQuery as arguments
        if(tagRequest.contains(" and ") ^ tagRequest.contains(" or ")){
            //If disjunction
            if(tagRequest.contains("and")){
                //Out of paranoia, let's confirm that there's only 1 'and'
                String normalized = tagRequest.trim().replaceAll("\\s+", " ");
                String[] parts = normalized.split("\\band\\b");
                if(parts.length != 2){
                    errorTag.setText("Invalid String 6");
                    tagQuery.setText("");
                    return;
                }
                //Rest of code
                String[] tags = tagRequest.split("\\band\\b");
                if(tags.length != 2){ 
                    errorTag.setText("Invalid String 7");
                    tagQuery.setText("");
                    return; //you should fail if there's some 'and' and it's at the end
                }
                String tag1 = tags[0].trim();
                String tag2 = tags[1].trim();

                String[] partsOfTag1 = tag1.split("=");
                if(partsOfTag1.length != 2){ 
                    errorTag.setText("Invalid String 8");
                    tagQuery.setText("");
                    return; //you should fail if there's some 'and' and it's at the end
                }
                String tagName1 = partsOfTag1[0].trim();
                String tagValue1 = partsOfTag1[1].trim();

                String[] partsOfTag2 = tag2.split("=");
                if(partsOfTag2.length != 2){ 
                    errorTag.setText("Invalid String 9");
                    tagQuery.setText("");
                    return; //you should fail if there's some 'and' and it's at the end
                }
                String tagName2 = partsOfTag2[0].trim();
                String tagValue2 = partsOfTag2[1].trim();

                searchResults = findTaggedPhotos(tagName1, tagValue1);
                searchResults.retainAll(findTaggedPhotos(tagName2, tagValue2));
            }
            //If conjunction
            else{
                //Out of paranoia, let's confirm that there's only 1 'or'
                String normalized = tagRequest.trim().replaceAll("\\s+", " ");
                String[] parts = normalized.split("\\bor\\b");
                if(parts.length != 2){
                    errorTag.setText("Invalid String 10");
                    tagQuery.setText("");
                    return;
                }
                //Rest of code
                String[] tags = tagRequest.split("\\bor\\b");
                if(tags.length != 2){ 
                    errorTag.setText("Invalid String 11");
                    tagQuery.setText("");
                    return; //you should fail if there's some 'and' and it's at the end
                }
                String tag1 = tags[0].trim();
                String tag2 = tags[1].trim();
                
                String[] partsOfTag1 = tag1.split("=");
                if(partsOfTag1.length != 2){ 
                    errorTag.setText("Invalid String 12");
                    tagQuery.setText("");
                    return; //you should fail if there's some 'and' and it's at the end
                }
                String tagName1 = partsOfTag1[0].trim();
                String tagValue1 = partsOfTag1[1].trim();

                String[] partsOfTag2 = tag2.split("=");
                if(partsOfTag2.length != 2){ 
                    errorTag.setText("Invalid String 13");
                    tagQuery.setText("");
                    return; //you should fail if there's some 'and' and it's at the end
                }
                String tagName2 = partsOfTag2[0].trim();
                String tagValue2 = partsOfTag2[1].trim();

                searchResults = findTaggedPhotos(tagName1, tagValue1);
                searchResults.addAll(findTaggedPhotos(tagName2, tagValue2));
            }
        }
        //Now we have an initialized Set<Photos> called searchResults. All search results are gathered.
        //If there's no search results, meaning searchResults is empty, we can tell the user that no such photos with that specification exists
        if(searchResults.isEmpty()){
            tagQuery.setText("");
            slides.clear();
            slidesAndButton.getChildren().clear();
            createButton.getChildren().clear();
            Label message = new Label("I'm sorry, but no photos of that specification exists.");
            slidesAndButton.getChildren().add(message);
        }
        else{
            //Iterate through searchResults and place them in slides in the VBox
            tagQuery.setText("");
            slides.clear();
            currentIndex = 0;
            slidesAndButton.getChildren().clear();
            createButton.getChildren().clear();

            for (Photo photo : searchResults) {
                ImageView imageView = new ImageView(new Image(new File(photo.getPath()).toURI().toString()));
                imageView.setFitWidth(100);
                imageView.setPreserveRatio(true);

                Label caption = new Label(photo.getCaption());
                VBox slide = new VBox(10, imageView, caption);
                slide.setStyle("-fx-alignment: center;");

                slides.add(slide);
            }

            if (!slides.isEmpty()) {
                Button prev = new Button("Previous");
                Button next = new Button("Next");

                prev.setOnAction(ev -> {
                    if (currentIndex > 0) {
                        currentIndex--;
                        slidesAndButton.getChildren().set(0, slides.get(currentIndex));
                    }
                });

                next.setOnAction(ev -> {
                    if (currentIndex < slides.size() - 1) {
                        currentIndex++;
                        slidesAndButton.getChildren().set(0, slides.get(currentIndex));
                    }
                });

                HBox navBar = new HBox(10, prev, next);
                navBar.setStyle("-fx-alignment: center;");

                slidesAndButton.getChildren().addAll(slides.get(currentIndex), navBar);
            }
            //Now we'll dynamically place the Button
            Button weirdAlbum = new Button("Create Album from these search results");
            //We'll setOnAction later when we make the create album button. This is a comment for a currently non-existent line
            weirdAlbum.setOnAction(c -> createAlbum(c));
            createButton.getChildren().add(weirdAlbum);
        }
    }
    /**
     * Finds photos with the specified tag name and value from all albums
     * associated with the current user.
     * @param tagName is the tag type (name) to match
     * @param tagValue is the tag value to match
     * @return a set of photos matching the tag criteria.
     */
    private Set<Photo> findTaggedPhotos(String tagName, String tagValue){
        Set<Photo> results = new HashSet<>();
        for (Album album : Users.userAlbums.get(Users.currentUser).values()) {
            Set<Photo> pList = album.getPhotos();
            for(Photo p: pList){
                Set<String> tagsOfPhoto = p.getTags();
                for(String s: tagsOfPhoto){
                    String[] partsOfTag = s.split(",");
                    if(partsOfTag.length != 2) continue;
                    String maybeName = partsOfTag[0].trim();
                    String maybeValue = partsOfTag[1].trim();
                    if(maybeName.equals(tagName) && maybeValue.equals(tagValue)){
                        results.add(p);
                        break;
                    }
                }
            }
        }
        return results;
    }
    /**
     * Creates a new album from the current search results.
     * Automatically names it "search compilation #n" and adds all results.
     * @param e is the triggering event from the createButton button.
     */
    public void createAlbum(ActionEvent e){
        //Start by initializing an album
        Album searchAlbum = new Album();
        //Let's put in the photos
        for(Photo p: searchResults){
            searchAlbum.addPhoto(p);
        }
        //Now let's set the earliest and latest dates
        //We'll start by initializing a bunch of variables
        String earliestDateStr = null;
        String latestDateStr = null;
        Calendar earliest = null;
        Calendar latest = null;

        for(Photo p: searchResults){
            Calendar current = p.getRealDates().iterator().next();
            if(earliest == null || current.before(earliest)){
                earliest = current;
            }
            if(latest == null || current.after(latest)){
                latest = current;
            }
        }

        if(earliest != null && latest != null){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            earliestDateStr = sdf.format(earliest.getTime());
            latestDateStr = sdf.format(latest.getTime());
        }

        searchAlbum.setOldestDate(earliestDateStr);
        searchAlbum.setNewestDate(latestDateStr);
        int counter = 1;
        String name = "search compilation # ";
        Map<String, Album> userMap = Users.userAlbums.get(Users.currentUser);
        String trueName = name + counter;
        while (userMap.containsKey(trueName)) {
            counter++;
            trueName = name + counter;
        }
        //Finally, we'll add the album
        Users.userAlbums.get(Users.currentUser).put(trueName, searchAlbum);
        Users.saveUserAlbums(); //the one persistence thing we need to do here
        albumNumber++;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Bulk.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Home Page");
            stage.show();
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Page");
            alert.setContentText("Something went wrong while loading the page.\nDetails: " + ex.getMessage());
            alert.showAndWait();
        }
    }
    /**
     * Returns to the home page from the search screen.
     * @param e is the triggering event from the returnButton button
     */
    public void returnToHome(ActionEvent e){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/Bulk.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Home Page");
            stage.show();
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Page");
            alert.setContentText("Something went wrong while loading the page.\nDetails: " + ex.getMessage());
            alert.showAndWait();
        }
    }
}
