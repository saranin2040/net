package org.example.BusinessLogic;

import java.util.ArrayList;

public interface Game
{
    public String getGameName();
    public int getDelayMs();
    public ArrayList<Snake> getSnakes();
    public ArrayList<Coords> getFoodList();
    public ArrayList<Player> getPlayers();
    public Player getMasterPlayer();
    public Player getDeputyPlayer();
    public Player getMainPlayer();
    public Field getField();

    public ArrayList<Player> getPlayersRate();

}
