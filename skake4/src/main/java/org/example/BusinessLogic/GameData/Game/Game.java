package org.example.BusinessLogic.GameData.Game;

import org.example.BusinessLogic.GameData.Coords;
import org.example.BusinessLogic.GameData.Field;
import org.example.BusinessLogic.GameData.Player.Player;
import org.example.BusinessLogic.GameData.Snake.Snake;

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
