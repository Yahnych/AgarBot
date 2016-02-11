package net.gegy1000.agarbot.network.packet;

import net.gegy1000.agarbot.network.AgarByteBuffer;

public abstract class PacketAgarBase
{
    public PacketAgarBase() {}

    public byte[] send(AgarByteBuffer buffer)
    {
        return buffer.toBytes();
    }

    public void receive(AgarByteBuffer buffer)
    {
    }

    public abstract int getDiscriminator();
}
