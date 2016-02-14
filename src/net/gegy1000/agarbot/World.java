package net.gegy1000.agarbot;

import net.gegy1000.agarbot.gui.AgarBotFrame;
import net.gegy1000.agarbot.network.packet.PacketServer0SetNick;
import net.gegy1000.agarbot.network.packet.PacketServer16Move;
import net.gegy1000.agarbot.network.packet.PacketServer17Split;
import net.gegy1000.agarbot.network.packet.PacketServer21EjectMass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World
{
    public List<Cell> cells = new ArrayList<Cell>();
    public List<Integer> playerIds = new ArrayList<Integer>();
    public Map<Integer, String> nameCache = new HashMap<Integer, String>();

    public Game game;

    public double minSizeX, minSizeY, maxSizeX, maxSizeY;

    public float moveX, moveY;

    public String[] leaderboard;

    public int level, exp, maxExp;

    public double zoomm;
    public double zoom;

    public World(Game game)
    {
        this.game = game;
    }

    public List<Cell> getCells()
    {
        return new ArrayList<>(cells);
    }

    public void removeCells(List<Cell> toDestroy)
    {
        cells.removeAll(toDestroy);
    }

    public void addCell(Cell cell)
    {
        cells.add(cell);
    }

    public void reset()
    {
        cells.clear();
    }

    public void addPlayerId(int id)
    {
        playerIds.add(id);

        System.out.println("Add player id " + id);
    }

    public void setLeaderboard(String[] leaderboard)
    {
        this.leaderboard = leaderboard;
    }

    public void setSize(double minX, double minY, double maxX, double maxY)
    {
        this.minSizeX = minX;
        this.minSizeY = minY;
        this.maxSizeX = maxX;
        this.maxSizeY = maxY;
    }

    public void setMove(float x, float y)
    {
        if (game.networkManager != null)
        {
            if (moveX != x || moveY != y)
            {
                this.moveX = x;
                this.moveY = y;

                game.networkManager.sendPacketToServer(new PacketServer16Move(x, y));
            }
        }
    }

    public void removeCell(int id)
    {
        Cell toRemove = getCellById(id);

        if (toRemove != null)
        {
            cells.remove(toRemove);
        }

        if (playerIds.contains(id))
        {
            playerIds.remove((Integer) id);
            playerDeath();
        }

        nameCache.remove(id);
    }

    public Cell getCellById(int id)
    {
        for (Cell cell : getCells())
        {
            if (cell != null && cell.id == id)
            {
                return cell;
            }
        }

        return null;
    }

    public List<Cell> getPlayerCells()
    {
        List<Cell> playerCells = new ArrayList<>();

        if (playerIds != null)
        {
            for (int playerId : new ArrayList<>(playerIds))
            {
                Cell player = getCellById(playerId);

                playerCells.add(player);
            }
        }

        return playerCells;
    }

    public void playerDeath()
    {
        System.out.println("Game Death");

        game.networkManager.sendPacketToServer(new PacketServer0SetNick(game.nick));

        moveX = 0;
        moveY = 0;
    }

    public void split()
    {
        game.networkManager.sendPacketToServer(new PacketServer17Split());
    }

    public void eject()
    {
        game.networkManager.sendPacketToServer(new PacketServer21EjectMass());
    }

    public void update()
    {
        int totalSize = 0;

        for(Cell player : getPlayerCells())
        {
            if (player != null)
            {
                totalSize += player.size;
            }
        }

        zoomm = AgarBotFrame.HEIGHT / (1024 / Math.pow(Math.min(64.0 / totalSize, 1), 0.4));

        if(zoomm > 1)
        {
            zoomm = 1;
        }

        if(zoomm == -1)
        {
            zoomm = zoom;
        }

        zoom += (zoomm - zoom) / 40f;
    }
}
