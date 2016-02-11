package net.gegy1000.agarbot.gui;

import net.gegy1000.agarbot.Game;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class AgarBotFrame extends JFrame
{
    public AgarBotFrame()
    {
        this.setSize(1000, 800);
        this.setTitle("agar.io bot - " + Game.NICK);

        this.setResizable(false);

        this.setLocation(0, 0);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.add(new GamePanel());
        this.setBackground(Color.BLACK);

        addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {

            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyChar() == ' ')
                {
                    Game.world.split();
                }
                else if (e.getKeyChar() == 'w')
                {
                    Game.world.eject();
                }
            }

            @Override
            public void keyReleased(KeyEvent e)
            {

            }
        });
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);

        Point mouse = MouseInfo.getPointerInfo().getLocation();

        if (mouse != null)
        {
            Point locationOnScreen = getLocationOnScreen();
            Game.world.setMove((mouse.x - locationOnScreen.x) - 500, (mouse.y - locationOnScreen.y) - 400);
        }

//        g.clearRect(0, 0, 1000, 800);
        this.setVisible(true);
    }
}
