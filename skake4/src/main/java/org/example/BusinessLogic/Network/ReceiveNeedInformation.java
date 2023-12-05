package org.example.BusinessLogic.Network;

import me.ippolitov.fit.snakes.SnakesProto;
import org.example.BusinessLogic.Network.Data.Adress;
import org.example.BusinessLogic.Player;
import org.example.BusinessLogic.PlayerMaster;

import java.util.ArrayList;
import java.util.HashMap;

public interface ReceiveNeedInformation
{
    public HashMap<String, SnakesProto.GameMessage> getPlayersDirection();
    public SnakesProto.GameState getGameState();
    public ArrayList<Adress> getWantedViewers();
    public void sendChangeRoleReceiver(String ip, int port,SnakesProto.NodeRole role);
}
