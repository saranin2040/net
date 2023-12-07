package org.example.BusinessLogic.GameData.Snake;

import me.ippolitov.fit.snakes.SnakesProto;
import org.example.BusinessLogic.GameData.Coords;

import java.util.ArrayList;

public interface Snake
{
    public SnakesProto.Direction getDirect();
    public Coords getCoords();
    public ArrayList<Coords> getBody();
    public SnakesProto.GameState.Snake.SnakeState getState();
    public int getId();
}
