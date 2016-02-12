package net.gegy1000.agarbot.gui;

import net.gegy1000.agarbot.Cell;
import net.gegy1000.agarbot.Game;
import net.gegy1000.agarbot.World;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
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

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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

            ArrayList<Cell> cells = new ArrayList<>(Game.world.cells);
            cells.sort(new Comparator<Cell>()
            {
                @Override
                public int compare(Cell cell1, Cell cell2)
                {
                    return cell1.size - cell2.size;
                }
            });
            for (Cell cell : cells)
            {
                drawCell(cell, g2d, cameraX, cameraY);
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

        g2d.dispose();
    }

    private void drawCenteredString(Graphics2D g, String text, int x, int y)
    {
        Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, g);

        g.drawString(text, (int) (x - bounds.getCenterX()), (int) (y - bounds.getCenterY()));
    }

    public void drawCell(Cell cell, Graphics2D g, int cameraX, int cameraY)
    {
        int actualX = cell.x;
        int actualY = cell.y;

        int drawSize = cell.size * 2;

        int relativeX = ((actualX - cameraX) + 500) - (drawSize / 2);
        int relativeY = ((actualY - cameraY) + 400) - (drawSize / 2);

        int red = cell.red;
        int green = cell.green;
        int blue = cell.blue;

        g.setColor(constructColour(red, green, blue));
        g.setStroke(new BasicStroke(1));
        g.fillOval(relativeX, relativeY, drawSize, drawSize);
        g.setColor(constructColour(red - 50, green - 50, blue - 50));
        g.setStroke(new BasicStroke(5));
        g.drawOval(relativeX, relativeY, drawSize, drawSize);

        String name = cell.getName();

        g.setColor(Color.WHITE);

        int textX = relativeX + (drawSize / 2);
        int textY = relativeY + (drawSize / 2);
        borderCenteredText(g, name, textX, textY);

        if (cell.size > 15)
        {
            borderCenteredText(g, "" + (cell.size - 22), textX, textY + 15);
        }
    }

    private void borderCenteredText(Graphics2D g, String text, int x, int y)
    {
        g.setColor(Color.BLACK);
        drawCenteredString(g, text, x - 1, y);
        drawCenteredString(g, text, x + 1, y);
        drawCenteredString(g, text, x, y - 1);
        drawCenteredString(g, text, x, y + 1);
        g.setColor(Color.WHITE);
        drawCenteredString(g, text, x, y);
    }

    private Color constructColour(int r, int g, int b)
    {
        return new Color(Math.min(Math.max(r, 0), 255), Math.min(Math.max(g, 0), 255), Math.min(Math.max(b, 0), 255));
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
