package org.example.Controller;

import org.example.BusinessLogic.BusinessLogic;
import org.example.BusinessLogic.Network.Data.DataGameAnnouncement;

import javax.swing.*;
import java.awt.*;
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
            // звлекаем подстроку между квадратными скобками
             ip = matcher.group(1);
        }


//        SwingWorker<Void, Void> worker = new SwingWorker<>() {
//            @Override
//            protected Void doInBackground() {
//                        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
//        Matcher matcher = pattern.matcher(string);
//
//        String ip=null;
//
//        if (matcher.find()) {
//            // звлекаем подстроку между квадратными скобками
//             ip = matcher.group(1);
//        }
//                //bc.joinToGame(ip, gameName);
//                return null;
//            }
//
//            @Override
//            protected void done() {
//                // Этот метод выполняется в EDT после завершения doInBackground
//                System.err.println("Master Value: " + " " + gameName);
//            }
//        };
//
//        worker.execute();


        thread=new Thread(new Runnable()
        {
            @Override
            public void run() {
                bc.joinToGame(ip,gameName);
            }
        });
        thread.start();


//
        System.err.println("Master Value: " + " " + gameName);
//        System.err.println("Master Value: "+ip+" "+gameName);
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
