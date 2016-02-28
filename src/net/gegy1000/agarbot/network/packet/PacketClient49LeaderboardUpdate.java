package net.gegy1000.agarbot.network.packet;

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
            int minimumBlobId = buffer.readInteger();

            String nick = buffer.readNullStr16();

            if (nick.length() == 0)
            {
                nick = "An unnamed cell";
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
