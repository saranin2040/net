package org.example.BusinessLogic.Network.Data;

import java.util.*;

public class DataMulticastServer
{

    public synchronized void addFoundGame(DataGameAnnouncement foundGame)
    {
        foundGames.put(new Adress(foundGame.getIp(),foundGame.getPort()),foundGame);
    }
    public void deleteLeftGames()
    {
        Iterator<Map.Entry<Adress, DataGameAnnouncement>> iterator = foundGames.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Adress, DataGameAnnouncement> entry = iterator.next();
            if (entry.getValue().ifOffline()) {
                iterator.remove();
            }
        }
    }

    public synchronized ArrayList<DataGameAnnouncement> getFoundGame()
    {
        return new ArrayList<>(foundGames.values());
    }

    //private ArrayList<DataGameAnnouncement> foundGames = Collections.synchronizedSet(new HashSet<>());
    //private List<DataGameAnnouncement> foundGames = Collections.synchronizedList(new ArrayList<>());
    private Map<Adress, DataGameAnnouncement> foundGames = new HashMap<>();
}
