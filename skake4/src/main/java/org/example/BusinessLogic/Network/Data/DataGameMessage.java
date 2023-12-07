package org.example.BusinessLogic.Network.Data;

import me.ippolitov.fit.snakes.SnakesProto;

public class DataGameMessage
{
    public DataGameMessage(String ip, int port, SnakesProto.GameMessage gameMessage)
    {
        this.ip=ip;
        this.port=port;
        this.gameMessage=gameMessage;
    }

    public DataGameMessage(Adress adress, SnakesProto.GameMessage gameMessage)
    {
        this.ip=adress.getIp();
        this.port=adress.getPort();
        this.gameMessage=gameMessage;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void setIp(String ip) {
        this.ip=ip;
    }

    public void setPort(int port) {
         this.port=port;
    }

    public SnakesProto.GameMessage getGameMessage() {
        return gameMessage;
    }

    public void updateTime()
    {
        timeLastSend=System.currentTimeMillis();
    }

    public static void setStateDelayMs(int delayMs)
    {
        STATE_DELAY_MS=delayMs;
    }

    public boolean canSend()
    {
        if (System.currentTimeMillis()-timeLastSend>STATE_DELAY_MS*0.1)
        {
            updateTime();
            return true;
        }
        return false;
    }

    private long timeLastSend=System.currentTimeMillis();
    private String ip;
    private int port;
    private final SnakesProto.GameMessage gameMessage;

    private static int STATE_DELAY_MS=5000;
}
