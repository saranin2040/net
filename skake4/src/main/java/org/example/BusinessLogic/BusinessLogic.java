package org.example.BusinessLogic;

import me.ippolitov.fit.snakes.SnakesProto;
import org.example.BusinessLogic.GameData.Game;
import org.example.BusinessLogic.GameData.GameJoined;
import org.example.BusinessLogic.GameData.GameMaster;
import org.example.BusinessLogic.GameData.GameUpdate;
import org.example.BusinessLogic.Network.Data.Adress;
import org.example.BusinessLogic.Network.Data.DataGameAnnouncement;
import org.example.BusinessLogic.Network.Network;
import org.example.BusinessLogic.GameData.Player.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class BusinessLogic
{
    public BusinessLogic()
    {
        network=new Network();
        startCheckGames();
    }

    public void startGame()
    {
        while(true)
        {
            updateGame();
        }
    }

    public boolean joinToGame(String ip, String gameName)
    {
        if (status!=StatusGame.JOINING)
        {
            restart();
            status = StatusGame.JOINING;
            if (network.getFoundGames().contains(new DataGameAnnouncement(ip, gameName))) {
                game = network.startNormalServer(ip, gameName,playerName);

                if (game != null) {
                    status = StatusGame.PLAY;
                }
            }
            return true;
        }
        return false;
    }

    public void becomeMaster()
    {
        network.setDeputy(false);

        game=new GameMaster(game,network.getMasterAdress());

        network.sendChangeMaster((GameMaster) game);
        network.setServerMaster(game);
        status=StatusGame.PLAY;

        System.out.println("i have became master!");
    }
    public synchronized void createNewGame(String gameName,int width,int height,int foods, int delay)
    {
        restart();
        game=new GameMaster(gameName,playerName,width,height,foods,delay);
        network.startMasterServer(game);
        status=StatusGame.PLAY;
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

    public Game getGame()
    {
        return game;
    }
    public String getPlayerName()
    {
        return playerName;
    }
    public StatusGame getStatus()
    {
        return status;
    }
    public ArrayList<DataGameAnnouncement> getListFoundGame()
    {
        return network.getFoundGames();
    }
    public void setStatusBuilding()
    {
        status=StatusGame.BUILDING;
        game=null;
    }

    public void setPlayerName(String playerName)
    {
        this.playerName = playerName;
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
            else if (game instanceof GameJoined)
            {
                network.sendChangeRoleSender(game.getMainPlayer().getId(),game.getMasterPlayer().getId());
            }
            game=null;
        }

        status=StatusGame.NONE;
    }

    public void exit()
    {
        restart();
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

        if (accededPlayers!=null)
        {
            System.out.println("WOW!");
            for (long msgSeq : accededPlayers.keySet())
            {
                Integer playerId = gameMaster.addPlayer(accededPlayers.get(msgSeq));
                if (playerId != null) {
                    network.sendAckMsg(accededPlayers.get(msgSeq).getIpAddress(), accededPlayers.get(msgSeq).getPort(),game.getMainPlayer().getId(), playerId,msgSeq);
                } else {
                    network.sendErrorMsg(accededPlayers.get(msgSeq).getIpAddress(), accededPlayers.get(msgSeq).getPort(), msgSeq,"You can't join");
                }
            }
        }
    }

    private void setDeputy(GameMaster gameMaster)
    {
        if ((gameMaster.getDeputyPlayer() == null ||
                network.isOffline(new Adress(game.getDeputyPlayer().getIpAddress(), game.getDeputyPlayer().getPort())))
                && gameMaster.getPlayers().size() >= 2)
        {
            for (Player player : gameMaster.getPlayers())
            {
                if (player.getRole() == SnakesProto.NodeRole.NORMAL)
                {
                    network.sendToBeDeputy(player.getIpAddress(), player.getPort(),game.getMainPlayer().getId(),
                            player.getId());
                    gameMaster.setDeputy(player);
                    break;
                }
            }
        }
    }

    private synchronized void updateGame()
    {
        if (status==StatusGame.PLAY)
        {
            if (game != null && network.isDataServer())
            {
                if ((game.getMainPlayer().getRole() == SnakesProto.NodeRole.DEPUTY || network.getDeputy()) && network.isOffline(network.getMasterAdress())) {
                    becomeMaster();
                }

                game.update(network);


                if (game instanceof GameMaster)
                {
                    GameMaster gameMaster = (GameMaster) game;
                    gameMaster.deletePlayers(network.getOfflineReceivers());
                    addPlayers();
                    setDeputy(gameMaster);
                    network.sendGameState(game);
                }
            }
        }
    }




    GameUpdate game=null;

    private String playerName="saranin2040";

    private final Network network;
    private StatusGame status=StatusGame.NONE;
}
