package org.example.BusinessLogic.Network;

import me.ippolitov.fit.snakes.SnakesProto;
import org.example.BusinessLogic.*;
import org.example.BusinessLogic.Network.Data.DataGameConfig;
import org.example.BusinessLogic.Network.Data.DataServer;

import java.util.ArrayList;
import java.util.List;

public class MessageBuilder
{

    static public SnakesProto.GameMessage getAnnouncementMsg(DataServer dataServer, long msgSeq)
    {
        return SnakesProto.GameMessage.newBuilder().setAnnouncement(getAnnouncementMsg(dataServer)).setMsgSeq(msgSeq).build();
    }

    static public SnakesProto.GameMessage getJoinMsg(String gameName, String playerName, SnakesProto.NodeRole playerRole, long msgSeq)
    {
        return SnakesProto.GameMessage.newBuilder().setJoin(getJoinMsg(gameName,playerName,playerRole)).setMsgSeq(msgSeq).build();
    }

    static public SnakesProto.GameMessage getSteerMsg(SnakesProto.Direction direction, int idSender,long msgSeq)
    {
        return SnakesProto.GameMessage.newBuilder().setSteer(getSteerMsg(direction)).setSenderId(idSender).setMsgSeq(msgSeq).build();
    }

    static public SnakesProto.GameMessage.SteerMsg getSteerMsg(SnakesProto.Direction direction)
    {
        return SnakesProto.GameMessage.SteerMsg.newBuilder().setDirection(direction).build();
    }

    static public SnakesProto.GameMessage getChangeRoleSender(SnakesProto.NodeRole role, int senderId,int receiverId, long msgSeq)
    {
        return SnakesProto.GameMessage.newBuilder().setRoleChange(SnakesProto.GameMessage.RoleChangeMsg
                .newBuilder().setSenderRole(role).build())
                .setSenderId(senderId)
                .setReceiverId(receiverId).setMsgSeq(msgSeq).build();
    }

    static public SnakesProto.GameMessage getChangeRoleSendRec(SnakesProto.NodeRole roleSend, SnakesProto.NodeRole roleRec,int senderId,int receiverId,long msgSeq)
    {
        return SnakesProto.GameMessage.newBuilder().setRoleChange(SnakesProto.GameMessage.RoleChangeMsg
                .newBuilder().setSenderRole(roleSend).setReceiverRole(roleRec).build())
                .setSenderId(senderId)
                .setReceiverId(receiverId)
                .setMsgSeq(msgSeq).build();
    }

    static public SnakesProto.GameMessage getChangeRoleReceiver(SnakesProto.NodeRole role, int senderId,int receiverId, long msgSeq)
    {
        return SnakesProto.GameMessage.newBuilder().setRoleChange(SnakesProto.GameMessage.RoleChangeMsg
                .newBuilder().setReceiverRole(role).build())
                .setSenderId(senderId)
                .setReceiverId(receiverId).setMsgSeq(msgSeq).build();
    }

    static public SnakesProto.GameMessage getPingMsg(long msgSeq)
    {
        return SnakesProto.GameMessage.newBuilder().setPing(SnakesProto.GameMessage.PingMsg.newBuilder().build()).setMsgSeq(msgSeq).build();
    }

    static public SnakesProto.GameMessage getAckMsg(int senderId,int receiverId,long msgSeq)
    {
        return SnakesProto.GameMessage.newBuilder().setAck(SnakesProto.GameMessage.AckMsg.newBuilder().build()).setMsgSeq(msgSeq)
                .setSenderId(senderId)
                .setReceiverId(receiverId).build();
    }

    static public SnakesProto.GameMessage getAckMsg(SnakesProto.GameMessage gameMessage)
    {
        return SnakesProto.GameMessage.newBuilder().setAck(SnakesProto.GameMessage.AckMsg.newBuilder().build()).setMsgSeq(gameMessage.getMsgSeq()).build();
    }

    static public SnakesProto.GameMessage getErrorMsg(String error,long msgSeq)
    {
        return SnakesProto.GameMessage.newBuilder().setError(getErrorMsg(error)).setMsgSeq(msgSeq).build();
    }

    static public SnakesProto.GameMessage.ErrorMsg getErrorMsg(String error)
    {
        return SnakesProto.GameMessage.ErrorMsg.newBuilder().setErrorMessage(error).build();
    }

    static public SnakesProto.GameMessage getStateMsg(Game game, long msgSeq)
    {
        return SnakesProto.GameMessage.newBuilder()
                .setState(
                        getStateMsg(game)).setMsgSeq(msgSeq)
                .build();
    }

    static private SnakesProto.GameMessage.StateMsg getStateMsg(Game game)
    {
        return SnakesProto.GameMessage.StateMsg.newBuilder()
                .setState(getGameState(game)).build();
    }

    static private SnakesProto.GameState getGameState(Game game)
    {
        SnakesProto.GameState.Builder gameState= SnakesProto.GameState.newBuilder()
                .setStateOrder(0);

        ArrayList<Snake> snakes= game.getSnakes();

        for (Coords coords:game.getFoodList())
        {
            gameState.addFoods(getCoord(coords.x, coords.y));
        }

        SnakesProto.GamePlayers.Builder gamePlayers=SnakesProto.GamePlayers.newBuilder();

        ArrayList<Player> players=game.getPlayers();
        for (int i=0;i<players.size();i++)
        {
            gamePlayers.addPlayers(getPlayer(players.get(i)));
        }

        try {

            for (int i = 0; i < snakes.size(); i++) {
                gameState.addSnakes(getSnake(snakes.get(i), snakes.get(i).getId(), game.getField().getWidth(), game.getField().getHeight()));
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            e.printStackTrace();
        }

        gamePlayers.build();

        gameState.setPlayers(gamePlayers);

        return gameState.build();
    }

    static private SnakesProto.GamePlayers getPlayers_(GameMaster game)
    {
        SnakesProto.GamePlayers.Builder gamePlayers=SnakesProto.GamePlayers.newBuilder();

        ArrayList<Player> players=game.getPlayers();
        for (int i=0;i<players.size();i++)
        {
            gamePlayers.addPlayers(getPlayer(players.get(i)));
        }

        return gamePlayers.build();
    }

    static private SnakesProto.GameState.Snake getSnake(Snake snake,int id,int width,int height)
    {
        SnakesProto.GameState.Snake.Builder snakeBuilder= SnakesProto.GameState.Snake.newBuilder()
                .setPlayerId(id)
                .setState(SnakesProto.GameState.Snake.SnakeState.ALIVE);


        ArrayList<Coords> coords=getOffsets(new ArrayList<>(snake.getBody()), width,height);


        for (Coords coord:coords)
        {
            snakeBuilder.addPoints(getCoord(coord.x,coord.y));
        }

        //snakeBuilder.addPoints(getCoord(snake.getBody().get(snake.getBody().size()-1).x,snake.getBody().get(snake.getBody().size()-1).y));

        switch (snake.getDirect())
        {
            case RIGHT -> snakeBuilder.setHeadDirection(SnakesProto.Direction.RIGHT);
            case LEFT -> snakeBuilder.setHeadDirection(SnakesProto.Direction.LEFT);
            case UP -> snakeBuilder.setHeadDirection(SnakesProto.Direction.UP);
            case DOWN -> snakeBuilder.setHeadDirection(SnakesProto.Direction.DOWN);
        }

        return snakeBuilder.build();
    }

    static private SnakesProto.GameState.Coord getCoord(int x,int y)
    {
        return SnakesProto.GameState.Coord.newBuilder().setX(x).setY(y).build();
    }

    static private SnakesProto.GameMessage.JoinMsg getJoinMsg(String gameName, String playerName, SnakesProto.NodeRole playerRole)
    {
        return SnakesProto.GameMessage.JoinMsg.newBuilder()
                .setPlayerType(TYPE_PLAYER_JOINING)
                .setPlayerName(playerName)
                .setGameName(gameName)
                .setRequestedRole(playerRole)
                .build();
    }

    static private SnakesProto.GameMessage.AnnouncementMsg getAnnouncementMsg(DataServer dataServer)
    {
        return SnakesProto.GameMessage.AnnouncementMsg.newBuilder()
                .addGames(getGameAnnouncement(dataServer))
                .build();
    }

    static private SnakesProto.GameAnnouncement getGameAnnouncement(DataServer dataServer)
    {
        return SnakesProto.GameAnnouncement.newBuilder()
                .setPlayers(getPlayers(dataServer))
                .setConfig(getGameConfig(dataServer))
                .setGameName(dataServer.getGameName())
                .setCanJoin(dataServer.getCanJoin())
                .build();
    }

    static private SnakesProto.GameConfig getGameConfig(DataServer dataServer)
    {
        DataGameConfig dataGameConfig=dataServer.getDataGameConfig();
        return SnakesProto.GameConfig.newBuilder()
                .setWidth(dataGameConfig.width)
                .setHeight(dataGameConfig.height)
                .setFoodStatic(dataGameConfig.countFood)
                .setStateDelayMs(dataGameConfig.stateDelayMs)
                .build();
    }

    static private SnakesProto.GamePlayers getPlayers(DataServer dataServer)
    {
        SnakesProto.GamePlayers.Builder gamePlayers=SnakesProto.GamePlayers.newBuilder();

        List<Player> players=dataServer.getPlayers();
        int n=players.size();
        for (int i=0;i<n;i++)
        {
            gamePlayers.addPlayers(getPlayer(players.get(i)));
        }

        return gamePlayers.build();
    }

    static private SnakesProto.GamePlayer getPlayer(Player player)
    {
        SnakesProto.GamePlayer.Builder gamePlayer= SnakesProto.GamePlayer.newBuilder();
        gamePlayer
                .setName(player.getName())
                .setId(player.getId())
                .setPort(player.getPort())
                .setScore(player.getScore())
                .setRole(player.getRole())
                .setType(player.getType())
                .build();

        if (player.getPort()!=-1)
        {
            gamePlayer.setPort(player.getPort());
        }

        if (player.getIpAddress()!=null)
        {
            gamePlayer.setIpAddress(player.getIpAddress());
        }

        return gamePlayer.build();
    }

    private static ArrayList<Coords> getOffsets(ArrayList<Coords> body,int width,int height) {
        ArrayList<Coords> offsets = new ArrayList<>();


        offsets.add(body.get(body.size()-1));


        int help=body.size()-1;
        int offsetX = 0;
        int offsetY = 0;
        int xh2 = body.get(body.size()-2).x - body.get(body.size()-1).x;
        int yh2 = body.get(body.size()-2).y - body.get(body.size()-1).y;


        boolean g=true;

        if (xh2!=0)
        {
            g=true;
            if (xh2==1 || xh2==-width+1) {
                offsetX++;
            }
            else if (xh2==-1 || xh2==width-1) {
                offsetX--;
            }
            //offsetX=body.get(body.size()-2).x - body.get(help).x;
        }
        else if (yh2!=0)
        {
            g=false;
            if (yh2==1 || yh2==-height+1) {
                offsetY++;
            }
            else if (yh2==-1 || yh2==height-1) {
                offsetY--;
            }
            //offsetY=body.get(body.size()-2).y - body.get(help).y;
        }

        for (int i = body.size()-2; i >= 1; i--)
        {
            int xh = body.get(i-1).x - body.get(i).x;
            int yh = body.get(i-1).y - body.get(i).y;

            if (xh!=0)
            {
                if (g==false)
                {
                    g=true;
                    offsets.add(new Coords(0,offsetY));
                    help=i+1;
                    offsetY=0;
                }

                if (xh==1 || xh==-width+1) {
                    offsetX++;
                }
                else if (xh==-1 || xh==width-1) {
                    offsetX--;
                }

                //offsetX=(body.get(i-1).x - body.get(help).x)%width;
            }
            else if (yh!=0)
            {
                if (g==true)
                {
                    g=false;
                    offsets.add(new Coords(offsetX,0));
                    help=i+1;
                    offsetX=0;
                }

                if (yh==1 || yh==-height+1) {
                    offsetY++;
                }
                else if (yh==-1 || yh==height-1) {
                    offsetY--;
                }
                //offsetY=(body.get(i-1).y - body.get(help).y)%height;
            }
        }

        offsets.add(new Coords(offsetX,offsetY));

        for (Coords coords:offsets)
        {
            if (coords.x==0 && coords.y==0)
            System.err.print("AAAAAAAAAAAAAAAAAAA");
        }

        return offsets;
    }

    private static final SnakesProto.PlayerType TYPE_PLAYER_JOINING = SnakesProto.PlayerType.HUMAN;
}
