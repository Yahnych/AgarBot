package net.gegy1000.agarbot.network.packet;

import net.gegy1000.agarbot.Main;
import net.gegy1000.agarbot.network.AgarByteBuffer;

public class PacketClient64MapSize extends PacketAgarBase
{
    @Override
    public void receive(AgarByteBuffer buffer)
    {
        game.world.setSize(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    @Override
    public int getDiscriminator()
    {
        return 64;
    }
}
