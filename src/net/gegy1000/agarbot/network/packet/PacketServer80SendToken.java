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

    @Override
    public byte[] send(AgarByteBuffer buffer)
    {
        buffer.writeEndStr8(token);

        return super.send(buffer);
    }

    @Override
    public int getDiscriminator()
    {
        return 80;
    }
}
