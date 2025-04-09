package photos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.collections.ObservableList;
/**
 * Controller for a ListViewDialog of sorts that display
 * a list of all currently-existing "admin-approved" users.
 * This controller is usually initialized by PhotosController,
 * which is linked to the Admin page, which is where the view 
 * associated with this controller is loaded in. This controller
 * also provides the user data via {@link #setUsersList}
 */
public class ListViewController {
    /**
     * The JavaFX ListView UI component that displays the list of user.
     * Injected via FXML.
     */
    @FXML
    private ListView<String> listView;

    // 👇 Global variable (class-wide), not initialized here
    /**
     * The list of users to be displayed in the ListView.
     * This is passed in from the calling Controllers.
     */
    private ObservableList<String> usersList;

    /**
     * Sets the list of users to be shown in the ListView.
     * This method should be called by the initiating controller
     * before the view is shown.
     * @param usersList is an observable list of usernames to display
     */
    // Public setter to initialize it from another controller
    public void setUsersList(ObservableList<String> usersList) {
        this.usersList = usersList;         // setting the usersList attribute of this object to the global usersList in PhotosController
        listView.setItems(this.usersList);  // apply to UI
    }
}

