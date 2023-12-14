package org.example.BusinessLogic.Network.Data;

import me.ippolitov.fit.snakes.SnakesProto;
import org.example.BusinessLogic.GameData.Game;
import org.example.BusinessLogic.GameData.GameJoined;
import org.example.BusinessLogic.Network.AccededPlayer;
import org.example.BusinessLogic.GameData.Player.Player;

import java.util.*;
import java.util.concurrent.*;

public class DataServer
{
    public DataServer(int delayMs)
    {
        //System.err.println("NEW DATA");

        Receiver.setStateDelayMs(delayMs);
        DataGameMessage.setStateDelayMs(delayMs);
        STATE_DELAY_MS=delayMs;
    }

    public DataServer (Game game)
    {
        //System.err.println("NEW DATA");

        Receiver.setStateDelayMs(game.getDelayMs());
        DataGameMessage.setStateDelayMs(game.getDelayMs());
        STATE_DELAY_MS=game.getDelayMs();
        this.gameName=game.getGameName();
        this.players=game.getPlayers();
    }
    public synchronized HashMap<Adress, SnakesProto.GameMessage> getChgPlDir()
    {
        HashMap<Adress, SnakesProto.GameMessage> ret= new HashMap<>(changedPlayerDirection);
        changedPlayerDirection.clear();
        return ret;
    }
    public synchronized void addChgPlDir(SnakesProto.GameMessage gameMessage,Adress adressFrom)
    {
        if ( !changedPlayerDirection.containsKey(adressFrom)
                || changedPlayerDirection.containsKey(adressFrom)
                && changedPlayerDirection.get(adressFrom).getMsgSeq()<gameMessage.getMsgSeq()) {
            changedPlayerDirection.put(adressFrom, gameMessage);
        }
    }
    public List<Player> getPlayers()
    {
        return players;
    }
    public synchronized void setPlayers(ArrayList<Player> players)
    {
        this.players=players;
    }

    public String getGameName()
    {
        return gameName;
    }

    public synchronized void setDataGameConfig(DataGameConfig dataGameConfig)
    {
        this.dataGameConfig=dataGameConfig;
    }

    public synchronized DataGameConfig getDataGameConfig()
    {
        return dataGameConfig;
    }

    public boolean getCanJoin()
    {

        return true; //TODO как понимать можно ли присоединиться?
    }


    public void updateDataAnnouncment(Game game)
    {
        this.gameName=game.getGameName();
        this.players=game.getPlayers();
        setPlayers(game.getPlayers());

        setDataGameConfig(new DataGameConfig(game.getField().getWidth(),
                game.getField().getHeight(),
                game.getField().getMaxFoods(),
                game.getDelayMs()));
    }

    public void setGameState(SnakesProto.GameState gameState)
    {
        futureGameState.complete(gameState);
    }

    public SnakesProto.GameState getGameState()
    {
        try
        {
            SnakesProto.GameState gameState = futureGameState.get((int)(STATE_DELAY_MS*0.8),TimeUnit.MILLISECONDS);
            futureGameState = new CompletableFuture<>();

            return gameState;
        }
        catch (InterruptedException | ExecutionException | TimeoutException e)
        {
            return null;
        }
    }

    public SnakesProto.GameState reallyGetGameState()
    {
        try
        {
            SnakesProto.GameState gameState = futureGameState.get();
            futureGameState = new CompletableFuture<>();

            return gameState;
        }
        catch (InterruptedException | ExecutionException e)
        {
            return null;
        }
    }

    public SnakesProto.GameMessage isJoin()
    {
        try
        {
            return futureCanJoin.get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public void setJoin(SnakesProto.GameMessage message)
    {
        futureCanJoin.complete(message);
    }

    public synchronized long pollMsgSeq()
    {
        msgSeq++;
        return msgSeq;
    }

    public synchronized ArrayList<AccededPlayer> getAccededPlayers()
    {
        if (accededPlayers.size()>0) {
            ArrayList<AccededPlayer> temp = new ArrayList<>(accededPlayers);
            accededPlayers.clear();
            return temp;
        }
        return null;
    }

    public synchronized void addAccededPlayers(AccededPlayer accededPlayer)
    {
        accededPlayers.add(accededPlayer);
    }

    public GameJoined getGame(int playerId, DataGameAnnouncement dataGameAnnouncement)
    {
        SnakesProto.GameState gameState = reallyGetGameState();
        if (gameState==null)
        {
            return null;
        }
        return new GameJoined(gameState,playerId,dataGameAnnouncement);
    }

    public DataGameMessage pollGameMessage()
    {
        if (receivers.size()>0)
        {
            Adress adress = receiversAdress.poll();

            if (adress != null)
            {
                Receiver receiver =  receivers.get(adress);

               // if (!receiver.isOffline()) {
                    receiversAdress.offer(adress);
                    return receiver.getDataGameMessage();
                //}
//                receivers.remove(adress);
//                offlineReceivers.offer(adress);
//                System.err.println("disconnect "+receiver.getIp()+":"+receiver.getPort());
            }
        }
        return null;
    }

    public void deleteGameMessage(Adress adressFrom,SnakesProto.GameMessage message)
    {
        Receiver receiver = receivers.get(adressFrom);

//        if (message.getTypeCase() != SnakesProto.GameMessage.TypeCase.ACK &&
//                message.getTypeCase() != SnakesProto.GameMessage.TypeCase.PING ) {
//            System.err.println("[DELETE] send {" + message.getTypeCase() + "}");
//        }

        if (receiver != null)
        {

            if (message.getTypeCase()== SnakesProto.GameMessage.TypeCase.JOIN)
            System.err.println(message.getTypeCase());
            receiver.deleteGameMessage(message);
        }
//
//        for (Receiver receiver:receivers)
//        {
//            if (receiver.getIp().equals(adressFrom.getIp())&&receiver.getPort()==adressFrom.getPort())
//            {
//                receiver.deleteGameMessage(message);
//                break;
//            }
//        }
    }

    public void updateTimeReceiver(String ip,int port)
    {
        Receiver receiver = receivers.get(new Adress(ip,port));
        if (receiver != null) {
            receiver.updateTimeReceive();
        }
//
//        for (Receiver receiver:receivers)
//        {
//            if (receiver.getIp().equals(ip)&&receiver.getPort()==port)
//            {
//                receiver.updateTimeReceive();
//                break;
//            }
//        }
    }

    public boolean putGameMessagesAck(DataGameMessage dataGameMessage,int senderId,int receiverId)
    {
        if (!offlineReceivers.contains(new Adress(dataGameMessage.getIp(),dataGameMessage.getPort())))
        {//TODO more optimizate by mater and deputy you now

            Receiver receiver2 = receivers.get(new Adress(dataGameMessage.getIp(),dataGameMessage.getPort()));
            if (receiver2 != null) {
                receiver2.putGameMessage(dataGameMessage,senderId,receiverId);
                return true;
            }

//            for (Receiver receiver : receivers) {
//                if (receiver.getIp().equals(dataGameMessage.getIp()) && receiver.getPort() == dataGameMessage.getPort()) {
//                    receiver.putGameMessage(dataGameMessage);
//                    return true;
//                }
//            }

            Receiver receiver = new Receiver(dataGameMessage.getIp(), dataGameMessage.getPort());
            receiver.putGameMessage(dataGameMessage,senderId,receiverId);
            receivers.put(new Adress(dataGameMessage.getIp(), dataGameMessage.getPort()),receiver);
            receiversAdress.offer(new Adress(dataGameMessage.getIp(), dataGameMessage.getPort()));
            return true;
        }
        return false;
    }

    public boolean putGameMessages(DataGameMessage dataGameMessage)
    {
        if (!offlineReceivers.contains(new Adress(dataGameMessage.getIp(),dataGameMessage.getPort())))
        {//TODO more optimizate by mater and deputy you now

            Receiver receiver2 = receivers.get(new Adress(dataGameMessage.getIp(),dataGameMessage.getPort()));
            if (receiver2 != null) {
                receiver2.putGameMessage(dataGameMessage);
                return true;
            }

//            for (Receiver receiver : receivers) {
//                if (receiver.getIp().equals(dataGameMessage.getIp()) && receiver.getPort() == dataGameMessage.getPort()) {
//                    receiver.putGameMessage(dataGameMessage);
//                    return true;
//                }
//            }

            Receiver receiver = new Receiver(dataGameMessage.getIp(), dataGameMessage.getPort());
            receiver.putGameMessage(dataGameMessage);
            receivers.put(new Adress(dataGameMessage.getIp(), dataGameMessage.getPort()),receiver);
            receiversAdress.offer(new Adress(dataGameMessage.getIp(), dataGameMessage.getPort()));
            return true;
        }
        return false;
    }

    public ArrayList<Adress> getPingReceivers()
    {
        ArrayList<Adress> adresses=new ArrayList<>();

        for (Receiver receiver:receivers.values())
        {
            if (receiver.isNeedPing())
            {
                adresses.add(new Adress(receiver.getIp(), receiver.getPort()));
            }
        }
        return adresses;
    }

    public void setjoining(Adress adress)
    {
        Receiver receiver2 = receivers.get(adress);
        if (receiver2 != null) {
            receiver2.setJoining();
        }

//        for (Receiver receiver : receivers) {
//            if (receiver.getIp().equals(adress.getIp()) && receiver.getPort() == adress.getPort()) {
//                //receiver.setJoining();
//                return;
//            }
//        }
    }

    public void deleteReceiver(Adress adress)
    {

    }

    public void deleteOfflineReceivers()
    {
        Iterator<Adress> iterator = receiversAdress.iterator();
        while (iterator.hasNext())
        {
            Adress adress = iterator.next();
            if (receivers.get(adress).isOffline())
            {
                offlineReceivers.offer(adress);
                System.err.println("disconnect "+adress.getIp()+":"+adress.getPort());
                iterator.remove();
            }
        }
    }

    public ArrayList<Adress> getOfflineReceivers()
    {
        ArrayList<Adress> temp = new ArrayList<>(offlineReceivers);
        offlineReceivers.clear();//TODO ...
        return temp;
    }

    public boolean isOffline(Adress adress)
    {
        return offlineReceivers.contains(adress);
    }

    public void setWantetViewer(Adress adress)
    {
        wantedViewers.add(adress);
    }

    public ArrayList<Adress> getWantetViewers()
    {
        return new ArrayList<> (wantedViewers);
    }

    public void redirection(Adress fromAdress,Adress toAdress)
    {
        Receiver receiver = receivers.get(fromAdress);
        if (receiver != null) {
            receiver.redirection(toAdress);
        }
//        for (Receiver receiver:receivers)
//        {
//            if (receiver.getIp().equals(fromAdress.getIp())&&receiver.getPort()==fromAdress.getPort())
//            {
//                receiver.redirection(toAdress);
//            }
//        }
    }

    public Receiver getReceiver(Adress adress)
    {
        return receivers.get(adress);
//
//        for (Receiver receiver:receivers)
//        {
//            if (receiver.getIp().equals(adress.getIp())&&receiver.getPort()==adress.getPort())
//            {
//                return receiver;
//            }
//        }
//        return null;
    }

    public void setServerRole( SnakesProto.NodeRole role)
    {
        serverRole= role;
    }

    public SnakesProto.NodeRole getServerRole()
    {
        return serverRole;
    }

    public void setMaster(String ip, int port)
    {
        master=new Adress(ip,port);
    }

    public Adress getMaster()
    {
        return master;
    }
    public boolean getDeputy() {
        return deputy;
    }

    public void setDeputy(boolean deputy) {
        this.deputy = deputy;
    }

    private String gameName="my favorite game";

    private DataGameConfig dataGameConfig;

    private SnakesProto.NodeRole serverRole= SnakesProto.NodeRole.NORMAL;

    private final HashMap<Adress, SnakesProto.GameMessage> changedPlayerDirection=new HashMap<>();

    private List<Player> players = Collections.synchronizedList(new ArrayList<>());

    private int msgSeq=0;

    private final ArrayList<AccededPlayer> accededPlayers=new ArrayList<>();

    private boolean deputy=false;
    private Adress master=null;

    private CompletableFuture<SnakesProto.GameMessage> futureCanJoin = new CompletableFuture<>();
    private CompletableFuture<SnakesProto.GameState> futureGameState = new CompletableFuture<>();

    //BlockingQueue<Receiver> receivers = new LinkedBlockingQueue<>();
    ConcurrentHashMap<Adress, Receiver> receivers = new ConcurrentHashMap<>();
    BlockingQueue<Adress> offlineReceivers = new LinkedBlockingQueue<>();
    List<Adress> wantedViewers = Collections.synchronizedList(new ArrayList<>());
    BlockingQueue<Adress> receiversAdress = new LinkedBlockingQueue<>();

    public int STATE_DELAY_MS=5000;
}