package net.gegy1000.agarbot.network.packet;

import net.gegy1000.agarbot.network.AgarByteBuffer;

public class PacketClient17SpectateUpdate extends PacketAgarBase
{
    @Override
    public int getDiscriminator()
    {
        return 17;
    }

    @Override
    public void receive(AgarByteBuffer buffer)
    {
        float viewX = buffer.readFloat();
        float viewY = buffer.readFloat();
        float viewZoom = buffer.readFloat();

        //TODO
    }
}
