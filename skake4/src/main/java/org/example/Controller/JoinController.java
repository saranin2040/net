package org.example.Controller;

import org.example.BusinessLogic.BusinessLogic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinController implements ActionListener
{
    public JoinController(BusinessLogic bc)
    {
        this.bc=bc;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {

        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(string);

        ip=null;

        if (matcher.find()) {
             ip = matcher.group(1);
        }

        thread=new Thread(new Runnable()
        {
            @Override
            public void run() {
                bc.joinToGame(ip,gameName);
            }
        });
        thread.start();

        System.out.println("Joined to " + gameName);
    }

    public void setInf(String string, String gameName)
    {
        this.string=string;
        this.gameName=gameName;
    }
    String string;
    String ip;
    String gameName;

    Thread thread;
    BusinessLogic bc;
}
