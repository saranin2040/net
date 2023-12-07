package org.example.Controller;

import org.example.BusinessLogic.BusinessLogic;
import org.example.Visual.Visual;

import javax.swing.*;

public class MainController {
    public void start()
    {
        System.out.println("1. New Game");
        System.out.println("2. Join Game");

        bc=new BusinessLogic();
        bc.createNewGame("Favorite game",10,10,10,100);

        SwingUtilities.invokeLater(() -> {
            app = new Visual(bc);
            app.setVisible(true);
        });

        bc.startGame();
    }

    BusinessLogic bc;
    Visual app;
}
