package net.gegy1000.agarbot;

public class Cell
{
    public int id;
    public short size;

    public int x, y;

    public String name;

    public int red, green, blue;
    public boolean virus;

    public Cell(String name, int id, int x, int y, short size)
    {
        this.name = name;
        this.id = id;
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void setColour(byte red, byte green, byte blue)
    {
        this.red = red + 128;
        this.green = green + 128;
        this.blue = blue + 128;
    }

    public void update()
    {

    }
}
