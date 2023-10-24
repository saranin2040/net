package ru.nsu.fit.BusinessLogic.Network.response.geoResponse;

import java.util.ArrayList;
import java.util.HashMap;

public class GeoResponse {
    private final ArrayList<GeocodingLocation> hits = new ArrayList<>();

    public ArrayList<GeocodingLocation> getHits() {
        return hits;
    }

    public ArrayList<GeocodingPoint> getListOfCoordinates() {
        ArrayList<GeocodingPoint> geocodingPoints = new ArrayList<>();
        for (GeocodingLocation geocodingLocation : hits) {
            geocodingPoints.add(geocodingLocation.getPoint());
        }
        return geocodingPoints;
    }

    public HashMap<String,GeocodingPoint> getLocationsMap()
    {
        HashMap<String,GeocodingPoint> locations=new HashMap<>();

        for (int i=0; i<hits.size();i++)
        {
            locations.put(hits.get(i).toString(),hits.get(i).getPoint());
        }

        return locations;
    }
}
