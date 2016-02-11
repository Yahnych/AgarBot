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
        int eats = buffer.readShort();

        for (int i = 0; i < eats; i++)
        {
            int eaterId = buffer.readInteger(); // What do we use this for?
            int victimId = buffer.readInteger();

            Game.world.removeCell(victimId);
        }

        addCell(buffer);

//        int removals = buffer.readInteger(); //TODO
//
//        System.out.println("Removing " + removals + " cells!");
//
//        for (int i = 0; i < removals; i++)
//        {
//            int id = buffer.readInteger();
//            Game.world.removeCell(id);
//        }
    }

    private void addCell(AgarByteBuffer buffer)
    {
        int id = buffer.readInteger();

        if (id != 0)
        {
            int x = buffer.readInteger();
            int y = buffer.readInteger();

            short size = buffer.readShort();

            byte r = buffer.readByte();
            byte g = buffer.readByte();
            byte b = buffer.readByte();

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
                skips += 8;
            }

            if (getFlag(flags, (byte) 0b1000))
            {
                skips += 16;
            }

            buffer.incrementIndex(skips);

            short charShort;

            String name = "";

            while ((charShort = buffer.readShort()) != 0)
            {
                name += (char) charShort;
            }

            Cell cell = Game.world.getCellById(id);

            if (cell == null)
            {
                cell = new Cell(name, id, x, y, size);
                cell.setColour(r, g, b);
                cell.virus = isVirus;

                Game.world.addCell(cell);
            }
            else
            {
                cell.name = name;
                cell.x = x;
                cell.y = y;
                cell.size = size;
                cell.virus = isVirus;
                cell.setColour(r, g, b);
            }
        }
    }

    private boolean getFlag(byte flags, int index)
    {
        return (flags & index) == 1;
    }
}
