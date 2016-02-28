package net.gegy1000.agarbot;

import net.gegy1000.agarbot.api.GameMode;
import net.gegy1000.agarbot.api.ServerData;
import net.gegy1000.agarbot.api.ServerLocation;
import net.gegy1000.agarbot.gui.AgarBotFrame;
import net.gegy1000.agarbot.network.NetworkManager;

import javax.swing.JFrame;
import java.io.IOException;
import java.util.List;

public class Game
{
    public World world;
    public NetworkManager networkManager;

    public String nick;

    public PlayerController controller;

    public ServerData serverData;

    public ServerLocation serverLocation;
    public GameMode gameMode;

    public AgarBotFrame frame;

    public Game(String nick, ServerLocation serverLocation, GameMode mode, boolean openWindow) throws Exception
    {
        this(nick, openWindow);
        this.networkManager = NetworkManager.create(this, serverLocation, mode);
        this.serverLocation = serverLocation;
        this.gameMode = mode;
    }

    public Game(String nick, ServerData serverData, boolean openWindow) throws Exception
    {
        this(nick, openWindow);
        this.networkManager = NetworkManager.create(this, serverData);
        setServerData();
    }

    private Game(String nick, boolean openWindow)
    {
        this.world = new World(this);
        this.nick = nick;

        try
        {
            this.controller = new PlayerController(this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (openWindow)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    AgarBotFrame frame = new AgarBotFrame(Game.this);
                    Game.this.frame = frame;
                    frame.setVisible(true);

                    while (true)
                    {
                        frame.repaint();
                    }
                }
            }).start();
        }
    }

    private void setServerData()
    {
        this.serverData = new ServerData(networkManager.ip, networkManager.token);
    }

    public void update()
    {
        if (networkManager.isOpen)
        {
            world.update();
        }
    }

    public void controlUpdate()
    {
        if (networkManager.isOpen)
        {
            controller.tick(this);
        }
    }

    public void rejoin() throws Exception
    {
        if (networkManager.isOpen)
        {
            networkManager.close();
        }

        world.playerDeath();

        if (serverData != null)
        {
            networkManager = NetworkManager.create(this, serverData);
        }
        else
        {
            networkManager = NetworkManager.create(this, serverLocation, gameMode);
        }
    }
}
