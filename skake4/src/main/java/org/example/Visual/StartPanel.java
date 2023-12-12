package org.example.Visual;

import javax.swing.*;
import java.awt.*;

public class StartPanel extends JPanel
{

    private void paintImage(Graphics g){
        Graphics2D g2 = (Graphics2D) g;

                String imagePath = pathImage;
                ImageIcon imageIcon = new ImageIcon(imagePath);

                Image image = imageIcon.getImage();

                g2.drawImage(image, 0, 0, (int) getWidth(), (int) getHeight(), null);



    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);;
        paintImage(g);
    }

    private static final String pathImage="src/main/resources/start.jpg";
}
