package net.gegy1000.agarbot.network.packet;

import net.gegy1000.agarbot.network.AgarByteBuffer;

public class PacketServer16Move extends PacketAgarBase
{
    private int x;
    private int y;

    public PacketServer16Move(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public byte[] send(AgarByteBuffer buffer)
    {
        buffer.writeInteger(x);
        buffer.writeInteger(y);
        buffer.writeInteger(0); // id

        return super.send(buffer);
    }

    @Override
    public int getDiscriminator()
    {
        return 16;
    }
}
