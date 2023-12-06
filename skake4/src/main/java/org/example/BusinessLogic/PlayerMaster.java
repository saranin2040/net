package org.example.BusinessLogic;

import me.ippolitov.fit.snakes.SnakesProto;
import org.example.BusinessLogic.Network.Data.Adress;

import java.util.HashMap;

public class PlayerMaster implements Player
{
    public PlayerMaster(int score, String name, int id, String ipAddress, int port, SnakesProto.NodeRole role,
                        SnakesProto.PlayerType playersType) {
        this.score = score;
        this.name = name;
        this.id = id;
        this.ipAddress = ipAddress;
        this.port = port;
        this.role = role;
        this.playersType = playersType;
    }

    public PlayerMaster(int score, String name, int id, SnakesProto.NodeRole role,
                        SnakesProto.PlayerType playersType) {
        this.score = score;
        this.name = name;
        this.id = id;
        this.role = role;
        this.playersType = playersType;
    }

    public PlayerMaster(String ipAddress, int port, String name, SnakesProto.NodeRole role, SnakesProto.PlayerType type)
    {
        this.name = name;
        this.ipAddress = ipAddress;
        this.port = port;
        this.role = role;
        this.playersType = type;
    }
    public PlayerMaster(String ipAddress, int port)
    {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public PlayerMaster(Adress adress)
    {
        this.ipAddress = adress.getIp();
        this.port = adress.getPort();
    }

    public PlayerMaster(Player player,int id)
    {
        name=player.getName();
        score=0;
        this.id=id;
        port=player.getPort();
        ipAddress=player.getIpAddress();
        role=player.getRole();
        playersType=player.getType();
    }

    public void changeRole(SnakesProto.NodeRole role)
    {
        this.role=role;
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
        if (this.ipAddress!=null && other.getIpAddress()!=null) {
            return this.ipAddress.equals(other.getIpAddress()) && this.port == other.getPort();
        }
        return false;
    }

    public PlayerMaster cloneSelf() {
        return new PlayerMaster(this.score, this.name, this.id, this.ipAddress, this.port, this.role, this.playersType);
    }

    public String getName()
    {
        return name;
    }

    public int getId()
    {
        return id;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public int getPort()
    {
        return port;
    }

    public SnakesProto.NodeRole getRole()
    {
        return role;
    }

    public SnakesProto.PlayerType getType()
    {
        return playersType;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Сеттер для id
    public void setId(int id) {
        this.id = id;
    }

    // Сеттер для ipAddress
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    // Сеттер для port
    public void setPort(int port) {
        this.port = port;
    }

    // Сеттер для role
    public void setRole(SnakesProto.NodeRole role) {
        this.role = role;
    }

    // Сеттер для playersType
    public void setType(SnakesProto.PlayerType playersType) {
        this.playersType = playersType;
    }

    public void addScore(double bonus)
    {
        score+=bonus;
    }

    public SnakesProto.Direction getDirect()
    {
        direct=predDirect;
        return direct;
    }
    public void setDirect(SnakesProto.Direction direction)
    {
        if (notAvailable.get(direction)!=this.direct) {
            this.predDirect=direction;
        }
    }

    public int getScore()
    {
        return score;
    }
    private int score;
    private String name;
    private int id=-1;
    private String ipAddress=null;
    private int port=-1;
    private SnakesProto.NodeRole role;
    private SnakesProto.PlayerType playersType;


    SnakesProto.Direction direct=SnakesProto.Direction.RIGHT;
    SnakesProto.Direction predDirect=SnakesProto.Direction.RIGHT;
    private final HashMap<SnakesProto.Direction, SnakesProto.Direction> notAvailable=new HashMap<>()
    {{
        put(SnakesProto.Direction.UP, SnakesProto.Direction.DOWN);
        put(SnakesProto.Direction.DOWN, SnakesProto.Direction.UP);
        put(SnakesProto.Direction.LEFT, SnakesProto.Direction.RIGHT);
        put(SnakesProto.Direction.RIGHT, SnakesProto.Direction.LEFT);
    }};
}
