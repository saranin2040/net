package org.example.Controller;

import me.ippolitov.fit.snakes.SnakesProto;
import org.example.BusinessLogic.BusinessLogic;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ControllerKeys extends KeyAdapter
{

    public ControllerKeys(BusinessLogic bc)
    {
        this.bc=bc;
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (bc.getGame().getMainPlayer().getRole()!= SnakesProto.NodeRole.VIEWER) {
            if (e.getKeyChar() == 'w') {
                bc.updateMainPlayesDirect(SnakesProto.Direction.UP);
            } else if (e.getKeyChar() == 's') {
                bc.updateMainPlayesDirect(SnakesProto.Direction.DOWN);
            } else if (e.getKeyChar() == 'a') {
                bc.updateMainPlayesDirect(SnakesProto.Direction.LEFT);
            } else if (e.getKeyChar() == 'd') {
                bc.updateMainPlayesDirect(SnakesProto.Direction.RIGHT);
            }
        }
    }

    BusinessLogic bc;
}
