package ru.nsu.fit.BusinessLogic.Network.response.placesResponse;

import com.google.gson.annotations.SerializedName;

public class PlacesElement
{
    public String getName() {return name;}

    public String getXid() {
        return xid;
    }

    public String getKind() {
        return kinds;
    }

    @SerializedName("name")
    private String name;
    @SerializedName("xid")
    private String xid;
    @SerializedName("kinds")
    private String kinds;
}
