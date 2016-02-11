package net.gegy1000.agarbot.gui;

import net.gegy1000.agarbot.Game;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Graphics;

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
        this.setBackground(Color.WHITE);
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);

//        g.clearRect(0, 0, 1000, 800);
        this.setVisible(true);
    }
}
