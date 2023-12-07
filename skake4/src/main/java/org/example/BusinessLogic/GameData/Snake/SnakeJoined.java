package org.example.BusinessLogic.GameData.Snake;

import me.ippolitov.fit.snakes.SnakesProto;
import org.example.BusinessLogic.GameData.Coords;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SnakeJoined implements Snake
{
    public SnakeJoined(SnakesProto.GameState.Snake snake, int width, int height )
    {
        direct=snake.getHeadDirection();
        state=snake.getState();
        body=getParseBody(snake.getPointsList(),width,height);
        id=snake.getPlayerId();
    }
    public SnakesProto.GameState.Snake.SnakeState getState()
    {
        return state;
    }

    public SnakesProto.Direction getDirect() {
        return direct;
    }

    public Coords getCoords()
    {
        return body.getLast();
    }
    public ArrayList<Coords> getBody()
    {
        return new ArrayList<>(body) ;
    }
    private LinkedList<Coords> getParseBody(List<SnakesProto.GameState.Coord> newBody, int width, int height)
    {
        body=new LinkedList<>();

        ArrayList<Coords> coords=new ArrayList<>();

        coords.add(new Coords(newBody.get(0).getX(),newBody.get(0).getY()));

        for (int i=1;i<newBody.size();i++)
        {
            int xm=coords.get(coords.size()-1).x;
            int ym=coords.get(coords.size()-1).y;

            for (int j=1; j<=newBody.get(i).getX();j++)
            {
                coords.add(new Coords(offset(xm,j,width),ym));
            }

            for (int j=1; j<=newBody.get(i).getY();j++)
            {
                coords.add(new Coords(xm,offset(ym,j,height)));
            }

            for (int j=-1; j>=newBody.get(i).getX();j--)
            {
                coords.add(new Coords(offset(xm,j,width),ym));
            }

            for (int j=-1; j>=newBody.get(i).getY();j--)
            {
                coords.add(new Coords(xm,offset(ym,j,height)));
            }
        }

        Collections.reverse(coords);

        body.addAll(coords);

        return body;
    }

    public int getId()
    {
        return id;
    }

    private int offset(int cur,int offset,int mod)
    {
        return (cur + offset+mod) %(mod);
    }

    private int id;
    private LinkedList<Coords> body=new LinkedList<>();
    private SnakesProto.Direction direct=SnakesProto.Direction.RIGHT;
    private SnakesProto.GameState.Snake.SnakeState state;
}
