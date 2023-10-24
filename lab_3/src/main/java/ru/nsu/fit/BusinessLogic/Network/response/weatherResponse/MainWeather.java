package ru.nsu.fit.BusinessLogic.Network.response.weatherResponse;

import com.google.gson.annotations.SerializedName;

public class MainWeather {
    public double getTemp() {
        return temp;
    }

    public double getFeels_like() {
        return feels_like;
    }

    public double getPressure() {
        return pressure;
    }

    public double getHumidity() {
        return humidity;
    }
    @SerializedName("temp")
    private double temp;

    @SerializedName("feels_like")
    private double feels_like;

    @SerializedName("pressure")
    private double pressure;

    @SerializedName("humidity")
    private double humidity;
}
