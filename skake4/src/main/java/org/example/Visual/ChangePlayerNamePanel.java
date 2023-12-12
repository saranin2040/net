package org.example.Visual;

import org.example.BusinessLogic.BusinessLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChangePlayerNamePanel extends JPanel
{
    public ChangePlayerNamePanel(BusinessLogic bc)
    {
        this.bc=bc;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        font = new Font("Arial", Font.PLAIN, sizeFont);

        gameNameField = new JTextField(columns);
        gameNameField.setFont(font);

        JButton createButton = new JButton("Change Name");

        Font buttonFont = new Font("Arial", Font.PLAIN, 106); // мя шрифта, стиль (PLAIN, BOLD, ITALIC), размер
        createButton.setFont(buttonFont);
        createButton.setPreferredSize(new Dimension(500, 150));

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String gameName = gameNameField.getText();

                bc.setPlayerName(gameName);
            }
        });

        add(createLabelAndField("Player Name:", gameNameField));
        add(createButton);
    }

    private JPanel createLabelAndField(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new FlowLayout());
        JLabel label =new JLabel(labelText);
        label.setFont(font);
        panel.add(label);
        panel.add(textField);
        return panel;
    }

    private int columns=15;
    private int sizeFont=40;
    private Font font;

    private JTextField gameNameField;

    BusinessLogic bc;
}
