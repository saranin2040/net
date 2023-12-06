package org.example.Visual;

import javax.swing.*;
import java.awt.*;

public class JoiningPanel extends JPanel
{
    public JoiningPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0)); // Выравнивание по центру

        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 100));
// Выравнивание текста по центру

        add(label);
    }

    private final String text="Joining...";
}
