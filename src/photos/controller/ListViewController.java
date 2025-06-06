package photos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.collections.ObservableList;
/**
 * Controller for a ListViewDialog of sorts that displays
 * a list of all currently-existing "admin-approved" users.
 * This controller is usually initialized by PhotosController,
 * which is linked to the Admin page, which is where the view 
 * associated with this controller is loaded in. PhotosController
 * also provides the user data via {@link #setUsersList}
 * @author Harshi Oleti
 */
public class ListViewController {
    /**
     * The JavaFX ListView UI component that displays the list of users.
     * Injected via FXML.
     */
    @FXML
    private ListView<String> listView;

    
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
    public void setUsersList(ObservableList<String> usersList) {
        this.usersList = usersList;         // setting the usersList attribute of this object to the global usersList in PhotosController
        listView.setItems(this.usersList);  // apply to UI
    }
}

