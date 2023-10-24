package ru.nsu.fit.BusinessLogic.Network;

import org.apache.http.client.utils.URIBuilder;
import ru.nsu.fit.BusinessLogic.Network.response.geoResponse.GeocodingPoint;

import java.net.URI;
import java.net.URISyntaxException;

public class URICreator {
    static public URI getGeoURI(String encodeLocation) throws URISyntaxException
    {
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme(HTTPS);
        uriBuilder.setPath(GEO_PATH);
        uriBuilder.addParameter(FORMAT_QUERY, JSON);
        uriBuilder.addParameter(LOCATION_QUERY, encodeLocation);
        uriBuilder.addParameter(GEO_APIKEY_QUERY, GEO_APIKEY);
        return uriBuilder.build();
    }

    static public URI getWeatherURI(GeocodingPoint point) throws URISyntaxException
    {
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme(HTTPS);
        uriBuilder.setPath(WEATHER_PATH);
        uriBuilder.addParameter(FORMAT_QUERY, JSON);
        uriBuilder.addParameter(LONGITUDE_QUERY, String.valueOf(point.getLng()));
        uriBuilder.addParameter(LATITUDE_QUERY, String.valueOf(point.getLat()));
        uriBuilder.addParameter(WEATHER_APIKEY_QUERY, WEATHER_APIKEY);
        return uriBuilder.build();
    }

    static public URI getPlacesURI(GeocodingPoint point) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme(HTTPS);
        uriBuilder.setPath(PLACES_PATH);
        uriBuilder.addParameter(FORMAT_QUERY, JSON);
        uriBuilder.addParameter(LONGITUDE_QUERY, String.valueOf(point.getLng()));
        uriBuilder.addParameter(LATITUDE_QUERY, String.valueOf(point.getLat()));
        uriBuilder.addParameter(RADIUS_QUERY, String.valueOf(RADIUS));
        uriBuilder.addParameter(PLACES_APIKEY_QUERY, PLACES_APIKEY);
        return uriBuilder.build();
    }

    static public URI getDescURI(String xid) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme(HTTPS);
        uriBuilder.setPath(DESC_PATH + xid);
        uriBuilder.addParameter(FORMAT_QUERY, JSON);
        uriBuilder.addParameter(RADIUS_QUERY, String.valueOf(RADIUS));
        uriBuilder.addParameter(PLACES_APIKEY_QUERY, PLACES_APIKEY);
        return uriBuilder.build();
    }

    static private final String HTTPS = "https";
    static private final String GEO_PATH = "//graphhopper.com/api/1/geocode";
    static private final String WEATHER_PATH = "//api.openweathermap.org/data/2.5/weather";
    static private final String PLACES_PATH = "//api.opentripmap.com/0.1/en/places/radius";
    static private final String DESC_PATH = "//api.opentripmap.com/0.1/en/places/xid/";
    static private final String GEO_APIKEY_QUERY = "key";
    static private final String WEATHER_APIKEY_QUERY = "appid";
    static private final String PLACES_APIKEY_QUERY = "apikey";
    static private final String GEO_APIKEY = "eb13575e-c1df-4741-901e-71d988ae7891";
    static private final String WEATHER_APIKEY = "57b3cd71df27da05973edeab0aad25a1";
    static private final String PLACES_APIKEY = "5ae2e3f221c38a28845f05b6842547a33b3a3ac96b32b01a21807ba7";
    static private final String LOCATION_QUERY = "q";
    static private final String LONGITUDE_QUERY = "lon";
    static private final String LATITUDE_QUERY = "lat";
    static private final String RADIUS_QUERY = "radius";
    static private final int RADIUS = 500;
    static private final String FORMAT_QUERY = "format";
    static private final String JSON = "json";


}
