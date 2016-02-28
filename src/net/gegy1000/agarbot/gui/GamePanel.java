package net.gegy1000.agarbot.gui;

import net.gegy1000.agarbot.Cell;
import net.gegy1000.agarbot.Game;
import net.gegy1000.agarbot.PlayerController;
import net.gegy1000.agarbot.World;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GamePanel extends JPanel
{
    private Game game;
    private Font font = new Font("Ubuntu", Font.BOLD, 12);

    public GamePanel(Game game)
    {
        super();
        this.game = game;
        this.setDoubleBuffered(true);
    }

    public void paint(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;

        draw(g2d);

        g2d.dispose();
    }

    public void draw(Graphics2D g)
    {
        World world = game.world;
        List<Cell> player = world.getPlayerCells();

        g.setFont(font);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (player.size() > 0)
        {
            ArrayList<Cell> cells = new ArrayList<Cell>(world.cells);
            cells.sort(new Comparator<Cell>()
            {
                @Override
                public int compare(Cell cell1, Cell cell2)
                {
                    return cell1.renderSize - cell2.renderSize;
                }
            });

            int smallestPlayerSize = Integer.MAX_VALUE;
            Cell smallestPlayer = null;

            for (Cell p : player)
            {
                if (p != null)
                {
                    if (p.size < smallestPlayerSize)
                    {
                        smallestPlayer = p;
                        smallestPlayerSize = p.size;
                    }
                }
            }

//            int[] avgPos = world.getCameraPos();
//
//            int cameraX = avgPos[0];
//            int cameraY = avgPos[1];

//            System.out.println(Arrays.toString(playerX) + ", " + world.cameraX);

            g.setStroke(new BasicStroke((float) (1.25 * world.zoom)));

            double zoom = world.zoom;

            int halfWidth = AgarBotFrame.WIDTH / 2;
            int halfHeight = AgarBotFrame.HEIGHT / 2;

            g.setColor(new Color(25, 25, 25));

            int spacing = 60;

            int offsetX = (int) (halfWidth / zoom);

            for (int x = world.cameraX - offsetX; x < world.cameraX + offsetX; x++)
            {
                int drawX = (int) ((((x / spacing * spacing) - world.cameraX) * zoom) + halfWidth);
                g.drawLine(drawX, 0, drawX, AgarBotFrame.HEIGHT);
            }

            int offsetY = (int) (halfHeight / zoom);

            for (int y = world.cameraY - offsetY; y < world.cameraY + offsetY; y++)
            {
                int drawY = (int) ((((y / spacing * spacing) - world.cameraY) * zoom) + halfHeight);
                g.drawLine(0, drawY, AgarBotFrame.WIDTH, drawY);
            }

            if (world.hasDebugLine)
            {
                g.setStroke(new BasicStroke((float) (4 * world.zoom)));

                for (Cell p : player)
                {
                    g.setColor(constructColour(p.red - 50, p.green - 50, p.blue - 50));

                    int relativePlayerX = calculateRelativePos(world.cameraX, p.x, zoom, 0, halfWidth);
                    int relativePlayerY = calculateRelativePos(world.cameraY, p.y, zoom, 0, halfHeight);

                    int relativeLineX = calculateRelativePos(world.cameraX, world.lineX, zoom, 0, halfWidth);
                    int relativeLineY = calculateRelativePos(world.cameraY, world.lineY, zoom, 0, halfHeight);

                    g.drawLine(relativePlayerX, relativePlayerY, relativeLineX, relativeLineY);
                }
            }

            g.setStroke(new BasicStroke(1));

            for (Cell cell : cells)
            {
                drawCell(smallestPlayer, cell, g, world.cameraX, world.cameraY, player.contains(cell));
            }
        }

        g.setColor(new Color(0, 0, 0, 90));

        g.fillRect(840, 10, 160, 280);

        g.setColor(Color.WHITE);

        drawCenteredString(g, "Leaderboard", 920, 25);

        String[] leaderboard = world.leaderboard;

        if (leaderboard != null && leaderboard.length > 0)
        {
            for (int i = 0; i < leaderboard.length; i++)
            {
                String entry = leaderboard[i];

                drawCenteredString(g, (i + 1) + ". " + entry, 915, (i * 25) + 50);
            }
        }

        PlayerController controller = game.controller;

        if (controller != null)
        {
            g.drawString("Generation: " + (controller.currentGeneration + 1), 10, 15);
            g.drawString("Highest Fitness: " + controller.highestFitness, 10, 30);
            g.drawString("Current Fitness: " + world.getFitness(), 20, 750);
            int width = controller.inputWidth;
            int height = controller.inputHeight;

            float[] inputs = controller.inputs;

            if (inputs != null)
            {
                for (int y = 0; y < height; y++)
                {
                    for (int x = 0; x < width; x++)
                    {
                        int baseIndex = (y * width) + x;

                        int colour = (int) ((inputs[baseIndex] + 1) * 127);

                        drawNeuralInput(g, x, y, new Color(colour, colour, colour, 180));
                    }
                }
            }
        }
    }

    private void drawNeuralInput(Graphics2D g, int x, int y, Color color)
    {
        x *= 5;
        y *= 5;

        x += 20;
        y += 40;

        g.setColor(color);
        g.fillRect(x, y, 5, 5);
    }

    private void drawCenteredString(Graphics2D g, String text, int x, int y)
    {
        Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, g);

        g.drawString(text, (int) (x - bounds.getCenterX()), (int) (y - bounds.getCenterY()));
    }

    public void drawCell(Cell smallestPlayer, Cell cell, Graphics2D g, int cameraX, int cameraY, boolean isPlayer)
    {
        int actualX = cell.x;
        int actualY = cell.y;

        double zoom = game.world.zoom;

        int drawSize = (int) ((cell.renderSize * 2) * zoom);

        int relativeX = calculateRelativePos(cameraX, actualX, zoom, drawSize, AgarBotFrame.WIDTH / 2);
        int relativeY = calculateRelativePos(cameraY, actualY, zoom, drawSize, AgarBotFrame.HEIGHT / 2);

        int red = cell.red;
        int green = cell.green;
        int blue = cell.blue;

//        if (neural)
//        {
//            if (cell.virus)
//            {
//                red = green = blue = 64;
//            }
//            else if (isPlayer)
//            {
//                red = green = blue = 200;
//            }
//            else
//            {
//                if (smallestPlayer != null && smallestPlayer.canEat(cell))
//                {
//                    red = green = blue = 256;
//                }
//                else
//                {
//                    red = green = blue = 1;
//                }
//            }
//        }

        g.setColor(constructColour(red, green, blue));

        g.setStroke(new BasicStroke(1));
        g.fillOval(relativeX, relativeY, drawSize, drawSize);

        g.setColor(constructColour(red - 50, green - 50, blue - 50));
        g.setStroke(new BasicStroke(5));
        g.drawOval(relativeX, relativeY, drawSize, drawSize);
        g.setStroke(new BasicStroke(1));

        if (!cell.virus)
        {
            String name = cell.getName();

            g.setColor(Color.WHITE);

            int textX = relativeX + (drawSize / 2);
            int textY = relativeY + (drawSize / 2);
            borderCenteredText(g, name, textX, textY);

            if (cell.getMass() >= 10)
            {
                borderCenteredText(g, "" + (cell.getMass()), textX, textY + 15);
            }
        }
    }

    private int calculateRelativePos(int camera, int actual, double zoom, int drawSize, int halfScreen)
    {
        return (int) ((((actual - camera) * zoom) + halfScreen) - (drawSize / 2));
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
}
