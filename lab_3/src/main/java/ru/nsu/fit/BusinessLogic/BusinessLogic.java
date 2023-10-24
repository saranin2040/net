package ru.nsu.fit.BusinessLogic;

import org.apache.commons.lang3.StringUtils;
import ru.nsu.fit.BusinessLogic.Network.Client;
import ru.nsu.fit.BusinessLogic.Network.response.descResponse.Address;
import ru.nsu.fit.BusinessLogic.Network.response.descResponse.DescriptionResponse;
import ru.nsu.fit.BusinessLogic.Network.response.descResponse.Info;
import ru.nsu.fit.BusinessLogic.Network.response.placesResponse.PlacesElement;
import ru.nsu.fit.BusinessLogic.Network.response.weatherResponse.MainWeather;
import ru.nsu.fit.BusinessLogic.Network.response.weatherResponse.WeatherResponse;
import ru.nsu.fit.BusinessLogic.CallBack.Wiretap;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BusinessLogic {

    static public boolean loadLocations(String text) throws IOException, ExecutionException,InterruptedException, URISyntaxException
    {
        if (checkErrorText(text)){return true;}

        client.loadGeoLocation(text);

        return false;
    }

    static public void loadDescriptionsPlaces(String location)
    {
        client.loadDescriptionsPlaces(location);
    }

    static public ArrayList<String> getLocationsList()
    {
        if (!client.isGeoReady()) {return null;}
        return client.getLocationsList();
    }

    static public String getPlacesText()
    {
        if (!client.isPlacesReady()) {return EMPTY_TEXT;}

        ArrayList<PlacesElement> places = client.getPlaces();
        if (places.size() == EMPTY_SIZE){return EMPTY_SIZE_PLACES;}

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < places.size(); i++)
        {
            addIndex(stringBuilder,i);
            addName(stringBuilder,places.get(i).getName());
            addKind(stringBuilder,places.get(i).getKind());

            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    static public String getDescriptionsText()
    {
        if (!client.isDescReady()) {return EMPTY_TEXT;}

        ArrayList<DescriptionResponse> descriptions = client.getDescriptions();

        if (descriptions.size() == EMPTY_SIZE) {return EMPTY_SIZE_DESCRIPTION;}

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < descriptions.size(); i++)
        {
            addIndex(stringBuilder,i);
            addRate(stringBuilder,descriptions.get(i).getRate());
            addAddress(stringBuilder,descriptions.get(i).getAddress());
            addInfo(stringBuilder,descriptions.get(i).getInfo());

            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    static public String getWeatherText()
    {
        if (!client.isWeatherReady()) return EMPTY_TEXT;

        WeatherResponse weatherResponse = client.getWeather();

        MainWeather mainWeather = weatherResponse.getMain();
        return "temp: " + mainWeather.getTemp() + "\n" +
                "feels like: " + mainWeather.getFeels_like() + "\n"+
                "humidity: " + mainWeather.getHumidity() + "\n"+
                "pressure: " + mainWeather.getPressure();
    }

    static public void setClientWiretapping(Wiretap wiretap)
    {
        client.setWiretap(wiretap);
    }

    static private void addIndex(StringBuilder stringBuilder,int i)
    {
        stringBuilder.append(i + 1).append(") ");
    }

    static private void addName(StringBuilder stringBuilder,String placeName)
    {
        if (!Objects.equals(placeName, EMPTY_TEXT)) {
            stringBuilder.append(placeName).append(":\n");
        }
    }

    static private void addKind(StringBuilder stringBuilder,String placeKind)
    {
        if (placeKind != null) {
            stringBuilder.append(placeKind);
        } else {
            stringBuilder.append("no kinds");
        }
    }

    static private void addRate(StringBuilder stringBuilder,String decriptionRate)
    {
        stringBuilder.append("Rate: ").append(decriptionRate).append(COMMA);
    }

    static private void addAddress(StringBuilder stringBuilder,Address decriptionAddress)
    {
        if (decriptionAddress != null)
        {
            stringBuilder.append(Stream.of(
                            decriptionAddress.getCity(),
                            decriptionAddress.getRoad(),
                            decriptionAddress.getHouse())
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.joining(COMMA)));
        }
    }
    static private void addInfo(StringBuilder stringBuilder, Info decriptionInfo)
    {
        if (decriptionInfo != null && decriptionInfo.getDescr() != null)
        {
            stringBuilder.append(" description:").append(decriptionInfo.getDescr());
        }
    }
    static private boolean checkErrorText(String text)
    {
        if (text.replaceAll(GAPS,EMPTY).equals(EMPTY))
        {
            return true;
        }
        return false;
    }

    static Client client=new Client();

    static private final String GAPS = "\\s+";
    static private final String EMPTY = "";
    private static final String COMMA = ", ";
    static private final String EMPTY_TEXT = "loading...";
    private static final String EMPTY_SIZE_DESCRIPTION = "no description";
    private static final String EMPTY_SIZE_PLACES = "no places";
    private static final int EMPTY_SIZE = 0;
}
