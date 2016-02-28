package net.gegy1000.agarbot.network.packet;

import net.gegy1000.agarbot.Cell;
import net.gegy1000.agarbot.Main;
import net.gegy1000.agarbot.network.AgarByteBuffer;

import java.util.Arrays;

public class PacketClient16UpdateCells extends PacketAgarBase
{
    @Override
    public int getDiscriminator()
    {
        return 16;
    }

    @Override
    public void receive(AgarByteBuffer buffer)
    {
        short eats = buffer.readShort();

        for (int i = 0; i < eats; i++)
        {
            int eater = buffer.readInteger();
            int victim = buffer.readInteger();

            game.world.removeCell(victim);
        }

        int id;

        while ((id = buffer.readInteger()) != 0)
        {
            int x = buffer.readInteger();
            int y = buffer.readInteger();

            short size = buffer.readShort();

            byte r = buffer.readByte();
            byte g = buffer.readByte();
            byte b = buffer.readByte();

            byte flags = buffer.readByte();

            boolean isVirus = getFlag(flags, 1);
            boolean isAgitated = getFlag(flags, 16);

            if (getFlag(flags, 0b10))
            {
                buffer.incrementIndex(4 + buffer.readInteger());
            }

            String skin = "";

            if (getFlag(flags, 0b100))
            {
                skin = buffer.readNullStr8();
            }

            String name = buffer.readNullStr16();

            Cell cell = game.world.getCellById(id);

            if (cell == null)
            {
                cell = new Cell(game, name, id, x, y, size, skin);
                cell.setColour(r, g, b);
                cell.virus = isVirus;

                game.world.addCell(cell);
            }
            else
            {
                cell.skin = skin;
                cell.setPosition(x, y);
                cell.size = size;
                cell.virus = isVirus;
                cell.setColour(r, g, b);
            }
        }

        if (buffer.hasNext(4))
        {
            int removals = buffer.readInteger();

            for (int i = 0; i < removals; i++)
            {
                game.world.removeCell(buffer.readInteger());
            }
        }
    }

    private boolean getFlag(byte flags, int modifier)
    {
        return (flags & modifier) == modifier;
    }
}
