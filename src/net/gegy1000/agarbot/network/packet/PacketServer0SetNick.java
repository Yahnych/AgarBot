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
        byte[] charBytes = new byte[this.nick.toCharArray().length * 2];

        int i = 0;

        for(char c : this.nick.toCharArray())
        {
            short charIndex = (short) c;

            charBytes[i] = (byte) (charIndex & 0b1);
            i++;
            charBytes[i] = (byte) (charIndex & 0b10);
            i++;
        }

        buffer.writeBytes(charBytes);

        return super.send(buffer);
    }

    @Override
    public int getDiscriminator()
    {
        return 0;
    }
}
