package ru.nsu.fit.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import ru.nsu.fit.App;
import ru.nsu.fit.BusinessLogic.BusinessLogic;
import ru.nsu.fit.Visual.LocationSelectionView;

import java.io.IOException;

public class LocationSelectionController extends LocationSelectionView {


    @FXML
    public void showLocationDescription(MouseEvent event)
    {
        if (selectionBox.getSelectionModel().isEmpty())
        {
            errorLabel.setText(MESSAGE_OF_EMPTY_LOCATION);
            return;
        }

        String location = selectionBox.getValue();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(DESCRIPTION_LOCATION_VIEW));

        try
        {
            BusinessLogic.loadDescriptionsPlaces(location);

            Parent root =  loader.load();
            LocationDescriptionController descriptionController = loader.getController();
            BusinessLogic.setClientWiretapping(descriptionController);

            App.setNewScene(root);
        }
        catch (IOException e) {
            errorLabel.setText(MESSAGE_OF_EMPTY_LOCATION);
        }
    }
    @FXML
    void goBack(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(START_WINDOW_VIEW));
            Parent root = loader.load();

            App.setNewScene(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        initView();
    }

    @FXML
    private Label errorLabel;

    private static final String MESSAGE_OF_EMPTY_LOCATION = "Please, choose one of the locations";
    private static final String DESCRIPTION_LOCATION_VIEW = "/ru/nsu/fit/locationDescription.fxml";
    private static final String START_WINDOW_VIEW = "/ru/nsu/fit/primary.fxml";
}