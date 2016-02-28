package net.gegy1000.agarbot.network.packet;

import net.gegy1000.agarbot.api.GameMode;
import net.gegy1000.agarbot.network.AgarByteBuffer;

public class PacketClient64MapSize extends PacketAgarBase
{
    @Override
    public void receive(AgarByteBuffer buffer)
    {
        game.world.setSize(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble());

        if (buffer.hasNext(4))
        {
            int gameModeIndex = buffer.readInteger();
            GameMode gameMode = GameMode.FFA;

            switch (gameModeIndex)
            {
                case 1:
                    gameMode = GameMode.TEAMS;
                    break;
                case 4:
                    gameMode = GameMode.EXPERIMENTAL;
                    break;
                case 8:
                    gameMode = GameMode.PARTY;
                    break;
            }


        }
    }

    @Override
    public int getDiscriminator()
    {
        return 64;
    }
}
