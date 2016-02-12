package net.gegy1000.agarbot.network.packet;

import net.gegy1000.agarbot.Cell;
import net.gegy1000.agarbot.Game;
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

            Game.world.removeCell(victim);
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

            boolean isVirus = getFlag(flags, 0b1);
            boolean isAgitated = getFlag(flags, 0b100000); //What does this do?

            int skips = 0;

            if (getFlag(flags, (byte) 0b10))
            {
                skips += 4;
            }

            if (getFlag(flags, (byte) 0b100))
            {
                String str = "";

                byte c;

                while ((c = buffer.readByte()) != 0)
                {
                    str += (char) c;
                }

                System.out.println("Some string " + str);
            }

            if (getFlag(flags, (byte) 0b1000))
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

            Cell cell = Game.world.getCellById(id);

            if (cell == null)
            {
                cell = new Cell(name, id, x, y, size);
                cell.setColour(red, green, blue);
                cell.virus = isVirus;

                Game.world.addCell(cell);
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

        int removals = buffer.readInteger();

        for (int i = 0; i < removals; i++)
        {
            if (!buffer.hasNext(4))
            {
                break;
            }

            Game.world.removeCell(buffer.readInteger());
        }
    }

    private boolean getFlag(byte flags, int index)
    {
        return (flags & index) == 1;
    }
}
