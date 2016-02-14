package net.gegy1000.agarbot.network.packet;

import net.gegy1000.agarbot.Cell;
import net.gegy1000.agarbot.Main;
import net.gegy1000.agarbot.network.AgarByteBuffer;

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

        for(int i = 0; i < eats; i++)
        {
            int eaten = buffer.readInteger();
            int victim = buffer.readInteger();

            game.world.removeCell(victim);
        }

        int id;

        while ((id = buffer.readInteger()) != 0)
        {
            int x = buffer.readInteger();
            int y = buffer.readInteger();

            short size = buffer.readShort();

            byte red = buffer.readByte();
            byte green = buffer.readByte();
            byte blue = buffer.readByte();

            byte flags = buffer.readByte();

            boolean isVirus = getFlag(flags, 1);
            boolean isAgitated = getFlag(flags, 16); //What does this do?

            int skips = 0;

            if (getFlag(flags, (byte) 2))
            {
                skips += 4;
            }

            if (getFlag(flags, (byte) 4))
            {
                String str = "";

                byte c;

                while ((c = buffer.readByte()) != 0)
                {
                    str += (char) c;
                }

                System.out.println("Skin: " + str);
            }

            if (getFlag(flags, (byte) 8))
            {
                skips += 16;
            }

            buffer.incrementIndex(skips);

            String name = "";

            short c;

            while ((c = buffer.readShort()) != 0)
            {
                name += (char) c;
            }

            Cell cell = game.world.getCellById(id);

            if (cell == null)
            {
                cell = new Cell(game, name, id, x, y, size);
                cell.setColour(red, green, blue);
                cell.virus = isVirus;

                game.world.addCell(cell);
            }
            else
            {
//                cell.name = name;
                cell.x = x;
                cell.y = y;
                cell.size = size;
                cell.virus = isVirus;
                cell.setColour(red, green, blue);
            }
        }

        if (buffer.hasNext(4))
        {
            int removals = buffer.readInteger();

            for (int i = 0; i < removals; i++)
            {
                if (!buffer.hasNext(4))
                {
                    break;
                }

                game.world.removeCell(buffer.readInteger());
            }
        }
    }

    private boolean getFlag(byte flags, int index)
    {
        return (flags & index) == 1;
    }
}
