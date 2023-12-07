package org.example.BusinessLogic.Network;

import me.ippolitov.fit.snakes.SnakesProto;
import org.example.BusinessLogic.Network.Data.Adress;
import org.example.BusinessLogic.Network.Data.DataGameMessage;
import org.example.BusinessLogic.Network.Data.DataServer;

import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server extends Thread
{
    public Server(DataServer dataServer)
    {
        this.dataServer = dataServer;



        try {
            socket = new MulticastSocket();

            NetworkInterface networkInterface = findNetworkInterface("Wi-Fi");
            //NetworkInterface networkInterface = findNetworkInterface("Radmin VPN");
            if (networkInterface == null) {
                System.err.println("Failed to find a suitable network interface");
                return;
            }

            socket.setNetworkInterface(networkInterface);

            socketLock= new ReentrantLock();
            pingSender=new PingSender(socket,socketLock,dataServer);
            pingSender.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void run()
    {
        while (!Thread.interrupted())
        {
            sendData();
            receiveData();
            dataServer.deleteOfflineReceivers();
        }
        pingSender.interrupt();
    }

    private void sendData()
    {
        if (dataServer.getServerRole()== SnakesProto.NodeRole.MASTER) {
            sendMulticast();
        }

        DataGameMessage dataGameMessage=dataServer.pollGameMessage();


        if (dataGameMessage!=null) {
            if (dataGameMessage.getGameMessage().getTypeCase()== SnakesProto.GameMessage.TypeCase.JOIN)
            {
                dataServer.setMaster(dataGameMessage.getIp(),dataGameMessage.getPort());
            }
            else if (dataGameMessage.getGameMessage().getTypeCase()== SnakesProto.GameMessage.TypeCase.ACK)
            {
                dataServer.deleteGameMessage(dataGameMessage.getIp(),dataGameMessage.getPort(),dataGameMessage.getGameMessage());
            }

            try {
                socketLock.lock();
                byte[] message = dataGameMessage.getGameMessage().toByteArray();

                InetAddress receiverAddress = InetAddress.getByName(dataGameMessage.getIp());
                DatagramPacket packet = new DatagramPacket(message, message.length, receiverAddress, dataGameMessage.getPort());


                socket.send(packet);
//                System.out.println("send through "+Math.abs(System.currentTimeMillis()-timeSend));
//                timeSend=System.currentTimeMillis();

//                if (dataGameMessage.getGameMessage().getTypeCase()!= SnakesProto.GameMessage.TypeCase.ACK
//                       // &&  dataGameMessage.getGameMessage().getTypeCase()!= SnakesProto.GameMessage.TypeCase.STATE
//                ) {
                    System.out.println("[SERVER] send {" + dataGameMessage.getGameMessage().getTypeCase() + "} msgSeq=" + dataGameMessage.getGameMessage().getMsgSeq()+" | send through "+Math.abs(System.currentTimeMillis()-timeSend));
                timeSend=System.currentTimeMillis();
                //}


            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                socketLock.unlock();
            }
        }
    }

    private void sendMulticast()
    {
        if (Math.abs(System.currentTimeMillis()-timeMulticast)>=LIMIT_MULTICAST)
        {
            try
            {
                socketLock.lock();
                //System.out.println("SEND");

                InetAddress group = InetAddress.getByName(MULTICAST_GROUP);

                SnakesProto.GameMessage gameMessage = MessageBuilder.getAnnouncementMsg(dataServer, dataServer.getMsgSeq());
                byte[] message = gameMessage.toByteArray();

                DatagramPacket packet = new DatagramPacket(message, message.length, group, MULTICAST_PORT);


                socket.send(packet);
                dataServer.addMsgSeq();

                timeMulticast=System.currentTimeMillis();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                socketLock.unlock();
            }
        }
    }

    private void receiveData()
    {
        try
        {
            socketLock.lock();
            socket.setSoTimeout(1);

            byte[] buffer = new byte[SIZE_RECEIVE_DATA];

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            SnakesProto.GameMessage message;

            try
            {

                socket.receive(packet);


                byte[] data = new byte[packet.getLength()];//TODO сократить
                System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
                message = SnakesProto.GameMessage.parseFrom(data);

                switch (message.getTypeCase())
                {
                    case JOIN -> processJoinMsg(packet,message);
                    case ACK -> processAckMsg(packet.getAddress().getHostAddress(), packet.getPort(), message);
                    case STATE -> processStateMsg(message);
                    case STEER -> processSteerMsg(message,packet.getAddress().getHostAddress(),packet.getPort());
                    case ERROR -> processErrorMsg(packet.getAddress().getHostAddress(), packet.getPort(), message);
                    case ROLE_CHANGE -> processRoleChangeMsg(packet.getAddress().getHostAddress(), packet.getPort(),message);
                }

                if (message.getTypeCase()!= SnakesProto.GameMessage.TypeCase.JOIN && message.getTypeCase()!= SnakesProto.GameMessage.TypeCase.ACK && message.getTypeCase()!= SnakesProto.GameMessage.TypeCase.DISCOVER &&
                        message.getTypeCase()!= SnakesProto.GameMessage.TypeCase.ANNOUNCEMENT)
                {
//                    if (message.getTypeCase()== SnakesProto.GameMessage.TypeCase.ERROR) {
//                        System.out.println("[SERVER] send ack for {" + message.getTypeCase() + "} msgSeq=" + message.getMsgSeq());
//                    }

                    try {
                        if (dataServer.getReceiver(packet.getAddress().getHostAddress(), packet.getPort()).getSenderId()>0) {
                            dataServer.putGameMessages(new DataGameMessage(packet.getAddress().getHostAddress(), packet.getPort(),
                                            MessageBuilder.getAckMsg(dataServer.getReceiver(packet.getAddress().getHostAddress(), packet.getPort()).getSenderId(),
                                                    dataServer.getReceiver(packet.getAddress().getHostAddress(), packet.getPort()).getReceiverId(),message.getMsgSeq())));
                        }
                        else {
                            dataServer.putGameMessages(new DataGameMessage(packet.getAddress().getHostAddress(), packet.getPort(),
                                            MessageBuilder.getAckMsg(message)));
                        }
                    }
                    catch (NullPointerException e)
                    {
                        dataServer.putGameMessages(new DataGameMessage(packet.getAddress().getHostAddress(), packet.getPort(),
                                MessageBuilder.getAckMsg(message)));
                        //System.err.println("NOT SUCH RECEIVER!");
                    }
                    //dataServer.deleteGameMessage();
                }

//                if (message.getTypeCase()!= SnakesProto.GameMessage.TypeCase.ACK
//                        && message.getTypeCase()!= SnakesProto.GameMessage.TypeCase.PING
//                        //&& message.getTypeCase()!= SnakesProto.GameMessage.TypeCase.STATE
//                ) {
                    System.out.println("[SERVER] Received {" + message.getTypeCase() + "} msgSeq=" + message.getMsgSeq()+" | send through "+Math.abs(System.currentTimeMillis()-timeReceive));
                   // System.out.println();
                    timeReceive=System.currentTimeMillis();
               // }

                dataServer.updateTimeReceiver(packet.getAddress().getHostAddress(), packet.getPort());
            }
            catch (SocketTimeoutException e)
            {
                //System.out.println("No data received within the timeout period. Continue with the code.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            socketLock.unlock();
        }
    }
    public void processRoleChangeMsg(String ip,int port,SnakesProto.GameMessage message)
    {
        if(message.getRoleChange().hasReceiverRole() && message.getRoleChange().getReceiverRole()== SnakesProto.NodeRole.DEPUTY)
        {
            System.out.println("i have became deputy!");
            dataServer.setDeputy(true);
        }
        else if (message.getRoleChange().hasSenderRole() && message.getRoleChange().getSenderRole()== SnakesProto.NodeRole.VIEWER)
        {
            dataServer.setWantetViewer(new Adress(ip,port));
        }
    }
    public void processErrorMsg(String ip,int port, SnakesProto.GameMessage message)
    {
        System.err.println(" (" + message.getError().getErrorMessage()+") FROM "+ip+" : "+port);
    }

    private void processSteerMsg(SnakesProto.GameMessage gameMessage, String ip,int port)
    {
        if (dataServer.getServerRole()== SnakesProto.NodeRole.MASTER) {
            dataServer.addChgPlDir(gameMessage, ip,port);
        }
    }

    private void processStateMsg(SnakesProto.GameMessage gameState)
    {
        if (dataServer.getServerRole()!= SnakesProto.NodeRole.MASTER)
        {
//            dataServer.o++;
//            o=dataServer.o;
//            System.out.println("server made "+dataServer.o);
            if (gameState.getMsgSeq()>msgSeqState) {
                dataServer.setGameState(gameState.getState().getState());
                msgSeqState=gameState.getMsgSeq();
            }
        }
    }
    private void processAckMsg(String ip,int port, SnakesProto.GameMessage message)
    {
        if (message.hasReceiverId()) {
            //System.err.println("IM JOINING ACK");
            dataServer.setJoin(message);
        }
        dataServer.deleteGameMessage(ip,port,message);
    }

    private void processJoinMsg(DatagramPacket packet, SnakesProto.GameMessage message)
    {
        if (dataServer.getServerRole()== SnakesProto.NodeRole.MASTER) {

//            dataServer.o++;
//            o=dataServer.o;
//            System.out.println("add acc "+dataServer.o);
            dataServer.addAccededPlayers(new AccededPlayer(packet.getAddress().getHostAddress(), packet.getPort(),
                    message.getJoin().getPlayerName(),
                    message.getJoin().getPlayerType(),
                    message.getJoin().getRequestedRole(),
                    message.getMsgSeq()));
        }
    }

    public static NetworkInterface findNetworkInterface(String networkName) throws SocketException {
        for (NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            //System.out.println("Interface: " + iface.getDisplayName());
            if (iface.isUp() && !iface.isLoopback()) {
                for (InterfaceAddress addr : iface.getInterfaceAddresses()) {
                    if (addr.getAddress() instanceof Inet4Address) {
                        if (iface.getDisplayName().contains(networkName)) {
                            return iface;
                        }
                    }
                }
            }
        }
        return null;
    }

    private MulticastSocket socket;
    private Lock socketLock;
    private final DataServer dataServer;
    private Thread pingSender;

    private long msgSeqState=-1;

    private long timeMulticast=System.currentTimeMillis();
    private long timeSend=System.currentTimeMillis();
    private long timeReceive=System.currentTimeMillis();

    private static final String MULTICAST_GROUP="239.192.0.4";
    private static final int MULTICAST_PORT=9192;
    private static final int LIMIT_MULTICAST=1000;
    private static final int SIZE_RECEIVE_DATA=1024;
}
