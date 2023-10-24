package ru.nsu.fit.BusinessLogic.Network.response.descResponse;

import com.google.gson.annotations.SerializedName;

public class Address
{
    public String getCity() {
        return city;
    }

    public String getRoad() {
        return road;
    }

    public String getHouse() {
        return house;
    }

    @SerializedName("city")
    private String city;
    @SerializedName("road")
    private String road;
    @SerializedName("house")
    private String house;

}
