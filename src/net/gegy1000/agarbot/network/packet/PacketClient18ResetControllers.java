package net.gegy1000.agarbot.network.packet;

import net.gegy1000.agarbot.network.AgarByteBuffer;

public class PacketClient18ResetControllers extends PacketAgarBase
{
    @Override
    public void receive(AgarByteBuffer buffer)
    {
        game.world.resetControllers();
    }

    @Override
    public int getDiscriminator()
    {
        return 18;
    }
}
