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
}
