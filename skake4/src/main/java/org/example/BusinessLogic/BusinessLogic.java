package org.example.BusinessLogic;

import me.ippolitov.fit.snakes.SnakesProto;
import org.example.BusinessLogic.Network.Data.Adress;
import org.example.BusinessLogic.Network.Data.DataGameAnnouncement;
import org.example.BusinessLogic.Network.Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BusinessLogic
{
    public BusinessLogic()
    {
        network=new Network();
        startCheckGames();
    }

    public synchronized void updateGame()
    {
        if (status==StatusGame.PLAY) {
            // System.out.print("update");
            if (game != null && network.isDataServer()) {

                if ((game.getMainPlayer().getRole() == SnakesProto.NodeRole.DEPUTY || network.getDeputy()) && network.isOffline(network.getMasterAdress())) {
                    becomeMaster();
                }

                //System.err.println("i see from update "+network.getSer());
                game.update(network);

                if (game instanceof GameMaster) {
                    GameMaster gameMaster = (GameMaster) game;
                    addPlayers();
                    if ((gameMaster.getDeputyPlayer() == null ||
                            network.isOffline(new Adress(game.getDeputyPlayer().getIpAddress(), game.getDeputyPlayer().getPort())))
                            && gameMaster.getPlayers().size() >= 2) {
                        for (Player player : gameMaster.getPlayers()) {
                            if (player.getRole() == SnakesProto.NodeRole.NORMAL) {
                                network.sendToBeDeputy(player.getIpAddress(), player.getPort(),game.getMainPlayer().getId(),
                                        player.getId());
                                gameMaster.setDeputy(player);
                                break;
                            }
                        }
                    }
                    gameMaster.deletePlayers(network.getOfflineReceivers());
                    network.sendGameState(game);
                }
            }
        }
    }

    public Game getGame()
    {
        return game;
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public boolean joinToGame(String ip, String gameName)
    {
        System.err.println("i see from join ");

        if (status!=StatusGame.JOINING)
        {
            restart();
            status = StatusGame.JOINING;
            if (network.getFoundGames().contains(new DataGameAnnouncement(ip, gameName))) {
                game = network.startNormalServer(ip, gameName,playerName);

                if (game != null) {
                    status = StatusGame.PLAY;
                    //System.out.println("i joined! " + network.getGameState().getPlayers().getPlayersCount());
                }
            }
            return false;
        }
        return false;
    }

    public void becomeMaster()
    {
        network.setDeputy(false);

        game=new GameMaster(game.getGameName(),game.getMainPlayer(),game.getField().getWidth(),game.getField().getHeight(),
                game.getField().getMaxFoods(),game.getDelayMs(),game.getPlayers(),game.getSnakes(),network.getMasterAdress(),game);

        network.sendChangeMaster((GameMaster) game);
        network.setServerMaster(game);
        status=StatusGame.PLAY;

        System.out.println("i have became master!");
    }
    public synchronized void createNewGame(String gameName,int width,int height,int foods, int delay)
    {
        restart();
        game=menu.createNewGame(gameName,playerName,width,height,foods,delay);
        network.startMasterServer(game);
        status=StatusGame.PLAY;
    }
    public ArrayList<DataGameAnnouncement> getListFoundGame()
    {
        return network.getFoundGames();
    }

    public void updateMainPlayesDirect(SnakesProto.Direction direction)
    {
        if (game.getMainPlayer().getRole()!= SnakesProto.NodeRole.VIEWER) {
            if (game instanceof GameMaster) {
                GameMaster gameMaster = (GameMaster) game;
                gameMaster.setDirection(direction);
            } else if (game instanceof GameJoined) {
                network.sendDirection(game, direction);
            }
        }
    }

    public void setStatusBuilding()
    {
        status=StatusGame.BUILDING;
        game=null;
    }

    public StatusGame getStatus()
    {
        return status;
    }
    public void restart()
    {
        if (game != null) {
            if (game instanceof GameMaster) {
                GameMaster gameMaster = (GameMaster) game;

                if ((gameMaster.getDeputyPlayer() == null ||
                        network.isOffline(new Adress(game.getDeputyPlayer().getIpAddress(), game.getDeputyPlayer().getPort())))
                        && gameMaster.getPlayers().size() >= 2)
                {
                    network.sendLastTryToMakeDeputy(gameMaster);
                } else if ((gameMaster.getDeputyPlayer() != null &&
                        !network.isOffline(new Adress(game.getDeputyPlayer().getIpAddress(), game.getDeputyPlayer().getPort())))) {
                    network.sendToDeputyThatMasterLeave(game);
                }
            }
            game=null;
        }

        status=StatusGame.NONE;
        //network.exit();
    }

    public void exit()
    {
        if (game != null) {
            if (game instanceof GameMaster) {
                GameMaster gameMaster = (GameMaster) game;

                if ((gameMaster.getDeputyPlayer() == null ||
                        network.isOffline(new Adress(game.getDeputyPlayer().getIpAddress(), game.getDeputyPlayer().getPort())))
                        && gameMaster.getPlayers().size() >= 2)
                {
                    network.sendLastTryToMakeDeputy(gameMaster);
                } else if ((gameMaster.getDeputyPlayer() != null &&
                        !network.isOffline(new Adress(game.getDeputyPlayer().getIpAddress(), game.getDeputyPlayer().getPort())))) {
                    network.sendToDeputyThatMasterLeave(game);
                }
            }
            game=null;
        }

        status=StatusGame.NONE;
        network.exit();
    }
    private void startCheckGames()
    {
        network.startMulticastServer();
    }
    private void addPlayers()
    {
        GameMaster gameMaster = (GameMaster) game;


        HashMap<Long,Player> accededPlayers=network.getAccededPlayers();

        //System.out.println("i see from add player "+network.getSome());

        if (accededPlayers!=null) {
            System.out.println("WOW!");
            for (long msgSeq : accededPlayers.keySet()) {
                Integer playerId = gameMaster.addPlayer(accededPlayers.get(msgSeq));
                if (playerId != null) {
                    network.sendAckMsg(accededPlayers.get(msgSeq).getIpAddress(), accededPlayers.get(msgSeq).getPort(),game.getMainPlayer().getId(), playerId.intValue(),msgSeq);
                } else {
                    network.sendErrorMsg(accededPlayers.get(msgSeq).getIpAddress(), accededPlayers.get(msgSeq).getPort(), msgSeq,"You can't join");
                }
            }
        }
    }

    public void setPlayerName(String playerName)
    {
        this.playerName = playerName;
    }


    GameUpdate game=null;
    Menu menu=new Menu();

    private String playerName="saranin2040";

    private Network network;
    private StatusGame status=StatusGame.NONE;
}
