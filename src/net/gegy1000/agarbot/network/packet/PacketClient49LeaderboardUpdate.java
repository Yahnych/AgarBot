package net.gegy1000.agarbot.network.packet;

import net.gegy1000.agarbot.Main;
import net.gegy1000.agarbot.network.AgarByteBuffer;

public class PacketClient49LeaderboardUpdate extends PacketAgarBase
{
    @Override
    public void receive(AgarByteBuffer buffer)
    {
        int size = buffer.readInteger();

        String[] leaderboard = new String[size];

        for (int i = 0; i < size; i++)
        {
            String nick = "";

            short charShort;

            while ((charShort = buffer.readShort()) != 0)
            {
                nick += (char) charShort;
            }

            if (nick.length() <= 2)
            {
                nick = "An unnamed cell";
            }
            else
            {
                nick = nick.substring(2);
            }

            leaderboard[i] = nick;
        }

        game.world.setLeaderboard(leaderboard);
    }

    @Override
    public int getDiscriminator()
    {
        return 49;
    }
}
