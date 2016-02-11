package net.gegy1000.agarbot.network.packet;

import net.gegy1000.agarbot.network.AgarByteBuffer;

public class PacketServer254Init1 extends PacketAgarBase
{
    @Override
    public byte[] send(AgarByteBuffer buffer)
    {
        buffer.writeInteger(5);

        return super.send(buffer);
    }

    @Override
    public int getDiscriminator()
    {
        return 254;
    }
}
