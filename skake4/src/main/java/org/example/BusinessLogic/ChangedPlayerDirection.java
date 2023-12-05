package org.example.BusinessLogic;

public class ChangedPlayerDirection
{
    public ChangedPlayerDirection(int idPlayer, Direct direct)
    {
        this.idPlayer=idPlayer;
        this.direct=direct;
    }
    public int getIdPlayer()
    {
        return idPlayer;
    }
    public Direct getDirect()
    {
        return direct;
    }
    private int idPlayer;
    private Direct direct;
}
