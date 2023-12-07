package org.example.BusinessLogic.GameData.Game;

import org.example.BusinessLogic.Network.ReceiveNeedInformation;

public interface GameUpdate extends Game
{
    public void update(ReceiveNeedInformation receiveNeedInformation);
}
