package net.gegy1000.agarbot.network.packet;

import net.gegy1000.agarbot.Main;
import net.gegy1000.agarbot.network.AgarByteBuffer;

public class PacketClient81Exp extends PacketAgarBase
{
    @Override
    public void receive(AgarByteBuffer buffer)
    {
        game.world.level = buffer.readInteger();
        game.world.exp = buffer.readInteger();
        game.world.maxExp = buffer.readInteger();
    }

    @Override
    public int getDiscriminator()
    {
        return 81;
    }
}
