package net.gegy1000.agarbot;

import net.gegy1000.agarbot.api.GameMode;
import net.gegy1000.agarbot.api.ServerData;
import net.gegy1000.agarbot.api.ServerLocation;
import net.gegy1000.agarbot.gui.AgarBotFrame;
import net.gegy1000.agarbot.network.NetworkManager;

import javax.swing.JFrame;
import java.util.List;

public class Game
{
    public World world;
    public NetworkManager networkManager;

    public String nick;

    public Game(String nick, ServerLocation serverLocation, GameMode mode, boolean openWindow) throws Exception
    {
        this(nick, openWindow);
        this.networkManager = NetworkManager.create(this, serverLocation, mode);
    }

    public Game(String nick, ServerData serverData, boolean openWindow) throws Exception
    {
        this(nick, openWindow);
        this.networkManager = NetworkManager.create(this, serverData);
    }

    private Game(String nick, boolean openWindow)
    {
        this.world = new World(this);
        this.nick = nick;

        if (openWindow)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    JFrame frame = new AgarBotFrame(Game.this);
                    frame.setVisible(true);

                    while (true)
                    {
                        frame.repaint();
                    }
                }
            }).start();
        }
    }

    public void update()
    {
        if (networkManager.isOpen)
        {
            world.update();

            if (world.playerIds.size() > 0)
            {
                List<Cell> playerCells = world.getPlayerCells();

                if (playerCells.size() > 0)
                {
                    Cell player = playerCells.get(0);

                    if (player != null)
                    {
                        Cell eat = null;

                        int highestScore = Integer.MIN_VALUE;

                        for (Cell cell : world.getCells())
                        {
                            if (cell != null && !cell.virus)
                            {
                                if (player.canEat(cell))
                                {
                                    short size = cell.size;

                                    int score = (int) (size * 1000.0 / player.getDistance(cell));

                                    if (score > highestScore)
                                    {
                                        eat = cell;
                                        highestScore = score;
                                    }
                                }
                            }
                        }

                        if (eat != null)
                        {
                            world.setMove(eat.x - player.x, eat.y - player.y);
                        }
                    }
                }
            }
        }
    }
}
