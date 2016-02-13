package net.gegy1000.agarbot.network.packet;

import net.gegy1000.agarbot.network.AgarByteBuffer;

public class PacketClient32AddPlayerNode extends PacketAgarBase
{
    @Override
    public void receive(AgarByteBuffer buffer)
    {
        game.world.addPlayerId(buffer.readInteger());
    }

    @Override
    public int getDiscriminator()
    {
        return 32;
    }
}
