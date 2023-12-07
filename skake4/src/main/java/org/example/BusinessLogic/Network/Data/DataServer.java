package org.example.BusinessLogic.Network.Data;

import me.ippolitov.fit.snakes.SnakesProto;
import org.example.BusinessLogic.*;
import org.example.BusinessLogic.Network.AccededPlayer;
import org.example.BusinessLogic.Network.Protect;

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
        HashMap<Adress, SnakesProto.GameMessage> ret=(HashMap<Adress, SnakesProto.GameMessage>)changedPlayerDirection.clone();
        changedPlayerDirection.clear();
        return ret;
    }
    public synchronized void addChgPlDir(SnakesProto.GameMessage gameMessage, String ip,int port)
    {
        if ( !changedPlayerDirection.containsKey(gameMessage.getSenderId())
                || changedPlayerDirection.containsKey(gameMessage.getSenderId())
                && changedPlayerDirection.get(gameMessage.getSenderId()).getMsgSeq()<gameMessage.getMsgSeq()) {
            changedPlayerDirection.put(new Adress(ip,port), gameMessage);
        }
    }
    public synchronized void setPermitSendState(boolean bool)
    {
        permitSendState=bool;
    }
    public synchronized boolean getPermitSendState()
    {
        return permitSendState;
    }
    public List<Player> getPlayers(Protect protect)
    {
//        if (protect.equals(Protect.READ))
//        {
//            List<PlayerMaster> copyListPlayers = new ArrayList<>();
//
//            for (PlayerMaster player : players) {
//                copyListPlayers.add(player.cloneSelf());
//            }
//            return copyListPlayers;
//        }

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

    public synchronized DataGameConfig getDataGameConfig(Protect protect)
    {
//        if (protect.equals(Protect.READ)) {
//                return dataGameConfig.cloneSelf();
//        }

        return dataGameConfig;
    }

    public boolean getCanJoin()
    {

        return true; //TODO как понимать можно ли присоединиться?
    }

    public synchronized boolean getNeedSend()
    {
        return needSend;
    }

    public synchronized void setNeedSend(boolean needSend)
    {
        this.needSend=needSend;
    }

    // Геттер и сеттер для ipJoinGame
    public String getIpJoinGame() {
        return ipJoinGame;
    }

    public void setIpJoinGame(String ipJoinGame) {
        this.ipJoinGame = ipJoinGame;
    }

    // Геттер и сеттер для nameJoinGame
    public String getNameJoinGame() {
        return nameJoinGame;
    }

    public void setNameJoinGame(String nameJoinGame) {
        this.nameJoinGame = nameJoinGame;
    }

    public void update(Game game)
    {
        this.gameName=game.getGameName();
        this.players=game.getPlayers();

        setPlayers(game.getPlayers());
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

    public  String getPlayerName() {
        return playerName;
    }

    public synchronized void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    // Геттер и сеттер для role
    public SnakesProto.NodeRole getRequestedRole() {
        return requestedRole;
    }

    public synchronized void setRequestedRole(SnakesProto.NodeRole role) {
        this.requestedRole = role;
    }

    public synchronized SnakesProto.GameMessage.TypeCase getTypeSend() {
        return typeSend;
    }

    public synchronized void setTypeSend(SnakesProto.GameMessage.TypeCase typeSend) {
        this.typeSend = typeSend;
    }

    public synchronized SnakesProto.GameMessage getGameMessage() {
        return gameMessage;
    }

    public synchronized void setGameMessage(SnakesProto.GameMessage gameMessage) {
        this.gameMessage = gameMessage;
    }

    public synchronized long getMsgSeq()
    {
        return msgSeq;
    }

    public synchronized void addMsgSeq() {
        this.msgSeq++;
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
            setIsNewPlayers(false);
            return temp;
        }
        return null;
    }

    public synchronized void addAccededPlayers(AccededPlayer accededPlayer)
    {
        accededPlayers.add(accededPlayer);
    }

    public synchronized boolean isNewPlayers() {
        return isNewPlayers;
    }

    // Сеттер для isNewPlayers
    public synchronized void setIsNewPlayers(boolean isNewPlayers) {
        this.isNewPlayers = isNewPlayers;
    }


    public GameJoined getGame(int playerId,DataGameAnnouncement dataGameAnnouncement)
    {
        SnakesProto.GameState gameState = getGameState();
        if (gameState==null)
        {
            return null;
        }
        return new GameJoined(gameState,playerId,dataGameAnnouncement);
    }
    public SnakesProto.Direction getDirection()
    {
        return direction;
    }

    public void setDirection(SnakesProto.Direction direction)
    {
        this.direction=direction;
    }

    public void setPort(int port)
    {
        this.port=port;
    }

    public int getPort()
    {
        return port;
    }

    public DataGameMessage pollGameMessage()
    {
        if (receivers.size()>0)
        {
            //System.out.println("receivers not zero");
            Receiver receiver = receivers.poll();

            if (receiver != null) {
                receivers.offer(receiver);
            }

            return receiver.getDataGameMessage();
        }
        return null;
    }

    public void deleteGameMessage(String ip,int port,SnakesProto.GameMessage message)
    {
        for (Receiver receiver:receivers)
        {
            if (receiver.getIp().equals(ip)&&receiver.getPort()==port)
            {
                receiver.deleteGameMessage(message);
                break;
            }
        }
    }

    public void updateTimeReceiver(String ip,int port)
    {
        for (Receiver receiver:receivers)
        {
            if (receiver.getIp().equals(ip)&&receiver.getPort()==port)
            {
                receiver.updateTimeReceive();
                break;
            }
        }
    }

    public boolean putGameMessagesAck(DataGameMessage dataGameMessage,int senderId,int receiverId)
    {
        if (!offlineReceivers.contains(new Adress(dataGameMessage.getIp(),dataGameMessage.getPort()))) {//TODO more optimizate by mater and deputy you now
            for (Receiver receiver : receivers) {
                if (receiver.getIp().equals(dataGameMessage.getIp()) && receiver.getPort() == dataGameMessage.getPort()) {
                    receiver.putGameMessage(dataGameMessage,senderId,receiverId);
                    return true;
                }
            }

            Receiver receiver = new Receiver(dataGameMessage.getIp(), dataGameMessage.getPort(),senderId,receiverId);
            receiver.putGameMessage(dataGameMessage);
            receivers.offer(receiver);
            return true;
        }
        return false;
    }

    public boolean putGameMessages(DataGameMessage dataGameMessage)
    {
        if (!offlineReceivers.contains(new Adress(dataGameMessage.getIp(),dataGameMessage.getPort()))) {//TODO more optimizate by mater and deputy you now
            for (Receiver receiver : receivers) {
                if (receiver.getIp().equals(dataGameMessage.getIp()) && receiver.getPort() == dataGameMessage.getPort()) {
                    receiver.putGameMessage(dataGameMessage);
                    return true;
                }
            }

            Receiver receiver = new Receiver(dataGameMessage.getIp(), dataGameMessage.getPort());
            receiver.putGameMessage(dataGameMessage);
            receivers.offer(receiver);
            return true;
        }
        return false;
    }

    public ArrayList<Adress> getPingReceivers()
    {
        ArrayList<Adress> adresses=new ArrayList<>();

        for (Receiver receiver:receivers)
        {
            if (receiver.isNeedPing())
            {
                adresses.add(new Adress(receiver.getIp(), receiver.getPort()));
            }
        }
        return adresses;
    }

    public void deleteOfflineReceivers()
    {
        Iterator<Receiver> iterator = receivers.iterator();
        while (iterator.hasNext())
        {
            Receiver receiver = iterator.next();
            if (receiver.isOffline())
            {
                offlineReceivers.offer(new Adress(receiver.getIp(),receiver.getPort()));
                System.err.println("disconnect "+receiver.getIp()+":"+receiver.getPort());
                iterator.remove();
            }
        }
    }

    public ArrayList<Adress> getOfflineReceivers()
    {
        ArrayList<Adress> temp = new ArrayList<>(offlineReceivers);
        offlineReceivers.clear();
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
        return new ArrayList<Adress> (wantedViewers);
    }

    public void redirection(Adress fromAdress,Adress toAdress)
    {
        for (Receiver receiver:receivers)
        {
            if (receiver.getIp().equals(fromAdress.ip)&&receiver.getPort()==fromAdress.port)
            {
                receiver.redirection(toAdress);
            }
        }
    }

    public Receiver getReceiver(String ip,int port)
    {
        for (Receiver receiver:receivers)
        {
            if (receiver.getIp().equals(ip)&&receiver.getPort()==port)
            {
                return receiver;
            }
        }
        return null;
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

    private HashMap<Adress, SnakesProto.GameMessage> changedPlayerDirection=new HashMap<>();
    private boolean permitSendState=false;

    private List<Player> players = Collections.synchronizedList(new ArrayList<Player>());
    private boolean needSend=false;
    private SnakesProto.GameMessage.TypeCase typeSend=null;
    private SnakesProto.GameMessage gameMessage=null;

    //private SnakesProto.GameState gameState=null;

    private String ipJoinGame;
    private String nameJoinGame;
    private String playerName=".";
    private SnakesProto.NodeRole requestedRole=null;
    private SnakesProto.Direction direction= SnakesProto.Direction.RIGHT;

    private int msgSeq=0;

    private ArrayList<AccededPlayer> accededPlayers=new ArrayList<>();


    //private boolean join=false;

    private boolean isNewPlayers;
    int port=0;

    private boolean deputy=false;
    private Adress master=null;

    private CompletableFuture<SnakesProto.GameMessage> futureCanJoin = new CompletableFuture<>();
    private CompletableFuture<SnakesProto.GameState> futureGameState = new CompletableFuture<>();

    BlockingQueue<Receiver> receivers = new LinkedBlockingQueue<>();
    BlockingQueue<Adress> offlineReceivers = new LinkedBlockingQueue<>();
    List<Adress> wantedViewers = Collections.synchronizedList(new ArrayList<>());

    private static int STATE_DELAY_MS=5000;
}