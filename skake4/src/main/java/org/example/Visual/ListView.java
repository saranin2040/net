package org.example.Visual;

import javax.swing.*;
import java.awt.*;

public class ListView
{

    ListView (JPanel list,JPanel container)
    {
        this.list=list;
        this.container=container;
    }

    public void addString(String string)
    {
        JLabel playerLabel = new JLabel(string);
        Font labelFont = playerLabel.getFont();
        playerLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 24)); // Установка размера шрифта 14
        list.add(playerLabel);
    }

    JPanel list;
    JPanel container;
}
