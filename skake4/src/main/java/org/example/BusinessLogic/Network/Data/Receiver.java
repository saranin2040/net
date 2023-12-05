package org.example.BusinessLogic.Network.Data;

import me.ippolitov.fit.snakes.SnakesProto;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Receiver
{
    public Receiver(String ip, int port)
    {
        this.ip=ip;
        this.port=port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public DataGameMessage getDataGameMessage()
    {
        if (gameMessages.size()>0)
        {
            for (int i=0; i<gameMessages.size();i++)
            {
                DataGameMessage message = shift();

                if (message.canSend()) {
                    updateTimeSend();
                    return message;
                }
            }
        }
        return null;
    }

    public void redirection(Adress adress)
    {
        this.ip=adress.ip;
        this.port=adress.port;

        for (DataGameMessage dataGameMessage:gameMessages)
        {
            dataGameMessage.setIp(ip);
            dataGameMessage.setPort(port);
        }
    }

    public void putGameMessage(DataGameMessage message)
    {
        if (message!=null) {
            gameMessages.offer(message);
        }
    }

    public void deleteGameMessage(SnakesProto.GameMessage messageNeeded)
    {
        Iterator<DataGameMessage> iterator = gameMessages.iterator();
        while (iterator.hasNext()) {
            DataGameMessage message = iterator.next();
            if (message.getGameMessage().getMsgSeq() == messageNeeded.getMsgSeq()) {
                iterator.remove();
                break;
            }
        }
    }
    public void updateTimeSend()
    {
        timeLastSend=System.currentTimeMillis();
    }

    public void updateTimeReceive()
    {
        timeLastReceive=System.currentTimeMillis();
    }

    public boolean isNeedPing()
    {
        return Math.abs( System.currentTimeMillis()-timeLastSend)>STATE_DELAY_MS*0.1;
    }

    private DataGameMessage shift()
    {
        DataGameMessage message = gameMessages.poll();

        if (message != null) {
            gameMessages.offer(message);
        }

        return message;
    }

    public static void setStateDelayMs(int delayMs)
    {
        STATE_DELAY_MS=delayMs;
    }

    public boolean isOffline()
    {
        return Math.abs(System.currentTimeMillis()-timeLastReceive)>STATE_DELAY_MS*0.8;
    }

    private String ip;
    private int port;
    private long timeLastSend=System.currentTimeMillis();
    private long timeLastReceive=System.currentTimeMillis();
    private BlockingQueue<DataGameMessage> gameMessages = new LinkedBlockingQueue<>();

    private static int STATE_DELAY_MS=5000;
}
