package org.example.BusinessLogic.Network;

import com.google.protobuf.InvalidProtocolBufferException;
import me.ippolitov.fit.snakes.SnakesProto;
import org.example.BusinessLogic.Network.Data.Adress;
import org.example.BusinessLogic.Network.Data.DataGameMessage;
import org.example.BusinessLogic.Network.Data.DataServer;
import org.example.BusinessLogic.Network.Data.Receiver;

import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server implements Runnable
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
            pingSender=new Thread(new PingSender(socket,socketLock,dataServer));
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

        if (dataGameMessage!=null)
        {
            checkToDo(dataGameMessage);

            socketLock.lock();
            try
            {
                DatagramPacket packet=createPacket(dataGameMessage);
                socket.send(packet);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally
            {
                socketLock.unlock();
            }
        }
    }

    private void sendMulticast()
    {
        if (Math.abs(System.currentTimeMillis()-timeMulticast)>=LIMIT_MULTICAST)
        {
            socketLock.lock();
            try
            {
                DatagramPacket packet =createMulticastPacket();
                socket.send(packet);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally
            {
                socketLock.unlock();
                timeMulticast=System.currentTimeMillis();
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

            try
            {
                socket.receive(packet);
                dataServer.updateTimeReceiver(packet.getAddress().getHostAddress(), packet.getPort());

                SnakesProto.GameMessage message=parseFrom(packet);

                processMessage(message, new Adress(packet.getAddress().getHostAddress(),packet.getPort()));
                sendAck(message, new Adress(packet.getAddress().getHostAddress(),packet.getPort()));
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
    private void processRoleChangeMsg(Adress adressFrom,SnakesProto.GameMessage message)
    {
        if(message.getRoleChange().hasReceiverRole() && message.getRoleChange().getReceiverRole()== SnakesProto.NodeRole.DEPUTY)
        {
            System.out.println("i have became deputy!");
            dataServer.setDeputy(true);
        }
        else if (message.getRoleChange().hasSenderRole() && message.getRoleChange().getSenderRole()== SnakesProto.NodeRole.VIEWER)
        {
            dataServer.setWantetViewer(adressFrom);
        }
    }
    private void processErrorMsg(Adress adressFrom, SnakesProto.GameMessage message)
    {
        System.err.println(" (" + message.getError().getErrorMessage()+") FROM "+adressFrom.getIp()+" : "+adressFrom.getPort());
    }

    private void processSteerMsg(Adress adressFrom,SnakesProto.GameMessage gameMessage)
    {
        if (dataServer.getServerRole()== SnakesProto.NodeRole.MASTER) {
            dataServer.addChgPlDir(gameMessage, adressFrom);
        }
    }

    private void processStateMsg(SnakesProto.GameMessage gameState)
    {
        if (dataServer.getServerRole()!= SnakesProto.NodeRole.MASTER &&
                gameState.getMsgSeq()>msgSeqState)
        {
            dataServer.setGameState(gameState.getState().getState());
            msgSeqState=gameState.getMsgSeq();
        }
    }
    private void processAckMsg(Adress adressFrom, SnakesProto.GameMessage message)
    {
        if (message.hasReceiverId())
        {
            dataServer.setJoin(message);
        }
        dataServer.deleteGameMessage(adressFrom,message);
    }

    private void processJoinMsg(Adress adressFrom, SnakesProto.GameMessage message)
    {
        if (dataServer.getServerRole()== SnakesProto.NodeRole.MASTER)
        {
            dataServer.addAccededPlayers(new AccededPlayer(adressFrom,
                    message.getJoin().getPlayerName(),
                    message.getJoin().getPlayerType(),
                    message.getJoin().getRequestedRole(),
                    message.getMsgSeq()));
        }
    }



    private void processMessage(SnakesProto.GameMessage message, Adress adressFrom)
    {
        switch (message.getTypeCase())
        {
            case JOIN -> processJoinMsg(adressFrom,message);
            case ACK -> processAckMsg(adressFrom, message);
            case STATE -> processStateMsg(message);
            case STEER -> processSteerMsg(adressFrom,message);
            case ERROR -> processErrorMsg(adressFrom, message);
            case ROLE_CHANGE -> processRoleChangeMsg(adressFrom,message);
        }
    }

    private void sendAck(SnakesProto.GameMessage message, Adress adressFrom)
    {
        if (message.getTypeCase()!= SnakesProto.GameMessage.TypeCase.JOIN && message.getTypeCase()!= SnakesProto.GameMessage.TypeCase.ACK && message.getTypeCase()!= SnakesProto.GameMessage.TypeCase.DISCOVER &&
                message.getTypeCase()!= SnakesProto.GameMessage.TypeCase.ANNOUNCEMENT)
        {
            try
            {
                Receiver receiver = dataServer.getReceiver(adressFrom);

                if (receiver.getSenderId()>0) {
                    dataServer.putGameMessages(new DataGameMessage(adressFrom,
                            MessageBuilder.getAckMsg(receiver.getSenderId(),
                                    receiver.getReceiverId(),message.getMsgSeq())));
                }
                else {
                    dataServer.putGameMessages(new DataGameMessage(adressFrom,
                            MessageBuilder.getAckMsg(message)));
                }
            }
            catch (NullPointerException e)
            {
                dataServer.putGameMessages(new DataGameMessage(adressFrom,
                        MessageBuilder.getAckMsg(message)));
                //System.err.println("NOT SUCH RECEIVER!");
            }
        }
    }

    private DatagramPacket createPacket(DataGameMessage dataGameMessage) throws UnknownHostException
    {
        byte[] message = dataGameMessage.getGameMessage().toByteArray();
        InetAddress receiverAddress = InetAddress.getByName(dataGameMessage.getIp());
        return new DatagramPacket(message, message.length, receiverAddress, dataGameMessage.getPort());
    }

    private DatagramPacket createMulticastPacket() throws UnknownHostException
    {
        InetAddress group = InetAddress.getByName(MULTICAST_GROUP);
        SnakesProto.GameMessage gameMessage = MessageBuilder.getAnnouncementMsg(dataServer, dataServer.pollMsgSeq());
        byte[] message = gameMessage.toByteArray();
        return new DatagramPacket(message, message.length, group, MULTICAST_PORT);
    }

    private SnakesProto.GameMessage parseFrom(DatagramPacket packet) throws InvalidProtocolBufferException
    {
        byte[] data = new byte[packet.getLength()];
        System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
        return SnakesProto.GameMessage.parseFrom(data);
    }

    private void checkToDo(DataGameMessage dataGameMessage)
    {
        if (dataGameMessage.getGameMessage().getTypeCase()== SnakesProto.GameMessage.TypeCase.JOIN)
        {
            dataServer.setMaster(dataGameMessage.getIp(),dataGameMessage.getPort());
        }
        else if (dataGameMessage.getGameMessage().getTypeCase()== SnakesProto.GameMessage.TypeCase.ACK)
        {
            dataServer.deleteGameMessage(new Adress(dataGameMessage.getIp(),dataGameMessage.getPort()),dataGameMessage.getGameMessage());
        }
    }

    private NetworkInterface findNetworkInterface(String networkName) throws SocketException {
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


//                    if (message.getTypeCase()!= SnakesProto.GameMessage.TypeCase.ACK
//                        && message.getTypeCase()!= SnakesProto.GameMessage.TypeCase.PING
////                        //&& message.getTypeCase()!= SnakesProto.GameMessage.TypeCase.STATE
//                ) {
//    System.out.println("[SERVER] Received {" + message.getTypeCase() + "} msgSeq=" + message.getMsgSeq()+" | send through "+Math.abs(System.currentTimeMillis()-timeReceive));
//    // System.out.println();
//    timeReceive=System.currentTimeMillis();
//}



//    //                System.out.println("send through "+Math.abs(System.currentTimeMillis()-timeSend));
////                timeSend=System.currentTimeMillis();
//
//                if (dataGameMessage.getGameMessage().getTypeCase()!= SnakesProto.GameMessage.TypeCase.ACK
////                       // &&  dataGameMessage.getGameMessage().getTypeCase()!= SnakesProto.GameMessage.TypeCase.STATE
//                ) {
//    System.out.println("[SERVER] send {" + dataGameMessage.getGameMessage().getTypeCase() + "} msgSeq=" + dataGameMessage.getGameMessage().getMsgSeq()+" | send through "+Math.abs(System.currentTimeMillis()-timeSend));
//    timeSend=System.currentTimeMillis();
//}

    private MulticastSocket socket;
    private Lock socketLock;
    private final DataServer dataServer;
    private Thread pingSender;

    private long msgSeqState=-1;

    private long timeMulticast=System.currentTimeMillis();
    private static final String MULTICAST_GROUP="239.192.0.4";
    private static final int MULTICAST_PORT=9192;
    private static final int LIMIT_MULTICAST=1000;
    private static final int SIZE_RECEIVE_DATA=1024;
}
