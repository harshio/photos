package photos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.collections.ObservableList;

public class ListViewController {

    @FXML
    private ListView<String> listView;

    // 👇 Global variable (class-wide), not initialized here
    private ObservableList<String> usersList;

    // Public setter to initialize it from another controller
    public void setUsersList(ObservableList<String> usersList) {
        this.usersList = usersList;         // setting the usersList attribute of this object to the global usersList in PhotosController
        listView.setItems(this.usersList);  // apply to UI
    }
}

