package net.gegy1000.agarbot.gui;

import net.gegy1000.agarbot.Game;
import net.gegy1000.agarbot.Main;

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
    private Game game;

    public AgarBotFrame(Game game)
    {
        this.game = game;

        this.setSize(1000, 800);
        this.setTitle("agar.io bot - " + game.nick);

        this.setResizable(false);

        this.setLocation(0, 0);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.add(new GamePanel(game));
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
                    game.world.split();
                }
                else if (e.getKeyChar() == 'w')
                {
                    game.world.eject();
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
            game.world.setMove((mouse.x - locationOnScreen.x) - 500, (mouse.y - locationOnScreen.y) - 400);
        }

        this.setVisible(true);
    }
}
