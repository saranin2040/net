package org.example.BusinessLogic.Network;

import me.ippolitov.fit.snakes.SnakesProto;
import org.example.BusinessLogic.*;
import org.example.BusinessLogic.Network.Data.*;

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

    public void startMasterServer(Game game)
    {
        dataServer=new DataServer(game);
        dataServer.update(game);
        dataServer.setDataGameConfig(new DataGameConfig(game.getField().getWidth(),
                game.getField().getHeight(),
                game.getField().getMaxFoods(),
                game.getDelayMs()));
        server = new Thread(new Server(dataServer, Role.MASTER));
        server.start();
    }
    public GameJoined startNormalServer(String ip, String gameName)
    {
        DataGameAnnouncement dataGameAnnouncement = getAnForIp(ip,gameName);

        dataServer=new DataServer(dataGameAnnouncement.getDelayMs());

        dataServer.putGameMessages(new DataGameMessage(ip,dataGameAnnouncement.getPort(),
                MessageBuilder.getJoinMsg(gameName,"srarnin2040", SnakesProto.NodeRole.NORMAL,dataServer.pollMsgSeq())));


//        dataServer.setIpJoinGame(ip);
//        dataServer.setNameJoinGame(gameName);
//        dataServer.setTypeRequest(TypeRequest.JOIN);
//        dataServer.setPlayerName("player");
//        dataServer.setPort(dataGameAnnouncement.getPort());
//        dataServer.setRequestedRole(SnakesProto.NodeRole.NORMAL);

        //dataServer.setPort(port);

        server = new Thread(new Server(dataServer,Role.NORMAL));
        server.start();


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
                dataServer.putGameMessages(new DataGameMessage(player.getIpAddress(), player.getPort(), gameMessage));
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
                MessageBuilder.getSteerMsg(direction,game.getMainPlayer().getId(),dataServer.pollMsgSeq()))))
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

    public HashMap<String, SnakesProto.GameMessage> getPlayersDirection()
    {
        return dataServer.getChgPlDir();
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
        dataServer.putGameMessages(new DataGameMessage(ip,port,
                MessageBuilder.getAckMsg(senderId,receiverId,msgSeq)));
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

    public void sendToBeDeputy(String ip,int port)
    {
        dataServer.putGameMessages(new DataGameMessage(ip,port,
                MessageBuilder.getChangeRoleReceiver(SnakesProto.NodeRole.DEPUTY,dataServer.pollMsgSeq())));
    }

    public void sendChangeMaster(GameMaster game)
    {
        SnakesProto.GameMessage gameMessage = MessageBuilder.getChangeRoleSender(SnakesProto.NodeRole.MASTER,dataServer.pollMsgSeq());
        SnakesProto.GameMessage gameMessageDep = MessageBuilder.getChangeRoleSendRec(SnakesProto.NodeRole.MASTER,SnakesProto.NodeRole.DEPUTY,dataServer.getMsgSeq());

        for (Player player:game.getPlayers())
        {
            if (player.getRole()!= SnakesProto.NodeRole.MASTER) {
                if (game.getDeputyPlayer()==null && player.getRole()== SnakesProto.NodeRole.NORMAL )
                {
                    dataServer.putGameMessages(new DataGameMessage(player.getIpAddress(), player.getPort(), gameMessageDep));
                    game.setDeputy(player);
                }
                else {

                    dataServer.putGameMessages(new DataGameMessage(player.getIpAddress(), player.getPort(), gameMessage));
                }
            }
        }
    }

    public void sendChangeRoleReceiver(String ip, int port,SnakesProto.NodeRole role)
    {
        dataServer.putGameMessages(new DataGameMessage(ip,port,
                MessageBuilder.getChangeRoleReceiver(role,dataServer.pollMsgSeq())));
    }

    public void sendChangeRoleSender(String ip, int port,SnakesProto.NodeRole role)
    {
        dataServer.putGameMessages(new DataGameMessage(ip,port,
                MessageBuilder.getChangeRoleSender(role,dataServer.pollMsgSeq())));
    }

    public void sendLastTryToMakeDeputy(Game game)
    {
        for (Player player : game.getPlayers()) {
            if (player.getRole() == SnakesProto.NodeRole.NORMAL) {
                dataServer.putGameMessages(new DataGameMessage(player.getIpAddress(), player.getPort(),
                        MessageBuilder.getChangeRoleSendRec(SnakesProto.NodeRole.VIEWER,
                                SnakesProto.NodeRole.DEPUTY,dataServer.pollMsgSeq())));
                break;
            }
        }
    }


    public void sendToDeputyThatMasterLeave(Game game)
    {
        dataServer.putGameMessages(new DataGameMessage(game.getDeputyPlayer().getIpAddress(), game.getDeputyPlayer().getPort(),
                MessageBuilder.getChangeRoleSendRec(SnakesProto.NodeRole.VIEWER,
                        SnakesProto.NodeRole.MASTER,dataServer.pollMsgSeq())));
    }

    Thread server;
    DataServer dataServer;
    DataMulticastServer dataMulticastServer=new DataMulticastServer();
    Thread multicastReceiveServer;
    MulticastReceive multicastReceive;
}
