package org.example.BusinessLogic.Network.Data;

import me.ippolitov.fit.snakes.SnakesProto;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Receiver
{
    public Receiver(String ip, int port,int senderId,int receiverId)
    {
        this.ip=ip;
        this.port=port;
        this.receiverId=receiverId;
        this.senderId=senderId;
    }

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
                if (message!=null) {

                    if (message.canSend()) {
                        updateTimeSend();
                        return message;
                    }
                }
            }
        }
        return null;
    }

    public void redirection(Adress adress)
    {
        this.ip=adress.getIp();
        this.port=adress.getPort();

//        for (DataGameMessage dataGameMessage:gameMessages)
//        {
//            dataGameMessage.setIp(ip);
//            dataGameMessage.setPort(port);
//        }
    }

    public void putGameMessage(DataGameMessage message)
    {
        if (message!=null)
        {
            gameMessagesSeq.offer(message.getGameMessage().getMsgSeq());
            gameMessages.put(message.getGameMessage().getMsgSeq(),message);
        }
    }

    public void putGameMessage(DataGameMessage message,int senderId,int receiverId)
    {
        if (message!=null)
        {
            gameMessagesSeq.offer(message.getGameMessage().getMsgSeq());
            gameMessages.put(message.getGameMessage().getMsgSeq(),message);
        }

        this.senderId=senderId;
        this.receiverId=receiverId;
    }

    public void deleteGameMessage(SnakesProto.GameMessage messageNeeded)
    {
        if (gameMessages.get(messageNeeded.getMsgSeq())!=null &&  gameMessages.get(messageNeeded.getMsgSeq()).getGameMessage().getTypeCase() == SnakesProto.GameMessage.TypeCase.JOIN)
        {
            isJoining=true;
            updateTimeReceive();
            System.err.println("connect "+getIp()+":"+getPort());
        }

        gameMessages.remove(messageNeeded.getMsgSeq());

//        Iterator<DataGameMessage> iterator = gameMessages.iterator();
//        while (iterator.hasNext()) {
//            DataGameMessage message = iterator.next();
//            if (message.getGameMessage().getMsgSeq() == messageNeeded.getMsgSeq())
//            {
//            if (messageNeeded.getTypeCase() != SnakesProto.GameMessage.TypeCase.ACK &&
//                    messageNeeded.getTypeCase() != SnakesProto.GameMessage.TypeCase.PING ) {
               // System.err.println("[DELETE] send {" + messageNeeded.getTypeCase() + "}");
//            }
//                iterator.remove();
//                break;
//            }
//        }
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
        return Math.abs( System.currentTimeMillis()-timeLastSend)>STATE_DELAY_MS*0.1 && isJoining;
    }

    public void setJoining()
    {
        isJoining=true;
    }

    private DataGameMessage shift()
    {
        Long magSeq=gameMessagesSeq.poll();

        if (magSeq != null)
        {

            DataGameMessage dataGameMessage = gameMessages.get(magSeq);
            if (dataGameMessage!=null) {

                gameMessagesSeq.offer(magSeq);

                dataGameMessage.setIp(ip);
                dataGameMessage.setPort(port);
                return dataGameMessage;
            }
        }

        return null;
    }

    public static void setStateDelayMs(int delayMs)
    {
        STATE_DELAY_MS=delayMs;
    }

    public boolean isOffline()
    {
        if (isJoining==false)
        {
            return false;
        }


        for (DataGameMessage dataGameMessage:gameMessages.values())
        {
            if (dataGameMessage.getGameMessage().getTypeCase()== SnakesProto.GameMessage.TypeCase.JOIN)
            {
                return false;
            }
        }

//        long x=Math.abs(System.currentTimeMillis()-timeLastReceive);
//        if (x>STATE_DELAY_MS*0.8)
//        {
//            int y=0;
//            //System.err.println("check delete "+(x>STATE_DELAY_MS*0.8));
//        }


       // System.out.println("check delete "+(x>STATE_DELAY_MS*0.8));

        return Math.abs(System.currentTimeMillis()-timeLastReceive)>STATE_DELAY_MS;
    }

    public int getReceiverId()
    {
        return  receiverId;
    }
    public int getSenderId()
    {
        return senderId;
    }

    public long getTimeLastReceive ()
    {
        return timeLastReceive;
    }

    private String ip;
    private int port;
    private int senderId=0;
    private int receiverId=0;
    private long timeLastSend=0;
    private long timeLastReceive=System.currentTimeMillis();
    ConcurrentHashMap<Long, DataGameMessage> gameMessages = new ConcurrentHashMap<>();
    private BlockingQueue<Long> gameMessagesSeq = new LinkedBlockingQueue<>();
    private boolean isJoining=false;

    private static int STATE_DELAY_MS=5000;
}
