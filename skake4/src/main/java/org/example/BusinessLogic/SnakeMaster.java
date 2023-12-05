package org.example.BusinessLogic;

import me.ippolitov.fit.snakes.SnakesProto;

import java.util.*;

public class SnakeMaster implements Snake
{

    public SnakeMaster(int id, Coords coords)
    {
        body.add(new Coords(coords.x-1,coords.y));//TODO add random
        body.add(coords);

        this.id=id;
    }

    public SnakeMaster(Snake snake)
    {
        this.direct=snake.getDirect();
        this.id=snake.getId();
        this.state=snake.getState();
    }

    public void move(SnakesProto.Direction direct, int width, int height)
    {
        this.direct=direct;

        switch (this.direct)
        {
            case RIGHT : {
                body.add(new Coords((body.getLast().x + 1+width) %(width) ,body.getLast().y));
                break;
            }
            case LEFT : {
                body.add(new Coords((body.getLast().x - 1+width) %(width),body.getLast().y));
                break;
            }
            case UP : {
                body.add(new Coords(body.getLast().x,(body.getLast().y-1+height)%(height)));
                break;
            }
            case DOWN : {
                body.add(new Coords(body.getLast().x,(body.getLast().y+1+height)%(height)));
                break;
            }
        }

        lastTail=body.poll();

    }
    public void growUp()
    {
        if (lastTail!=null) {
            body.addFirst(lastTail);
        }
    }
    public void setId(int id)
    {
        this.id=id;
    }

    public SnakesProto.Direction getDirect() {
        return direct;
    }

    public Coords getCoords()
    {
        try {
            if (body != null && !body.isEmpty())
            {
                Coords coords = body.getLast();
                return body.getLast();
            }
        }
        catch(NoSuchElementException e)
        {
            e.printStackTrace();
        }
        return new Coords(-1,-1);
    }
    public ArrayList<Coords> getBody()
    {
        return new ArrayList<Coords>(body);
    }
    public SnakesProto.GameState.Snake.SnakeState getState()
    {
        return state;
    }
    public void setState(SnakesProto.GameState.Snake.SnakeState state)
    {
        this.state=state;
    }



    public int getId()
    {
        return id;
    }
    private Coords lastTail=null;
    private LinkedList<Coords> body=new LinkedList<>();

    private int id;
    SnakesProto.Direction direct=SnakesProto.Direction.RIGHT;
    private SnakesProto.GameState.Snake.SnakeState state= SnakesProto.GameState.Snake.SnakeState.ALIVE;
}
