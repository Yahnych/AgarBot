package net.gegy1000.agarbot;

import net.gegy1000.agarbot.api.GameMode;
import net.gegy1000.agarbot.api.ServerData;
import net.gegy1000.agarbot.api.ServerLocation;
import net.gegy1000.agarbot.network.*;
import net.gegy1000.agarbot.network.packet.*;

import java.util.ArrayList;
import java.util.List;

public class Main
{
    public static final String VERSION = "154669603";
    private static final String NICK = "Learning Bot";

    public static List<Game> games = new ArrayList<Game>();

    public static void main(String[] args) throws Exception
    {
        // Client
        NetworkManager.registerClientPacket(16, PacketClient16UpdateCells.class);
        NetworkManager.registerClientPacket(17, PacketClient17SpectateUpdate.class);
        NetworkManager.registerClientPacket(18, PacketClient18ResetControllers.class);
        NetworkManager.registerClientPacket(20, PacketClient20Reset.class);
        NetworkManager.registerClientPacket(21, PacketClient21DrawDebugLine.class);
        NetworkManager.registerClientPacket(32, PacketClient32AddPlayerNode.class);
        NetworkManager.registerClientPacket(49, PacketClient49LeaderboardUpdate.class);
        NetworkManager.registerClientPacket(64, PacketClient64MapSize.class);
        NetworkManager.registerClientPacket(81, PacketClient81Exp.class);

        // Server
        NetworkManager.registerServerPacket(0, PacketServer0SetNick.class);
        NetworkManager.registerServerPacket(1, PacketServer1Spectate.class);
        NetworkManager.registerServerPacket(16, PacketServer16Move.class);
        NetworkManager.registerServerPacket(17, PacketServer17Split.class);
        NetworkManager.registerServerPacket(18, PacketServer18QPress.class);
        NetworkManager.registerServerPacket(19, PacketServer19QRelease.class);
        NetworkManager.registerServerPacket(20, PacketServer20Explode.class);
        NetworkManager.registerServerPacket(21, PacketServer21EjectMass.class);
        NetworkManager.registerServerPacket(80, PacketServer80SendToken.class);
        NetworkManager.registerServerPacket(254, PacketServer254Init1.class);
        NetworkManager.registerServerPacket(255, PacketServer255Init2.class);

        Game mainGame = new Game(NICK, ServerLocation.LONDON, GameMode.FFA, true);

        games.add(mainGame);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    for (Game game : games)
                    {
                        game.controlUpdate();
                    }
                }
            }
        }).start();

        double lastUpdated;
        final double ns = 1000000000.0 / 100.0;
        double delta = 0;
        lastUpdated = System.nanoTime();
        int updates = 0;
        long timer = System.currentTimeMillis();

        while (true)
        {
            long now = System.nanoTime();
            delta += (now - lastUpdated) / ns;
            lastUpdated = now;

            while(delta >= 1)
            {
                for (Game game : games)
                {
                    game.update();
                }

                updates++;
                delta--;
            }

            if(System.currentTimeMillis() - timer > 1000)
            {
                timer += 1000;
                updates = 0;
            }
        }
    }
}
