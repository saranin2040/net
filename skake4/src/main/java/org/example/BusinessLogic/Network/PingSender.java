package org.example.BusinessLogic.Network;

import org.example.BusinessLogic.Network.Data.Adress;
import org.example.BusinessLogic.Network.Data.DataGameMessage;
import org.example.BusinessLogic.Network.Data.DataServer;
import org.example.BusinessLogic.Network.Data.DataTimeSend;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

public class PingSender extends Thread
{
    public PingSender(MulticastSocket socket, Lock sockeLock, DataTimeSend dataTimeSend,DataServer dataServer)
    {
        this.socket=socket;
        this.socketLock=sockeLock;
        this.dataTimeSend=dataTimeSend;
        this.dataServer=dataServer;
    }
    public void run()
    {
        while(!Thread.interrupted())
        {
            ArrayList<Adress> dataGameMessages = dataServer.getPingReceivers();

            for (Adress adress : dataGameMessages) {
                try {
                    byte[] message = MessageBuilder.getPingMsg(dataServer.pollMsgSeq()).toByteArray();

                    InetAddress receiverAddress = InetAddress.getByName(adress.getIp());
                    DatagramPacket packet = new DatagramPacket(message, message.length, receiverAddress, adress.getPort());

                    socketLock.lock();
                    socket.send(packet);

                    //System.out.println("[PING] send {ip:" + adress.getIp() + " port: "+adress.getPort()+"}");

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    socketLock.unlock();
                }
            }
        }
    }


    private long timeMulticast=System.currentTimeMillis();
    private MulticastSocket socket;
    private Lock socketLock;
    private DataTimeSend dataTimeSend;
    private DataServer dataServer;
}
