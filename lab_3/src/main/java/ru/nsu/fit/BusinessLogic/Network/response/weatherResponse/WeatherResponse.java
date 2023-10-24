package ru.nsu.fit.BusinessLogic.Network.response.weatherResponse;

import com.google.gson.annotations.SerializedName;
public class WeatherResponse
{
    public MainWeather getMain() {
        return main;
    }
    @SerializedName("main")
    private MainWeather main;
}