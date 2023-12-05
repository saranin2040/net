package org.example.BusinessLogic.Network;

import me.ippolitov.fit.snakes.SnakesProto;
import org.example.BusinessLogic.*;
import org.example.BusinessLogic.Network.Data.Adress;
import org.example.BusinessLogic.Network.Data.DataGameMessage;
import org.example.BusinessLogic.Network.Data.DataServer;
import org.example.BusinessLogic.Network.Data.DataTimeSend;

import java.io.IOException;
import java.net.*;
import java.nio.channels.*;
import java.util.Collections;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server implements Runnable
{
    public Server(DataServer dataServer, Role role)
    {
        this.role=role;
        this.dataServer = dataServer;

        if (role==Role.NORMAL)
        {
            dataServer.setTypeRequest(TypeRequest.JOIN);
        }

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
            pingSender=new Thread(new PingSender(socket,socketLock,dataTimeSend,dataServer));
            pingSender.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public void run()
    {
        while (true)
        {
            sendData();
            receiveData();
            dataServer.deleteOfflineReceivers();
        }
    }

    private void sendData()
    {
        if (role==Role.MASTER) {
            sendMulticast();
        }

        DataGameMessage dataGameMessage=dataServer.pollGameMessage();


        if (dataGameMessage!=null) {
            if (dataGameMessage.getGameMessage().getTypeCase()== SnakesProto.GameMessage.TypeCase.JOIN)
            {
                dataServer.setMaster(dataGameMessage.getIp(),dataGameMessage.getPort());
            }
            try {
                byte[] message = dataGameMessage.getGameMessage().toByteArray();

                InetAddress receiverAddress = InetAddress.getByName(dataGameMessage.getIp());
                DatagramPacket packet = new DatagramPacket(message, message.length, receiverAddress, dataGameMessage.getPort());

                socketLock.lock();
                socket.send(packet);

                if (dataGameMessage.getGameMessage().getTypeCase()!= SnakesProto.GameMessage.TypeCase.ACK) {
                    System.out.println("[SERVER] send {" + dataGameMessage.getGameMessage().getTypeCase() + "}");
                }

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
                //System.out.println("SEND");

                InetAddress group = InetAddress.getByName(MULTICAST_GROUP);

                SnakesProto.GameMessage gameMessage = MessageBuilder.getAnnouncementMsg(dataServer, dataServer.getMsgSeq());
                byte[] message = gameMessage.toByteArray();

                DatagramPacket packet = new DatagramPacket(message, message.length, group, MULTICAST_PORT);

                socketLock.lock();
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
                    case STEER -> processSteerMsg(message,packet.getAddress().getHostAddress());
                    case ERROR -> processErrorMsg(packet.getAddress().getHostAddress(), packet.getPort(), message);
                    case ROLE_CHANGE -> processRoleChangeMsg(packet.getAddress().getHostAddress(), packet.getPort(),message);
                }

                if (message.getTypeCase()!= SnakesProto.GameMessage.TypeCase.JOIN && message.getTypeCase()!= SnakesProto.GameMessage.TypeCase.ACK && message.getTypeCase()!= SnakesProto.GameMessage.TypeCase.DISCOVER &&
                        message.getTypeCase()!= SnakesProto.GameMessage.TypeCase.ANNOUNCEMENT)
                {
                    dataServer.putGameMessages(new DataGameMessage(packet.getAddress().getHostAddress(),packet.getPort(),
                            MessageBuilder.getAckMsg(message)));
                }

                if (message.getTypeCase()!= SnakesProto.GameMessage.TypeCase.ACK) {
                    System.out.println("[SERVER] Received {" + message.getTypeCase() + "} msgSeq=" + message.getMsgSeq());
                }

                dataServer.updateTimeReceiver(packet.getAddress().getHostAddress(), packet.getPort());
            }
            catch (SocketTimeoutException e)
            {
                //System.out.println("No data received within the timeout period. Continue with the code.");
            }

        }
        catch (SocketException e) {
            e.printStackTrace();
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

    private void processSteerMsg(SnakesProto.GameMessage gameMessage, String ip)
    {
        dataServer.addChgPlDir(gameMessage,ip);
    }

    private void processStateMsg(SnakesProto.GameMessage gameState)
    {
        if (role!=Role.MASTER)
        {
            if (gameState.getMsgSeq()>msgSeqState) {
                dataServer.setGameState(gameState.getState().getState());
                msgSeqState=gameState.getMsgSeq();
            }
        }
    }
    private void processAckMsg(String ip,int port, SnakesProto.GameMessage message)
    {
        if (message.hasReceiverId()) {
            dataServer.setJoin(message);
        }
        dataServer.deleteGameMessage(ip,port,message);
    }

    private void processJoinMsg(DatagramPacket packet, SnakesProto.GameMessage message)
    {
        if (role==Role.MASTER) {
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

    MulticastSocket socket;

    private long msgSeqState=-1;

    private long timeMulticast=System.currentTimeMillis();
    private Role role;
    private DataServer dataServer;
    private DataTimeSend dataTimeSend=new DataTimeSend();
    private Thread pingSender;

    private Lock socketLock;

    private static final String MULTICAST_GROUP="239.192.0.4";
    private static final int MULTICAST_PORT=9192;
    private static final int LIMIT_MULTICAST=1000;
    private static final int SIZE_RECEIVE_DATA=1024;
}
