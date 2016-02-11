package net.gegy1000.agarbot.gui;

import net.gegy1000.agarbot.Cell;
import net.gegy1000.agarbot.Game;
import net.gegy1000.agarbot.World;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel
{
    private World world = Game.world;

    public GamePanel()
    {
        super();
        this.setDoubleBuffered(true);
    }

    public void paint(Graphics g)
    {
        String[] leaderboard = Game.world.leaderboard;

        if (leaderboard != null && leaderboard.length > 0)
        {
            for (int i = 0; i < leaderboard.length; i++)
            {
                String entry = leaderboard[i];

                g.drawString((i + 1) + ". " + entry, 850, (i * 25) + 50);
            }
        }

        List<Cell> player = Game.world.getPlayerCells();

        if (player.size() > 0)
        {
            int[] playerX = new int[player.size()];
            int[] playerY = new int[player.size()];

            int i = 0;

            for (Cell p : player)
            {
                if (p != null)
                {
                    playerX[i] = p.x;
                    playerY[i] = p.y;

                    i++;
                }
            }

            int cameraX = getAverage(playerX);
            int cameraY = getAverage(playerY);

            for (Cell cell : new ArrayList<>(Game.world.cells))
            {
                drawCell(cell, g, cameraX, cameraY);
            }
        }
    }

    public void drawCell(Cell cell, Graphics g, int cameraX, int cameraY)
    {
        int actualX = cell.x;
        int actualY = cell.y;

        int relativeX = (actualX - cameraX) + 500;
        int relativeY = (actualY - cameraY) + 400;

        int size = cell.size * 5;

        g.setColor(new Color(cell.red, cell.green, cell.blue));
        g.fillOval(relativeX, relativeY, size, size);

        String name = cell.name;
        g.setColor(Color.WHITE);

        g.drawString(name, relativeX + (size / 2), relativeY + (size / 2) + 5);
    }

    private int getAverage(int... values)
    {
        int value = 0;

        for (int v : values)
        {
            value += v;
        }

        return value / values.length;
    }
}
