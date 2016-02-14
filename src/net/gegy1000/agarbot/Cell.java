package net.gegy1000.agarbot;

public class Cell
{
    public int id;
    public short size;

    public int x, y;

    public int red, green, blue;
    public boolean virus;

    public Game game;

    public Cell(Game game, String name, int id, int x, int y, short size)
    {
        this.game = game;
        this.game.world.nameCache.put(id, name);
        this.id = id;
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void setColour(byte red, byte green, byte blue)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;

        if(red < 0)
        {
            this.red = red + 256;
        }

        if(green < 0)
        {
            this.green = green + 256;
        }

        if(blue < 0)
        {
            this.blue = blue + 256;
        }
    }

    public String getName()
    {
        String name = game.world.nameCache.get(id);

        if (name == null)
        {
            name = "";
        }

        return name;
    }

    public boolean canEat(Cell cell)
    {
        return ((float) size / (float) cell.size) * 100.0F > 125;
    }

    public double getDistance(Cell cell)
    {
        int distX = Math.abs(x - cell.x);
        int distY = Math.abs(y - cell.y);

        return Math.sqrt((distX * distX) + (distY * distY));
    }

    public int getMass()
    {
        return size * size / 100;
    }
}
