package org.example.BusinessLogic;

import me.ippolitov.fit.snakes.SnakesProto;
import org.example.BusinessLogic.Network.Data.DataGameAnnouncement;
import org.example.BusinessLogic.Network.ReceiveNeedInformation;

import java.util.*;

public class GameJoined implements GameUpdate
{
    public GameJoined(String gameName)
    {
        this.gameName=gameName;
    }

    public GameJoined(SnakesProto.GameState gameState, int playerId, DataGameAnnouncement dataGameAnnouncement)
    {
        players= gameState.getPlayers().getPlayersList();
        snakes= gameState.getSnakesList();
        //foods= gameState.getFoodsList();
        stateOrder=gameState.getStateOrder();

        field=new Field(dataGameAnnouncement.getWidth(),dataGameAnnouncement.getHeight(),dataGameAnnouncement.getCountFood());

        for (SnakesProto.GamePlayer player:players)
        {
            if (player.getId()==playerId)
            {
                mainPlayer=new PlayerJoined(player);
                break;
            }
        }
    }

    public void update(ReceiveNeedInformation receiveNeedInformation)
    {
        SnakesProto.GameState gameState=receiveNeedInformation.getGameState();

        players= gameState.getPlayers().getPlayersList();
        snakes= gameState.getSnakesList();

        field.setFood( parseFood (gameState.getFoodsList()));
        stateOrder=gameState.getStateOrder();

        for (SnakesProto.GamePlayer player:players)
        {
            if (player.getId()==mainPlayer.getId())
            {
                mainPlayer=new PlayerJoined(player);
                break;
            }
        }
    }

    private ArrayList<Coords> parseFood(List<SnakesProto.GameState.Coord> coords)
    {
        ArrayList<Coords> coordss=new ArrayList<>();
        for (SnakesProto.GameState.Coord coord:coords)
        {
            coordss.add(new Coords(coord.getX(),coord.getY()));
        }
        return coordss;
    }

    public Player getMainPlayer()
    {
        return mainPlayer;
    }

    public Player getDeputyPlayer()
    {
        for (SnakesProto.GamePlayer player:players)
        {
            if (player.getRole()== SnakesProto.NodeRole.DEPUTY)
            {
                return new PlayerJoined(player);
            }
        }
        return null;
    }
    public Player getMasterPlayer()
    {
        for (SnakesProto.GamePlayer player:players)
        {
            if (player.getRole()== SnakesProto.NodeRole.MASTER)
            {
                return new PlayerJoined(player);
            }
        }
        return null;
    }

    public int getDelayMs()
    {
        return delayMs;
    }

    public ArrayList<Snake> getSnakes()
    {
        ArrayList<Snake> snakesReturn=new ArrayList<>();

        for (SnakesProto.GameState.Snake snake:snakes)
        {
            snakesReturn.add(new SnakeJoined(snake,field.getWidth(), field.getHeight()));
        }

        return snakesReturn;
    }

    public Player getMainPlayers()
    {
        return mainPlayer;
    }
    public ArrayList<Coords> getFoodList()
    {

        return field.getFoods();
    }

    public ArrayList<Player> getPlayers()
    {
        ArrayList<Player> playersReturn=new ArrayList<>();

        for (SnakesProto.GamePlayer player:players)
        {
            playersReturn.add(new PlayerJoined(player));
        }

        return playersReturn;
    }

    public String getGameName()
    {
        return gameName;
    }
    public Field getField()
    {
        return field;
    }


    protected String gameName;
    protected Player mainPlayer;
    protected List<SnakesProto.GamePlayer> players=new ArrayList<>();
    protected List<SnakesProto.GameState.Snake> snakes=new ArrayList<>();
    //protected List<SnakesProto.GameState.Coord> foods=new ArrayList<>();
    private int stateOrder;
    protected Field field=new Field(0,0,0);
    protected int delayMs;
}
