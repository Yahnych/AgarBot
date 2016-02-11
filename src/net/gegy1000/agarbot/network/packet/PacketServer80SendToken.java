package net.gegy1000.agarbot.network.packet;

import net.gegy1000.agarbot.network.AgarByteBuffer;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketServer80SendToken extends PacketAgarBase
{
    private String token;

    public PacketServer80SendToken(String token)
    {
        this.token = token;
    }

    public byte[] nameBytes()
    {
        byte[] charArray;

        try
        {
            charArray = this.token.getBytes("UTF-8");
            byte[] bytes = new byte[charArray.length];
            ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);

            for(byte c : charArray)
            {
                buffer.put(c);
            }

            return bytes;
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public byte[] send(AgarByteBuffer buffer)
    {
        buffer.writeBytes(nameBytes());

        return super.send(buffer);
    }

    @Override
    public int getDiscriminator()
    {
        return 80;
    }
}
