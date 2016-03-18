package net.gegy1000.agarbot.network.packet;

import net.gegy1000.agarbot.api.LoginType;
import net.gegy1000.agarbot.network.AgarByteBuffer;

public class PacketServer82Login extends PacketAgarBase
{
    private String token;
    private LoginType loginType;

    public PacketServer82Login(LoginType loginType, String token)
    {
        this.token = token;
        this.loginType = loginType;
    }

    @Override
    public byte[] send(AgarByteBuffer buffer)
    {
        buffer.writeByte((byte) loginType.getContextId());
        buffer.writeEndStr8(token);

        return super.send(buffer);
    }

    @Override
    public int getDiscriminator()
    {
        return 82;
    }
}
