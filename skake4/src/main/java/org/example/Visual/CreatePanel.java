package org.example.Visual;

import org.example.BusinessLogic.BusinessLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreatePanel extends JPanel
{
    public CreatePanel(BusinessLogic bc)
    {
        this.bc=bc;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        font = new Font("Arial", Font.PLAIN, sizeFont);

        gameNameField = new JTextField(columns);
        gameNameField.setFont(font);
        widthFieldField = new JTextField(columns);
        widthFieldField.setFont(font);
        heightFieldField = new JTextField(columns);
        heightFieldField.setFont(font);
        countFoodsField = new JTextField(columns);
        countFoodsField.setFont(font);
        delayField = new JTextField(columns);
        delayField.setFont(font);

        JButton createButton = new JButton("Create");

        Font buttonFont = new Font("Arial", Font.PLAIN, 106); // мя шрифта, стиль (PLAIN, BOLD, ITALIC), размер
        createButton.setFont(buttonFont);
        createButton.setPreferredSize(new Dimension(500, 150));

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String gameName = gameNameField.getText();
                int widthField = Integer.parseInt(widthFieldField.getText());
                int heightField = Integer.parseInt(heightFieldField.getText());
                int countFoods = Integer.parseInt(countFoodsField.getText());
                int delay = Integer.parseInt(delayField.getText());

                bc.createNewGame(gameName,widthField,heightField,countFoods,delay);
            }
        });

        add(createLabelAndField("Game Name:", gameNameField));
        add(createLabelAndField("Width Field:", widthFieldField));
        add(createLabelAndField("Height Field:", heightFieldField));
        add(createLabelAndField("Count Foods:", countFoodsField));
        add(createLabelAndField("Delay Game:", delayField));
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
    private JTextField widthFieldField;
    private JTextField delayField;
    private JTextField heightFieldField;
    private JTextField countFoodsField;

    BusinessLogic bc;
}
