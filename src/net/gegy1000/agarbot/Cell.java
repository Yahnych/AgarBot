package net.gegy1000.agarbot;

public class Cell
{
    public int id;
    public short size;
    public short renderSize;

    public int x, y;

    public int red, green, blue;
    public boolean virus;

    public Game game;

    public int prevX, prevY;
    public String skin;

    public int moveX, moveY;

    public int lastMoveTimer;

    public Cell(Game game, String name, int id, int x, int y, short size, String skin)
    {
        this.game = game;
        this.game.world.nameCache.put(id, name);
        this.id = id;
        this.size = size;
        this.renderSize = size;
        this.skin = skin;
        this.setPosition(x, y);
        this.lastMoveTimer = 50;
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
        return ((float) size / (float) cell.size) * 100.0F >= 125;
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

    public void setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public void update()
    {
        moveX = prevX - x;
        moveY = prevY - y;

        if (moveX != 0 || moveY != 0)
        {
            lastMoveTimer = 50;
        }
        else if (lastMoveTimer > 0)
        {
            lastMoveTimer--;
        }

        this.prevX = x;
        this.prevY = y;

        if (size != renderSize)
        {
            double diff = size - renderSize;

            renderSize += (int) Math.min(Math.max(diff / 16.0, -1.0), 1.0);
        }
    }
}
