package ru.nsu.fit.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import ru.nsu.fit.App;
import ru.nsu.fit.Visual.LocationDescriptionView;

import java.io.IOException;

public class LocationDescriptionController extends LocationDescriptionView {
    @FXML
    void goBack(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_SELECTION_VIEW));
            Parent root = loader.load();

            App.setNewScene(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static final String LOCATION_SELECTION_VIEW = "/ru/nsu/fit/locationSelection.fxml";

}