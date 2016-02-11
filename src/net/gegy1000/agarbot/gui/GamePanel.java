package net.gegy1000.agarbot.gui;

import net.gegy1000.agarbot.Cell;
import net.gegy1000.agarbot.Game;
import net.gegy1000.agarbot.World;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
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

        g.setColor(Color.LIGHT_GRAY);

        g.fillRect(840, 10, 160, 280);

        g.setColor(Color.BLACK);

        g.drawString("Leaderboard:", 850, 25);

        String[] leaderboard = Game.world.leaderboard;

        if (leaderboard != null && leaderboard.length > 0)
        {
            for (int i = 0; i < leaderboard.length; i++)
            {
                String entry = leaderboard[i];

                g.drawString((i + 1) + ". " + entry, 850, (i * 25) + 50);
            }
        }
    }

    private void drawCenteredString(Graphics g, String text, int x, int y)
    {
        Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, g);

        g.drawString(text, (int) (x - bounds.getCenterX()), (int) (y - bounds.getCenterY()));
    }

    public void drawCell(Cell cell, Graphics g, int cameraX, int cameraY)
    {
        int actualX = cell.x;
        int actualY = cell.y;

        int drawSize = cell.size * 2;

        int relativeX = ((actualX - cameraX) + 500) - (drawSize / 2);
        int relativeY = ((actualY - cameraY) + 400) - (drawSize / 2);

        g.setColor(new Color(cell.red, cell.green, cell.blue));
        g.fillOval(relativeX, relativeY, drawSize, drawSize);

        String name = cell.name;

        g.setColor(Color.WHITE);

        int textX = relativeX + (drawSize / 2);
        int textY = relativeY + (drawSize / 2);
        drawCenteredString(g, name, textX, textY);

        if (cell.size > 15)
        {
            drawCenteredString(g, "" + cell.size, textX, textY + 15);
        }
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
