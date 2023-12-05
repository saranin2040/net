package org.example.BusinessLogic;

import me.ippolitov.fit.snakes.SnakesProto;

public class PlayerJoined implements Player
{
    public PlayerJoined(String ipAddress, int port)
    {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public PlayerJoined(SnakesProto.GamePlayer player)
    {
        name=player.getName();
        score=player.getScore();
        id=player.getId();
        port=player.getPort();
        ipAddress=player.getIpAddress();
        role=player.getRole();
        playersType=player.getType();
    }

    public String getName()
    {
        return name;
    }
    public int getId()
    {
        return id;
    }
    public int getScore()
    {
        return score;
    }
    public SnakesProto.NodeRole getRole()
    {
        return role;
    }
    public SnakesProto.PlayerType getType()
    {
        return playersType;
    }
    public String getIpAddress()
    {
        return ipAddress;
    }
    public int getPort()
    {
        return port;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Player other = (Player) obj;
        return this.ipAddress == other.getIpAddress() && this.port == other.getPort();
    }


    private int score;
    private String name;
    private int id;
    private String ipAddress;
    private int port;
    private SnakesProto.NodeRole role;
    private SnakesProto.PlayerType playersType;
}
