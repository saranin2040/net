package ru.nsu.fit.BusinessLogic.Network.response.geoResponse;


import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GeocodingLocation
{
    @Override
    public String toString()
    {
        return Stream.of(country, state, city,name, street)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(POWERED));
    }

    public GeocodingPoint getPoint() {
        return point;
    }

    public String getName() {
        return name;
    }

    private GeocodingPoint point = new GeocodingPoint();
    @SerializedName("name")
    private String name;
    @SerializedName("country")
    private String country;
    @SerializedName("state")
    private String state;
    @SerializedName("city")
    private String city;
    @SerializedName("street")
    private String street;

    private final String POWERED = ", ";
}
