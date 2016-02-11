package net.gegy1000.agarbot.network.packet;

import net.gegy1000.agarbot.network.AgarByteBuffer;

public class PacketServer21EjectMass extends PacketAgarBase
{
    @Override
    public byte[] send(AgarByteBuffer buffer)
    {
        return super.send(buffer);
    }

    @Override
    public int getDiscriminator()
    {
        return 21;
    }
}
