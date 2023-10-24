module ru.nsu.fit {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.google.gson;
    requires org.apache.commons.lang3;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires geojson.jackson;
    requires org.apache.httpcomponents.httpclient;
    requires okhttp3;

    opens ru.nsu.fit to javafx.fxml;
    exports ru.nsu.fit;
    opens ru.nsu.fit.BusinessLogic.Network.response.geoResponse to com.google.gson;
    opens ru.nsu.fit.BusinessLogic.Network.response.placesResponse to com.google.gson;
    opens ru.nsu.fit.BusinessLogic.Network.response.weatherResponse to com.google.gson;
    opens ru.nsu.fit.BusinessLogic.Network.response.descResponse to com.google.gson;
    opens ru.nsu.fit.Visual to javafx.fxml;
    opens ru.nsu.fit.Controller to javafx.fxml;
    //exports ru.nsu.fit.controller to javafx.fxml;
}