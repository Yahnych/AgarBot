package net.gegy1000.agarbot.network.packet;

import net.gegy1000.agarbot.Game;
import net.gegy1000.agarbot.network.AgarByteBuffer;

public class PacketClient81Exp extends PacketAgarBase
{
    @Override
    public void receive(AgarByteBuffer buffer)
    {
        Game.world.level = buffer.readInteger();
        Game.world.exp = buffer.readInteger();
        Game.world.maxExp = buffer.readInteger();
    }

    @Override
    public int getDiscriminator()
    {
        return 81;
    }
}
