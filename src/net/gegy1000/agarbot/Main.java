package net.gegy1000.agarbot;

import net.gegy1000.agarbot.api.GameMode;
import net.gegy1000.agarbot.api.ServerData;
import net.gegy1000.agarbot.api.ServerLocation;
import net.gegy1000.agarbot.gui.AgarBotFrame;
import net.gegy1000.agarbot.network.*;
import net.gegy1000.agarbot.network.packet.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Main
{
    public static final String VERSION = "154669603";
    private static final String NICK = "NotABot";

    public static List<Game> games = new ArrayList<Game>();

    public static void main(String[] args) throws Exception
    {
        // Client
        NetworkManager.registerClientPacket(16, PacketClient16UpdateCells.class);
        NetworkManager.registerClientPacket(20, PacketClient20ResetLevel.class);
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

        Game mainGame = new Game(NICK, ServerLocation.LONDON, GameMode.PARTY, true);

        games.add(mainGame);

        while (true)
        {
            for (Game game : games)
            {
                game.update();
            }
        }
    }
}
