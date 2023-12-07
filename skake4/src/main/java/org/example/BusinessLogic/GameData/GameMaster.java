package org.example.BusinessLogic.GameData;

import me.ippolitov.fit.snakes.SnakesProto;
import org.example.BusinessLogic.Network.Data.Adress;
import org.example.BusinessLogic.Network.ReceiveNeedInformation;
import org.example.BusinessLogic.GameData.Player.Player;
import org.example.BusinessLogic.GameData.Player.PlayerMaster;
import org.example.BusinessLogic.GameData.Snake.Snake;
import org.example.BusinessLogic.GameData.Snake.SnakeMaster;

import java.util.*;

public class GameMaster implements GameUpdate
{
    public GameMaster(String gameName,String playerName, int width,int height,int foods, int delay)
    {
        this.gameName=gameName;
        this.field=new Field(width,height,foods);
        this.delayMs=delay;
        mainPlayer=new PlayerMaster(0, playerName, 1, SnakesProto.NodeRole.MASTER, SnakesProto.PlayerType.HUMAN);
        players.put(1,mainPlayer);
        snakes.put(1,new SnakeMaster(1,getFreeCoords()));
    }

    public GameMaster(String gameName, Player player, int width, int height, int foods, int delay, ArrayList<Player> players , ArrayList<Snake> snakes, Adress adressM, Game game)
    {
        this.gameName=gameName;
        this.field=game.getField();
        this.delayMs=delay;
        //mainPlayer=new PlayerMaster(player.getScore(), player.getName(), player.getId(), player.getIpAddress(), player.getPort(), SnakesProto.NodeRole.MASTER, SnakesProto.PlayerType.HUMAN);

        for (Player player1:players)
        {
            if (player1.getRole()== SnakesProto.NodeRole.DEPUTY)
            {
                PlayerMaster playerMaster=new PlayerMaster(player1,player1.getId());
                playerMaster.setRole(SnakesProto.NodeRole.MASTER);
                mainPlayer=playerMaster;
            }
            else if (player1.getRole() == SnakesProto.NodeRole.MASTER)
            {
                PlayerMaster playerMaster=new PlayerMaster(player1,player1.getId());
                playerMaster.setRole(SnakesProto.NodeRole.VIEWER);
                playerMaster.setIpAddress(adressM.getIp());
                playerMaster.setPort(adressM.getPort());
                this.players.put(playerMaster.getId(),playerMaster);
            }
            else {

                this.players.put(player1.getId(), new PlayerMaster(player1, player1.getId()));
            }

            if (player1.getId()>lastPlayerId)
            {
                lastPlayerId=player1.getId();
            }
        }

        this.players.put(mainPlayer.getId(),mainPlayer);

        for (Snake snake:snakes)
        {
            this.snakes.put(snake.getId(),new SnakeMaster(snake));
        }
    }
    public Field getField()
    {
        return field;
    }
    public Player getMasterPlayer()
    {
        return mainPlayer;
    }
    public Player getDeputyPlayer()
    {
        return deputyPlayer;
    }

    public ArrayList<Player> getPlayersRate()
    {
        ArrayList<Player> sortedPlayers=new ArrayList<>(players.values());

        Comparator<Player> scoreComparator = Comparator.comparingInt(Player::getScore).reversed();

        // Сортируем список
        Collections.sort(sortedPlayers, scoreComparator);

        return sortedPlayers;
    }

    public Player getMainPlayer()
    {
        return mainPlayer;
    }
    public Integer addPlayer(Player player)
    {

        for(Player player1:players.values())
        {
            if (player1.getIpAddress()!=null && player1.getIpAddress().equals(player.getIpAddress()) && player1.getPort()==player.getPort())
            {
                return null;
            }
        }

        System.out.println(player.getIpAddress()+" "+player.getPort());

        Coords coords=getFreeCoords();
        if (coords!=null)
        {
            Integer playerId=generateId();
            players.put(playerId,new PlayerMaster(player,playerId.intValue()));
            snakes.put(playerId,new SnakeMaster(playerId,coords));
            return playerId;
        }

        return null;
    }

    public void update(ReceiveNeedInformation receiveNeedInformation)
    {


        if (Math.abs(System.currentTimeMillis()-time)>delayMs)
        {
            changeRole(receiveNeedInformation);
            changePlayersDirection(receiveNeedInformation.getPlayersDirection());

            moveSnakes();
            checkCollitions(receiveNeedInformation);

            time=System.currentTimeMillis();
        }
    }

    public void changeRole(ReceiveNeedInformation receiveNeedInformation)
    {
        ArrayList<Adress> wantedViewers=receiveNeedInformation.getWantedViewers();

        for (Adress wantedViewer:wantedViewers) {
            for (PlayerMaster player : players.values()) {
                if (player.getIpAddress().equals(wantedViewer.getIp())&& player.getPort()==wantedViewer.getPort())
                {
                    snakes.get(player.getId()).setState(SnakesProto.GameState.Snake.SnakeState.ZOMBIE);
                    player.changeRole(SnakesProto.NodeRole.VIEWER);
                    break;
                }
            }
        }
    }
    public int getDelayMs()
    {
        return delayMs;
    }

    public ArrayList<Snake> getSnakes()
    {
        return new ArrayList<Snake>(snakes.values());
    }

    public ArrayList<Coords> getFoodList()
    {
        return field.getFoods();
    }

    public ArrayList<Player> getPlayers()
    {
        return new ArrayList<Player>(players.values());
    }

    public String getGameName()
    {
        return gameName;
    }
    public void setDirection(SnakesProto.Direction direction)
    {
        mainPlayer.setDirect(direction);
    }

    public void deletePlayers(ArrayList<Adress> adresses)
    {
        for(Adress adress:adresses)
        {
            if (snakes.get(getKeyByValue(players,new PlayerMaster(adress.getIp(),adress.getPort())))!=null) {
                snakes.get(getKeyByValue(players, new PlayerMaster(adress.getIp(), adress.getPort())))
                        .setState(SnakesProto.GameState.Snake.SnakeState.ZOMBIE);
            }
            players.remove(getKeyByValue(players,new PlayerMaster(adress.getIp(),adress.getPort())));
             //players.remove(new PlayerJoined(adress.getIp(),adress.getPort()));
        }
    }

    public Integer  getKeyByValue(Map<java.lang.Integer, PlayerMaster> map, PlayerMaster value) {
        for (Map.Entry<java.lang.Integer, PlayerMaster> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void changePlayersDirection(HashMap<Adress, SnakesProto.GameMessage> list_ChangedPlayerDirection)
    {
        if (list_ChangedPlayerDirection!=null) {
            for (Adress adress : list_ChangedPlayerDirection.keySet())
            {
                for (PlayerMaster player:players.values())
                {
                    if (player.equals(new PlayerMaster(adress)))
                    {
                        player.setDirect(list_ChangedPlayerDirection.get(adress).getSteer().getDirection());
                        break;
                    }
                }
            }
        }
    }
    private void moveSnakes()
    {
        for (SnakeMaster snakeMaster:snakes.values())
        {
            if (players.containsKey(snakeMaster.getId()) &&  players.get(snakeMaster.getId()).getRole()!= SnakesProto.NodeRole.VIEWER)
            {
                snakeMaster.move(players.get(snakeMaster.getId()).getDirect(), field.getWidth(), field.getHeight());//TODO реализация поворотау игрока
                if (field.getFoods().contains(snakeMaster.getCoords()))
                {
                    field.removeFood(snakeMaster.getCoords());
                    //field.getFoods().remove(player.getSnake().getCoords());///TODO it needs override equles!!!
                    snakeMaster.growUp();
                    players.get(snakeMaster.getId()).addScore(1);
                }
            }
            else {
                snakeMaster.move(snakeMaster.getDirect(), field.getWidth(), field.getHeight());
                if (field.getFoods().contains(snakeMaster.getCoords()))
                {
                    field.removeFood(snakeMaster.getCoords());
                    //field.getFoods().remove(player.getSnake().getCoords());///TODO it needs override equles!!!
                    snakeMaster.growUp();
                }
            }
        }

        ArrayList<Coords> busysCoords=new ArrayList<>();
        for (SnakeMaster snakeMaster:snakes.values())
        {
            busysCoords.addAll(snakeMaster.getBody());
        }

        field.spawnFoods(busysCoords);
    }
    private void checkCollitions(ReceiveNeedInformation receiveNeedInformation)
    {
        ArrayList<Coords> busysCoords=new ArrayList<>();
        for (SnakeMaster snakeMaster:snakes.values())
        {
            busysCoords.addAll(snakeMaster.getBody());
        }

        Iterator<Coords> iterator = busysCoords.iterator();
        while (iterator.hasNext()) {
            Coords currentCoords = iterator.next();
            // Если элемент повторяется ровно один раз, то удаляем его из исходного списка
            if (Collections.frequency(busysCoords, currentCoords) == 1) {
                iterator.remove();
            }
        }


        Iterator<Map.Entry<Integer, SnakeMaster>> iterator2 = snakes.entrySet().iterator();
        while (iterator2.hasNext()) {
            Map.Entry<Integer, SnakeMaster> snake = iterator2.next();

            if (busysCoords.contains(snake.getValue().getCoords()))
            {
                if (players.containsKey(snake.getKey()) &&  players.get(snake.getKey()).getRole()== SnakesProto.NodeRole.NORMAL)
                {
                    players.get(snake.getKey()).changeRole(SnakesProto.NodeRole.VIEWER);
                    receiveNeedInformation.sendChangeRoleReceiver(players.get(snake.getKey()).getIpAddress(),players.get(snake.getKey()).getPort(),mainPlayer.getId(),players.get(snake.getKey()).getId(), SnakesProto.NodeRole.VIEWER);
                }
                field.spawnRandomFoods(snake.getValue().getBody());
                iterator2.remove();
            }
        }
    }

    private Integer generateId()
    {
        lastPlayerId++;
        return lastPlayerId;
    }

    public void setDeputy(Player player)
    {
        System.out.println("I made "+player.getId()+" deputy");
        players.get(player.getId()).setRole(SnakesProto.NodeRole.DEPUTY);
        deputyPlayer=player;
    }


    private Coords getFreeCoords()
    {
        ArrayList<Coords> busysCoords=new ArrayList<>();
        for (SnakeMaster snakeMaster:snakes.values()) {
            busysCoords.addAll(snakeMaster.getBody());
        }

        for (int x = 0; x <= field.getWidth() - FREE_SQUARE_SIZE; x++)
        {
            for (int y = 0; y <= field.getHeight() - FREE_SQUARE_SIZE; y++)
            {
                boolean squareIsValid = true;

                // Проверяем, содержатся ли координаты внутри квадрата
                for (int i = x; i < x + FREE_SQUARE_SIZE && squareIsValid; i++)
                {
                    for (int j = y; j < y + FREE_SQUARE_SIZE; j++)
                    {
                        if (busysCoords.contains(new Coords(i, j)))
                        {
                            squareIsValid = false;
                            break;
                        }
                    }
                }

                if (squareIsValid)
                {
                    int centerX = x + FREE_SQUARE_SIZE / 2;
                    int centerY = y + FREE_SQUARE_SIZE / 2;
                    if (!field.getFoods().contains(new Coords(centerX,centerY))&&
                            !field.getFoods().contains(new Coords(centerX+1,centerY))&&
                            !field.getFoods().contains(new Coords(centerX,centerY+1))&&
                            !field.getFoods().contains(new Coords(centerX-1,centerY))&&
                            !field.getFoods().contains(new Coords(centerX,centerY-1))) {
                        return new Coords(centerX, centerY);
                    }
                }
            }
        }

        // Если не удалось найти подходящий квадрат, возвращаем null
        return null;
    }

    private String gameName;
    private PlayerMaster mainPlayer;
    private Player deputyPlayer=null;
    private HashMap<Integer,PlayerMaster> players=new HashMap<>();
    private HashMap<Integer,SnakeMaster> snakes=new HashMap<>();
    private Field field;
    private int lastPlayerId=1;

    private long time =System.currentTimeMillis();
    private int delayMs=700;

    private static final int FREE_SQUARE_SIZE = 5;
}
