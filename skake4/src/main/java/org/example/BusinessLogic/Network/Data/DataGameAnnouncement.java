package org.example.BusinessLogic.Network.Data;

import me.ippolitov.fit.snakes.SnakesProto;
import org.example.BusinessLogic.Player;
import org.example.BusinessLogic.PlayerJoined;
import org.example.BusinessLogic.PlayerMaster;

import java.util.ArrayList;

public class DataGameAnnouncement
{

//    private class FoundPlayer
//    {
//        private String name;
//        int id;
//        String ip;
//        int port;
//        Role role;
//        PlayersType type;
//        int score;
//    }

    public DataGameAnnouncement(String ip, int port, String gameName, int width, int height, int countFood, SnakesProto.GamePlayers players,int delayMs) {
        this.ip = ip;
        this.gameName = gameName;
        this.width = width;
        this.height = height;
        this.countFood = countFood;
        this.port=port;
        this.delayMs=delayMs;

        for (SnakesProto.GamePlayer player:players.getPlayersList())
        {
            this.players.add(new PlayerJoined(player));
            if(player.getRole()== SnakesProto.NodeRole.MASTER)
            {
                masterName=player.getName();
            }
        }
    }

    public DataGameAnnouncement(String ip, String gameName) {
        this.ip = ip;
        this.gameName = gameName;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }


        DataGameAnnouncement other = (DataGameAnnouncement) obj;
        return this.ip.equals(other.ip) && this.gameName.equals(other.gameName);
    }

    public String getGameName()
    {
        return gameName;
    }

    public void setTime(long time)
    {
        timeMulticast=time;
    }
    public long getTime()
    {
        return timeMulticast;
    }

    public String getIp()
    {
        return ip;
    }
    public int getPort()
    {
        return port;
    }

    public String getMasterName()
    {
        return masterName;
    }

    public int getWidth()
    {
        return  width;
    }

    public int getHeight()
    {
        return  height;
    }

    public int getCountFood()
    {
        return countFood;
    }

    public ArrayList<Player> getplayers()
    {
        return players;
    }
    public int getDelayMs()
    {
        return delayMs;
    }

    private String ip;
    private String gameName;
    private int width;
    private int height;
    private int countFood;
    private int delayMs;

    private int port;
    String masterName;

    private long timeMulticast=System.currentTimeMillis();

    ArrayList<Player> players=new ArrayList<>();
}
