package org.example.BusinessLogic.GameData.Player;

import me.ippolitov.fit.snakes.SnakesProto;

public interface Player
{
    public String getName();
    public int getId();
    public int getScore();
    public SnakesProto.NodeRole getRole();
    public SnakesProto.PlayerType getType();
    public String getIpAddress();
    public int getPort();

}
