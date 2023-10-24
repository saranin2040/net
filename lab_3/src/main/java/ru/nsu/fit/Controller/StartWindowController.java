package ru.nsu.fit.Controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import ru.nsu.fit.App;
import ru.nsu.fit.BusinessLogic.BusinessLogic;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class StartWindowController {
    @FXML
    void showLocations(MouseEvent event)
    {
        try
        {
            //Load locations
            if (BusinessLogic.loadLocations(locationField.getText())){errorLabel.setText(MESSAGE_NON_EXISTENT_LOCATION); return;}

            //Enable select location view
            enable_selectLocationView();
        }
        catch (IOException | ExecutionException | InterruptedException | URISyntaxException e)
        {
            errorLabel.setText(MESSAGE_OF_ERROR);
            e.printStackTrace();
        }
    }

    private void enable_selectLocationView()throws IOException,ExecutionException,InterruptedException,URISyntaxException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SELECT_LOCATION_VIEW));
        selectLocationView = loader.load();
        LocationSelectionController descriptionController = loader.getController();
        BusinessLogic.setClientWiretapping(descriptionController);
        App.setNewScene(selectLocationView);
    }
    static private final String SELECT_LOCATION_VIEW = "/ru/nsu/fit/locationSelection.fxml";
    static private final String MESSAGE_OF_ERROR = "Something went wrong. Please try again";
    static private final String MESSAGE_NON_EXISTENT_LOCATION = "There are no such location";


    private Parent selectLocationView;

    @FXML
    private TextField locationField;

    @FXML
    private Label errorLabel;
}
