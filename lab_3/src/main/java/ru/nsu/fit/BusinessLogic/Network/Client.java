package ru.nsu.fit.BusinessLogic.Network;

import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.nsu.fit.BusinessLogic.Network.response.descResponse.DescriptionResponse;
import ru.nsu.fit.BusinessLogic.Network.response.geoResponse.GeoResponse;
import ru.nsu.fit.BusinessLogic.Network.response.geoResponse.GeocodingPoint;
import ru.nsu.fit.BusinessLogic.Network.response.placesResponse.PlacesElement;
import ru.nsu.fit.BusinessLogic.Network.response.weatherResponse.WeatherResponse;
import ru.nsu.fit.BusinessLogic.CallBack.Notification;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import okhttp3.Response;
import okhttp3.Callback;
import java.io.IOException;

public class Client extends Notification {
//
//    private void sendPlacesRequest(GeocodingPoint point) {
//        increaseCountRequests();
//
//        String url = URICreator.getPlacesURI(point).toString();
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                // Обработка ошибки
//                e.printStackTrace();
//                reduceCountRequests();
//                notifyWiretap();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) {
//                try {
//                    if (response.isSuccessful()) {
//                        String placesFuture = response.body().string();
//                        getPlacesResponse(placesFuture);
//                    } else {
//                        // Обработка неуспешного ответа
//                        reduceCountRequests();
//                        notifyWiretap();
//                    }
//                } catch (IOException e) {
//                    // Обработка ошибки
//                    e.printStackTrace();
//                    reduceCountRequests();
//                    notifyWiretap();
//                }
//            }
//        });
//
//        timeOfLastSending = System.currentTimeMillis();
//    }

    public CompletableFuture<String> getGeoLocation(String location) throws URISyntaxException
    {
        String encodeLocation = java.net.URLEncoder.encode(location, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder().uri(URICreator.getGeoURI(encodeLocation)).build();
        timeOfLastSending = System.currentTimeMillis();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body);
    }

    public void loadGeoLocation(String location) throws URISyntaxException
    {
        String encodeLocation = java.net.URLEncoder.encode(location, StandardCharsets.UTF_8);
        sendGeoLocation(encodeLocation);
    }


    public void loadDescriptionsPlaces(String location)
    {
        try
        {
            GeocodingPoint coordinates=geoResponse.getLocationsMap().get(location);
            if (coordinates==null) {return;}

            sendWeatherRequest(coordinates);
            sendPlacesRequest(coordinates);
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();/////////////////////////////////////////////////////////
        }
    }


    public ArrayList<String> getLocationsList()
    {
        return new ArrayList<>(geoResponse.getLocationsMap().keySet());
    }

    public ArrayList<PlacesElement> getPlaces()
    {
        return placesResponse;
    }

    public WeatherResponse getWeather() {
        return weatherResponse;
    }

    public ArrayList<DescriptionResponse> getDescriptions()
    {
        return descriptionRespons;
    }

    public boolean isGeoReady() {
        return isGeoReady;
    }

    public boolean isWeatherReady() {
        return isWeatherReady;
    }

    public boolean isPlacesReady()
    {
        return isPlacesReady;
    }

    public boolean isDescReady() {
        return isDescReady;
    }

    public void sendGeoLocation(String requestText) throws URISyntaxException
    {
        increaseCountRequests();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URICreator.getGeoURI(requestText))
                .build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::getlocationsResponse);
        timeOfLastSending = System.currentTimeMillis();
    }

    private void sendWeatherRequest(GeocodingPoint point) throws URISyntaxException
    {
        increaseCountRequests();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URICreator.getWeatherURI(point))
                .build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::getWeatherResponse);
        timeOfLastSending = System.currentTimeMillis();
    }

    private void sendPlacesRequest(GeocodingPoint point) throws URISyntaxException
    {
        increaseCountRequests();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URICreator.getPlacesURI(point))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::getPlacesResponse)
                .thenAccept(this::sendDescriptionsRequest);
        timeOfLastSending = System.currentTimeMillis();
    }
    private void sendDescriptionRequest(String xid) throws URISyntaxException
    {
        increaseCountRequests();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URICreator.getDescURI(xid))
                .build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::getDescriptionsResponse);
        timeOfLastSending = System.currentTimeMillis();
    }

    private void sendDescriptionsRequest(String f)
    {
            if (placesResponse.size() == EMPTY_SIZE) {
                isDescReady = true;
                descriptionRespons = null;
                return;
            }

            for (PlacesElement place : placesResponse)
            {
                checkLimit();

                try
                {
                    sendDescriptionRequest(place.getXid());
                }
                catch (URISyntaxException e)
                {
                    e.printStackTrace();
                    break;
                }
            }
    }

    private void getlocationsResponse(String locationsFuture)
    {
        geoResponse = gson.fromJson(locationsFuture, GeoResponse.class);
        isGeoReady = true;
        reduceCountRequests();
        notifyWiretap();
    }

    private void getWeatherResponse(String weatherFuture) {
        weatherResponse = gson.fromJson(weatherFuture, WeatherResponse.class);
        isWeatherReady = true;
        reduceCountRequests();
        notifyWiretap();
    }

    private String getPlacesResponse(String placesFuture)
    {
        TypeToken<ArrayList<PlacesElement>> collectionType = new TypeToken<>(){};
        placesResponse = gson.fromJson(placesFuture, collectionType);
        isPlacesReady = true;
        notifyWiretap();
        reduceCountRequests();
        return placesFuture;
    }

    private String getDescriptionsResponse(String descFuture) {
        DescriptionResponse response = gson.fromJson(descFuture, DescriptionResponse.class);
        descriptionRespons.add(response);
        isDescReady = true;
        reduceCountRequests();
        notifyWiretap();
        return descFuture;
    }

    private void checkLimit()
    {
        long time = System.currentTimeMillis() - timeOfLastSending;
        if (time < TIME_LIMIT &&
                getcountRequests()  >= REQUESTS_LIMIT)
        {
            while(getcountRequests()  >= REQUESTS_LIMIT)
            {

            }
        }
    }
    private synchronized void increaseCountRequests() {
        countRequests++;
    }

    private synchronized void reduceCountRequests() {
        countRequests--;
    }

    private synchronized int getcountRequests() {
        return countRequests;
    }

    private final int TIME_LIMIT = 60 * 100;
    private final int REQUESTS_LIMIT = 6;
    private final int EMPTY_SIZE = 0;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    private boolean isGeoReady = false;
    private boolean isWeatherReady = false;
    private boolean isPlacesReady = false;
    private boolean isDescReady = false;

    private GeoResponse geoResponse;
    private WeatherResponse weatherResponse;
    private ArrayList<PlacesElement> placesResponse;
    private ArrayList<DescriptionResponse> descriptionRespons = new ArrayList<>();
    private long timeOfLastSending;
    private int countRequests = 0;


}
