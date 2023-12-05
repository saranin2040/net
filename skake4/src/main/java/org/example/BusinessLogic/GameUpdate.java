package org.example.BusinessLogic;

import me.ippolitov.fit.snakes.SnakesProto;
import org.example.BusinessLogic.Game;
import org.example.BusinessLogic.Network.ReceiveNeedInformation;

public interface GameUpdate extends Game
{
    public void update(ReceiveNeedInformation receiveNeedInformation);
}
