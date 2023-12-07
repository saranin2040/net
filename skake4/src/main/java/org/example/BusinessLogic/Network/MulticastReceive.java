package org.example.BusinessLogic.Network;

import me.ippolitov.fit.snakes.SnakesProto;
import org.example.BusinessLogic.Network.Data.DataGameAnnouncement;
import org.example.BusinessLogic.Network.Data.DataMulticastServer;

import java.io.IOException;
import java.net.*;
import java.util.Collections;

public class MulticastReceive implements Runnable
{
    public MulticastReceive(DataMulticastServer dataMulticastServer)
    {
        this.dataMulticastServer=dataMulticastServer;
    }
    public void run()
    {
        receiveMulticast();
    }

    public void receiveMulticast()
    {
        try {
            // Создаем сокет для мультикаст-группы
            InetAddress group = InetAddress.getByName(MULTICAST_GROUP);
            MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT);

            // Находим подходящий сетевой интерфейс
            NetworkInterface networkInterface = findNetworkInterface("Wi-Fi");
            //NetworkInterface networkInterface = findNetworkInterface("Radmin VPN");
            if (networkInterface == null) {
                System.err.println("Failed to find a suitable network interface");
                return;
            }

            SocketAddress socketAddress = new InetSocketAddress(group, MULTICAST_PORT);

            multicastSocket.joinGroup(socketAddress, networkInterface);

            //multicastSocket.joinGroup(group);

            // Создаем буфер для приема данных
            byte[] buffer = new byte[1024];

            int i=0;

            while (true)
            {
                i++;

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);


                multicastSocket.receive(packet);

                //if(!InetAddress.getLocalHost().equals(packet.getAddress())) {


                //System.out.println("[MULTICAST] receive" + packet.getAddress());


                    byte[] data = new byte[packet.getLength()];
                    System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
                    SnakesProto.GameMessage message = SnakesProto.GameMessage.parseFrom(data);

                    //String message = new String(packet.getData(), 0, packet.getLength());
                    //System.out.println(i+". name game: " + message.getAnnouncement().getGames(0).getGameName());

                    dataMulticastServer.addFoundGame(new DataGameAnnouncement(packet.getAddress().getHostAddress(),
                            packet.getPort(),
                            message.getAnnouncement().getGames(0).getGameName(),
                            message.getAnnouncement().getGames(0).getConfig().getWidth(),
                            message.getAnnouncement().getGames(0).getConfig().getHeight(),
                            message.getAnnouncement().getGames(0).getConfig().getFoodStatic(),
                            message.getAnnouncement().getGames(0).getPlayers(),
                            message.getAnnouncement().getGames(0).getConfig().getStateDelayMs()));
                }
            //}
        } catch (IOException e) {
            e.printStackTrace();
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
//    private final String MULTICAST_GROUP="239.192.0.4";
//    private final int MULTICAST_PORT=9192;

    private final String MULTICAST_GROUP="239.192.0.4";
    private final int MULTICAST_PORT=9192;
    DataMulticastServer dataMulticastServer;

}
