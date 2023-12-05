package org.example.BusinessLogic;

import me.ippolitov.fit.snakes.SnakesProto;

import java.util.ArrayList;
import java.util.LinkedList;

public interface Snake
{
    public SnakesProto.Direction getDirect();
    public Coords getCoords();
    public ArrayList<Coords> getBody();
    public SnakesProto.GameState.Snake.SnakeState getState();
    public int getId();
}
