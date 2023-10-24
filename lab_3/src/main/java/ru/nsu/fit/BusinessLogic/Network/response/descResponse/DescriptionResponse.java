package ru.nsu.fit.BusinessLogic.Network.response.descResponse;

import com.google.gson.annotations.SerializedName;
public class DescriptionResponse
{
    public String getName() {
        return name;
    }

    public String getRate() {
        return rate;
    }

    public Info getInfo() {
        return info;
    }

    public Address getAddress() {
        return address;
    }
    @SerializedName("xid")
    private String xid;

    @SerializedName("name")
    private String name;

    @SerializedName("rate")
    private String rate;

    @SerializedName("info")
    private Info info;

    @SerializedName("address")
    private Address address;
}
