package net.gegy1000.agarbot.network.packet;

import net.gegy1000.agarbot.network.AgarByteBuffer;

public class PacketServer255Init2 extends PacketAgarBase
{
    @Override
    public byte[] send(AgarByteBuffer buffer)
    {
        buffer.writeInteger((int) 2200049715L);

        return super.send(buffer);
    }

    @Override
    public int getDiscriminator()
    {
        return 255;
    }
}
