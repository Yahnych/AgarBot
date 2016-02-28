package net.gegy1000.agarbot.network.packet;

import net.gegy1000.agarbot.network.AgarByteBuffer;

public class PacketClient21DrawDebugLine extends PacketAgarBase
{
    @Override
    public void receive(AgarByteBuffer buffer)
    {
        game.world.setLineLocation(buffer.readShort(), buffer.readShort());
    }

    @Override
    public int getDiscriminator()
    {
        return 21;
    }
}
