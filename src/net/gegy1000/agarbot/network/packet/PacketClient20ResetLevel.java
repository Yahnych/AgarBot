package net.gegy1000.agarbot.network.packet;

import net.gegy1000.agarbot.Game;
import net.gegy1000.agarbot.network.AgarByteBuffer;

public class PacketClient20ResetLevel extends PacketAgarBase
{
    @Override
    public void receive(AgarByteBuffer buffer)
    {
        Game.world.reset();
    }

    @Override
    public int getDiscriminator()
    {
        return 20;
    }
}
