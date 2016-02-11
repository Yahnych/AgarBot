package net.gegy1000.agarbot.network.packet;

import net.gegy1000.agarbot.network.AgarByteBuffer;

public class PacketServer16Move extends PacketAgarBase
{
    private float x;
    private float y;

    public PacketServer16Move(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public byte[] send(AgarByteBuffer buffer)
    {
        buffer.writeFloat(x);
        buffer.writeFloat(y);
        buffer.writeInteger(0); // id

        return super.send(buffer);
    }

    @Override
    public int getDiscriminator()
    {
        return 16;
    }
}
