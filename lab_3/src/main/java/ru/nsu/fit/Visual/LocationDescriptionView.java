package ru.nsu.fit.Visual;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import ru.nsu.fit.BusinessLogic.BusinessLogic;
import ru.nsu.fit.BusinessLogic.CallBack.Wiretap;

public class LocationDescriptionView implements Wiretap
{
    @Override
    public void update()
    {
        String places = BusinessLogic.getPlacesText();
        String descriptions = BusinessLogic.getDescriptionsText();
        String weather = BusinessLogic.getWeatherText();

        Platform.runLater(() -> this.places.setText(places));
        Platform.runLater(() -> this.descriptions.setText(descriptions));
        Platform.runLater(() -> this.weather.setText(weather));
    }

    @FXML
    private TextArea descriptions;
    @FXML
    private TextArea places;
    @FXML
    private TextArea weather;
}
