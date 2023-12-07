package org.example.BusinessLogic.Network;

import me.ippolitov.fit.snakes.SnakesProto;

public class AccededPlayer
{
    public AccededPlayer(String ip, int port, String playerName, SnakesProto.PlayerType type, SnakesProto.NodeRole role,long msgSeq)
    {
        this.ip=ip;
        this.playerName=playerName;
        this.port=port;
        this.role=role;
        this.type=type;
        this.msgSeq=msgSeq;
    }
    public long msgSeq;
    public String ip;
    public int port;
    public String playerName;
    public SnakesProto.PlayerType type;
    public SnakesProto.NodeRole role;

}
