package org.example.BusinessLogic.Network;

import me.ippolitov.fit.snakes.SnakesProto;
import org.example.BusinessLogic.GameData.Game.Game;
import org.example.BusinessLogic.GameData.Game.GameJoined;
import org.example.BusinessLogic.GameData.Game.GameMaster;
import org.example.BusinessLogic.Network.Data.*;
import org.example.BusinessLogic.GameData.Player.Player;
import org.example.BusinessLogic.GameData.Player.PlayerMaster;

import java.util.*;

public class Network implements ReceiveNeedInformation
{
    public Network()
    {

    }

    public boolean isDataServer()
    {
        return dataServer!=null;
    }

    public void exit()
    {
        if (server!=null)
        {
            System.out.println("oh?");
            server.interrupt();
            server=null;
            dataServer = null;
        }
    }
    public void setServerMaster(Game game)
    {
        dataServer.update(game);
        dataServer.setDataGameConfig(new DataGameConfig(game.getField().getWidth(),
                game.getField().getHeight(),
                game.getField().getMaxFoods(),
                game.getDelayMs()));

        dataServer.setServerRole(SnakesProto.NodeRole.MASTER);
    }

    public void startMasterServer(Game game)
    {
        if (dataServer==null) {
            dataServer = new DataServer(game);
        }

        dataServer.update(game);
        dataServer.setDataGameConfig(new DataGameConfig(game.getField().getWidth(),
                game.getField().getHeight(),
                game.getField().getMaxFoods(),
                game.getDelayMs()));

        dataServer.setServerRole(SnakesProto.NodeRole.MASTER);

        if (server==null)
        {
            server = new Server(dataServer);
            server.start();
            serverWork=true;
        }
    }
    public GameJoined startNormalServer(String ip, String gameName, String playerName)
    {
        DataGameAnnouncement dataGameAnnouncement = getAnForIp(ip,gameName);

        if (dataServer==null) {
            dataServer = new DataServer(dataGameAnnouncement.getDelayMs());
        }

        dataServer.putGameMessages(new DataGameMessage(ip,dataGameAnnouncement.getPort(),
                MessageBuilder.getJoinMsg(gameName,playerName, SnakesProto.NodeRole.NORMAL,dataServer.pollMsgSeq())));

        dataServer.setServerRole(SnakesProto.NodeRole.NORMAL);

        if (server==null)
        {
            server = new Server(dataServer);
            server.start();
        }

        SnakesProto.GameMessage ackMsg = dataServer.isJoin();

        if (ackMsg!=null)
        {
            int playerId = ackMsg.getReceiverId();
            return dataServer.getGame(playerId,dataGameAnnouncement);
        }
        return null;
        //dataServer.getCanJoin();
    }


    public void startMulticastServer()
    {
        multicastReceive=new MulticastReceive(dataMulticastServer);
        multicastReceiveServer = new Thread(multicastReceive);
        multicastReceiveServer.start();
    }

    public ArrayList<DataGameAnnouncement> getFoundGames()
    {
        return dataMulticastServer.getFoundGame();
    }
    public SnakesProto.GameState getGameState()
    {

        return dataServer.getGameState();
    }

    public void sendGameState(Game game)
    {
        SnakesProto.GameMessage gameMessage = MessageBuilder.getStateMsg(game,dataServer.pollMsgSeq());

        for (Player player:game.getPlayers())
        {
            if (player.getRole()!= SnakesProto.NodeRole.MASTER) {
                dataServer.putGameMessagesAck(new DataGameMessage(player.getIpAddress(), player.getPort(), gameMessage),game.getMainPlayer().getId(),
                        player.getId());
            }
        }

        //dataServer.update(game);
        //dataServer.setGameMessage(MessageBuilder.getStateMsg(game,dataServer,dataServer.getMsgSeq()));
        //dataServer.setTypeSend(SnakesProto.GameMessage.TypeCase.STATE);
    }

    public HashMap<Long,Player> getAccededPlayers()
    {
        ArrayList<AccededPlayer> accededPlayers = dataServer.getAccededPlayers();

        if (accededPlayers!=null && accededPlayers.size()>0) {
            HashMap<Long,Player> newPlayers = new HashMap<>();

            for (AccededPlayer accededPlayer : accededPlayers) {
                newPlayers.put(accededPlayer.msgSeq, new PlayerMaster(accededPlayer.ip,
                        accededPlayer.port,
                        accededPlayer.playerName,
                        accededPlayer.role,
                        accededPlayer.type));
            }

            return newPlayers;
        }
        return null;
    }

    public void sendDirection(Game game, SnakesProto.Direction direction)
    {
        String ip=dataServer.getMaster().getIp();
        int port =dataServer.getMaster().getPort();

        if (!dataServer.putGameMessages(new DataGameMessage(ip,port,
                MessageBuilder.getSteerMsg(direction,game.getMainPlayer().getId(),dataServer.pollMsgSeq()))) &&
        game.getDeputyPlayer()!=null)
        {
            ip=game.getDeputyPlayer().getIpAddress();
            port =game.getDeputyPlayer().getPort();

            dataServer.redirection(new Adress(game.getMasterPlayer().getIpAddress(),game.getMasterPlayer().getPort()),
                    new Adress(ip,port));

            dataServer.putGameMessages(new DataGameMessage(ip,port,
                    MessageBuilder.getSteerMsg(direction,game.getMainPlayer().getId(), dataServer.pollMsgSeq())));
        }
    }

    public boolean isOffline(Adress adress)
    {
        return dataServer.isOffline(adress);
    }

    public HashMap<Adress, SnakesProto.GameMessage> getPlayersDirection()
    {
        if (dataServer!=null)
        {
            return dataServer.getChgPlDir();
        }
        return new HashMap<>();
    }

    public DataGameAnnouncement getAnForIp(String ip,String gameName)
    {
        ArrayList<DataGameAnnouncement> an=getFoundGames();

        for(DataGameAnnouncement dataGameAnnouncement:an)
        {
            if (dataGameAnnouncement.equals(new DataGameAnnouncement(ip,gameName)))
            {
                return dataGameAnnouncement;
            }
        }
        return null;
    }

    public void sendAckMsg(String ip,int port,int senderId,int receiverId,long msgSeq)
    {
        dataServer.putGameMessagesAck(new DataGameMessage(ip,port,
                MessageBuilder.getAckMsg(senderId,receiverId,msgSeq)),senderId,receiverId);
    }

    public void sendErrorMsg(String ip,int port,long msgSeq,String error)
    {
        dataServer.putGameMessages(new DataGameMessage(ip,port,
                MessageBuilder.getErrorMsg(error,msgSeq)));
    }
    public  ArrayList<Adress> getWantedViewers()
    {
        if (dataServer==null)
        {
            return new ArrayList<>();
        }
        return dataServer.getWantetViewers();
    }

    public ArrayList<Adress> getOfflineReceivers()
    {
        return dataServer.getOfflineReceivers();
    }

    public void sendToBeDeputy(String ip,int port, int senderId,int receiverId)
    {
        dataServer.putGameMessages(new DataGameMessage(ip,port,
                MessageBuilder.getChangeRoleReceiver(SnakesProto.NodeRole.DEPUTY,senderId,receiverId,dataServer.pollMsgSeq())));
    }

    public void sendChangeMaster(GameMaster game)
    {



        for (Player player:game.getPlayers())
        {
            if (player.getRole()!= SnakesProto.NodeRole.MASTER) {
                if (game.getDeputyPlayer()==null && player.getRole()== SnakesProto.NodeRole.NORMAL)
                {
                    SnakesProto.GameMessage gameMessageDep = MessageBuilder.getChangeRoleSendRec(SnakesProto.NodeRole.MASTER
                            ,SnakesProto.NodeRole.DEPUTY,game.getMainPlayer().getId(),
                            player.getId()
                            ,dataServer.getMsgSeq());

                    dataServer.putGameMessages(new DataGameMessage(player.getIpAddress(), player.getPort(), gameMessageDep));
                    game.setDeputy(player);
                }
                else {

                    SnakesProto.GameMessage gameMessage = MessageBuilder.getChangeRoleSender(SnakesProto.NodeRole.MASTER,game.getMainPlayer().getId(),
                            player.getId(),dataServer.pollMsgSeq());

                    dataServer.putGameMessages(new DataGameMessage(player.getIpAddress(), player.getPort(), gameMessage));
                }
            }
        }
    }

    public void sendChangeRoleReceiver(String ip, int port, int senderId,int receiverId,SnakesProto.NodeRole role)
    {
        dataServer.putGameMessages(new DataGameMessage(ip,port,
                MessageBuilder.getChangeRoleReceiver(role,senderId,receiverId,dataServer.pollMsgSeq())));
    }

    public void sendChangeRoleSender(String ip, int port, int senderId,int receiverId,SnakesProto.NodeRole role)
    {
        dataServer.putGameMessages(new DataGameMessage(ip,port,
                MessageBuilder.getChangeRoleSender(role,senderId,receiverId,dataServer.pollMsgSeq())));
    }

    public void sendLastTryToMakeDeputy(GameMaster game)
    {
        for (Player player : game.getPlayers()) {
            if (player.getRole() == SnakesProto.NodeRole.NORMAL)
            {
                dataServer.putGameMessages(new DataGameMessage(player.getIpAddress(), player.getPort(),
                        MessageBuilder.getChangeRoleSendRec(SnakesProto.NodeRole.VIEWER,
                                SnakesProto.NodeRole.DEPUTY,game.getMainPlayer().getId(),
                                game.getDeputyPlayer().getId(),dataServer.pollMsgSeq())));
                break;
            }
        }
    }


    public void sendToDeputyThatMasterLeave(Game game)
    {
        dataServer.putGameMessages(new DataGameMessage(game.getDeputyPlayer().getIpAddress(), game.getDeputyPlayer().getPort(),
                MessageBuilder.getChangeRoleSendRec(SnakesProto.NodeRole.VIEWER,
                        SnakesProto.NodeRole.MASTER,game.getMainPlayer().getId(),
                        game.getDeputyPlayer().getId(),dataServer.pollMsgSeq())));
    }

    public Adress getMasterAdress()
    {
        return dataServer.getMaster();
    }

    public void setDeputy(boolean bool)
    {
        dataServer.setDeputy(bool);
    }

    public boolean getDeputy()
    {
        return dataServer.getDeputy();
    }

    Thread server=null;
    DataServer dataServer=null;
    DataMulticastServer dataMulticastServer=new DataMulticastServer();
    Thread multicastReceiveServer;
    MulticastReceive multicastReceive;

    boolean serverWork=false;
}
