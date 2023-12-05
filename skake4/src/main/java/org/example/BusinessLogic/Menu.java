package org.example.BusinessLogic;

public class Menu
{
    public GameMaster createNewGame(String gameName,String playerName, int width,int height,int foods, int delay)
    {

        return new GameMaster(gameName,playerName,width,height,foods,delay);
    }

    public GameMaster createCopyGame(String gameName,String playerName, int width,int height,int foods, int delay)
    {

        return new GameMaster(gameName,playerName,width,height,foods,delay);
    }
    public void joinTheGame()
    {

    }

    public void startGame()
    {

    }

    public void quite()
    {

    }

    private Thread server;
}
