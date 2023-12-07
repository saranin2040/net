package org.example.Controller;

import org.example.BusinessLogic.BusinessLogic;
import org.example.BusinessLogic.GameData.GameMaster;
import org.example.BusinessLogic.Network.Data.DataGameAnnouncement;
import org.example.Visual.Visual;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MainController {
    public void start()
    {

        System.out.println("1. New Game");
        System.out.println("2. Join Game");
        BusinessLogic bc=new BusinessLogic();
//
//        System.out.println("enter:");
//        Scanner scanner = new Scanner(System.in);
//        String str =scanner.nextLine();
        String str="1";
        if (str.equals("1"))
        {
//            System.out.println("1.1 Enter game name:");
//            String name=scanString();
//            System.out.println("1.2 Enter player name:");
//            String playerName=scanString();
//            System.out.println("2. Enter width (10 to 100):");
//            int width = scanInt();
//            System.out.println("3. Enter height (10 to 100):");
//            int height = scanInt();
//            System.out.println("4. Enter max food (0 to 100):");
//            int foods = scanInt();
//            System.out.println("5. Enter delay (100 to 3000):");
//            int delay = scanInt();

            //bc.createNewGame(name,playerName,width,height,foods,delay);
            bc.createNewGame("Nemo",10,10,10,1000);
        }
        else {
            while(true)
            {
                ArrayList<DataGameAnnouncement> f= bc.getListFoundGame();

                for (DataGameAnnouncement gameAnnouncement: f)
                {
                    System.out.print("("+gameAnnouncement.getGameName()+":"+gameAnnouncement.getIp()+") ");
                }

                System.out.println("enter ip: ");
                Scanner scanner2 = new Scanner(System.in);
                String ip =scanner2.nextLine();

                System.out.println("enter name: ");
                scanner2 = new Scanner(System.in);
                String name =scanner2.nextLine();

                int port=0;
                for (DataGameAnnouncement gameAnnouncement: f)
                {
                    if (gameAnnouncement.getIp().equals(ip))
                    {
                        port= gameAnnouncement.getPort();
                        break;
                    }
                    //System.out.print("("+gameAnnouncement.getGameName()+":"+gameAnnouncement.getIp()+") ");
                }

                bc.joinToGame(ip,name);
            }
        }


        // SwingUtilities.invokeLater(() -> {

        SwingUtilities.invokeLater(() -> {
            app = new Visual(bc);
            app.setVisible(true);
        });





        while(true)
        {
            //System.out.println("update");
            bc.updateGame();
//            SwingUtilities.invokeLater(() -> {
//                app.update2(bc.getGame());
//                app.updateGameTable(bc.getListFoundGame());
//                 });

//
//            if (bc.getGame())
//            {
//
//            }
//            bc.updateMainPlayesDirect(SnakesProto.Direction.UP);

            //System.out.println("x="+bc.getGame().getMainPlayers().getSnake().getCoords().x+":y="+bc.getGame().getMainPlayers().getSnake().getCoords().y);

            //visual.update(bc.getGame());
        }
       // });

//        Menu menu=new Menu();
//        game=menu.createNewGame();
//
//        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//
//        scheduler.scheduleAtFixedRate(() -> {
//            game.update();
//        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

//    public void pressKey()
//    {
//        game.updateMainPlayesDirect(Direct.RIGHT);
//    }

    private String scanString()
    {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private int scanInt()
    {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }
    private GameMaster game;
    Visual app;
}
