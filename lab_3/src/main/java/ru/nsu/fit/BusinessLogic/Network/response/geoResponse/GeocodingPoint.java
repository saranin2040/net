package ru.nsu.fit.BusinessLogic.Network.response.geoResponse;

public class GeocodingPoint {
    private final Double lat;
    private final Double lng;

    public GeocodingPoint() {
        lat = 0.0;
        lng = 0.0;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }
}
