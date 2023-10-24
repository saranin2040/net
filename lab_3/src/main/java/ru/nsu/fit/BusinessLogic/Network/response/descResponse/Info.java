package ru.nsu.fit.BusinessLogic.Network.response.descResponse;

import com.google.gson.annotations.SerializedName;
public class Info
{
    public String getDescr() {
        return descr;
    }
    @SerializedName("descr")
    private String descr;
}
