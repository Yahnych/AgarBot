package net.gegy1000.agarbot.network.packet;

import net.gegy1000.agarbot.network.AgarByteBuffer;

public class PacketServer0SetNick extends PacketAgarBase
{
    private String nick;

    public PacketServer0SetNick(String nick)
    {
        this.nick = nick;
    }

    @Override
    public byte[] send(AgarByteBuffer buffer)
    {
        for(char c : this.nick.toCharArray())
        {
            short charIndex = (short) c;

            buffer.writeShort(charIndex);
        }

        buffer.writeShort((short) 0);

        return super.send(buffer);
    }

    @Override
    public int getDiscriminator()
    {
        return 0;
    }
}
