package net.gegy1000.agarbot;

import net.gegy1000.agarbot.api.ServerData;
import net.gegy1000.agarbot.network.NetworkManager;
import net.gegy1000.agarbot.network.packet.PacketClient16UpdateCells;
import net.gegy1000.agarbot.network.packet.PacketClient17SpectateUpdate;
import net.gegy1000.agarbot.network.packet.PacketClient18ResetControllers;
import net.gegy1000.agarbot.network.packet.PacketClient20Reset;
import net.gegy1000.agarbot.network.packet.PacketClient21DrawDebugLine;
import net.gegy1000.agarbot.network.packet.PacketClient32AddPlayerNode;
import net.gegy1000.agarbot.network.packet.PacketClient49LeaderboardUpdate;
import net.gegy1000.agarbot.network.packet.PacketClient64MapSize;
import net.gegy1000.agarbot.network.packet.PacketClient81Exp;
import net.gegy1000.agarbot.network.packet.PacketServer0SetNick;
import net.gegy1000.agarbot.network.packet.PacketServer16Move;
import net.gegy1000.agarbot.network.packet.PacketServer17Split;
import net.gegy1000.agarbot.network.packet.PacketServer18QPress;
import net.gegy1000.agarbot.network.packet.PacketServer19QRelease;
import net.gegy1000.agarbot.network.packet.PacketServer1Spectate;
import net.gegy1000.agarbot.network.packet.PacketServer20Explode;
import net.gegy1000.agarbot.network.packet.PacketServer21EjectMass;
import net.gegy1000.agarbot.network.packet.PacketServer254Init1;
import net.gegy1000.agarbot.network.packet.PacketServer255Init2;
import net.gegy1000.agarbot.network.packet.PacketServer80SendToken;
import net.gegy1000.agarbot.network.packet.PacketServer82Login;

import java.util.ArrayList;
import java.util.List;

public class Main
{
    public static final String VERSION = "154669603";
    private static final String NICK = "Bot";

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
        NetworkManager.registerServerPacket(82, PacketServer82Login.class);
        NetworkManager.registerServerPacket(254, PacketServer254Init1.class);
        NetworkManager.registerServerPacket(255, PacketServer255Init2.class);

        ServerData serverData = new ServerData("127.0.0.1:10101", "");

        for (int i = 0; i < 100; i++)
        {
            games.add(new Game(NICK + " " + i, serverData, i == 0));
        }

        PlayerController.init();

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
