package org.example.BusinessLogic.Network.Data;

import java.util.*;

public class DataMulticastServer
{

    public synchronized void addFoundGame(DataGameAnnouncement foundGame)
    {
        //DataGameAnnouncement f=new DataGameAnnouncement("192.168.0.95","g");
        //f.equals(foundGame);

        if (!foundGames.contains(foundGame)) {
            foundGames.add(foundGame);
        }

        for (DataGameAnnouncement f:foundGames)
        {
            if (f.equals(foundGame))
            {
                f.setTime(System.currentTimeMillis());
            }
        }

        Iterator<DataGameAnnouncement> iterator = foundGames.iterator();
        while (iterator.hasNext()) {
            DataGameAnnouncement f = iterator.next();
            if (f.ifOffline()) {
                iterator.remove(); // спользуем итератор для безопасного удаления
            }
        }

    }

    public synchronized ArrayList<DataGameAnnouncement> getFoundGame()
    {
        return new ArrayList<DataGameAnnouncement>(foundGames);
    }

    //private ArrayList<DataGameAnnouncement> foundGames = Collections.synchronizedSet(new HashSet<>());
    private List<DataGameAnnouncement> foundGames = Collections.synchronizedList(new ArrayList<>());
}
