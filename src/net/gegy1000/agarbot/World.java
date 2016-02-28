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

    public int moveX, moveY;

    public String[] leaderboard;

    public int level, exp, maxExp;

    public double zoomm;
    public double zoom;

    private int score;
    public boolean isDead;

    public long ticks;

    public int lineX, lineY;
    public boolean hasDebugLine;

    public int cameraX, cameraY;

    public long fitness;

    public World(Game game)
    {
        this.game = game;
    }

    public List<Cell> getCells()
    {
        return new ArrayList<>(cells);
    }

    public void addCell(Cell cell)
    {
        if (cell != null)
        {
            cells.add(cell);
        }
    }

    public void reset()
    {
        cells.clear();
        resetControllers();
    }

    public void addPlayerId(int id)
    {
        playerIds.add(id);
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

    public void setMove(int x, int y)
    {
        List<Cell> player = game.world.getPlayerCells();

        if (player.size() > 0)
        {
            int[] avg = getCameraPos();

            x += avg[0];
            y += avg[1];

            if (game.networkManager != null)
            {
                if (moveX != x || moveY != y)
                {
                    this.moveX = x;
                    this.moveY = y;

                    setLineLocation(x, y);

                    game.networkManager.sendPacketToServer(new PacketServer16Move(x, y));
                }
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

            if (playerIds.size() == 0)
            {
                playerDeath();
            }
        }

        nameCache.remove(id);
    }

    private int getAverage(int... values)
    {
        int value = 0;

        for (int v : values)
        {
            value += v;
        }

        return values.length != 0 ? value / values.length : 0;
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
            for (Integer playerId : new ArrayList<>(playerIds))
            {
                if (playerId != null)
                {
                    Cell player = getCellById(playerId);

                    if (player != null)
                    {
                        playerCells.add(player);
                    }
                }
            }
        }

        return playerCells;
    }

    public void playerDeath()
    {
        isDead = true;

        cells.clear();
        nameCache.clear();
        hasDebugLine = false;
    }

    public void respawn()
    {
        game.networkManager.sendPacketToServer(new PacketServer0SetNick(game.nick));

        moveX = 0;
        moveY = 0;

        isDead = false;
        score = 0;
        ticks = 0;
        fitness = 0;
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

        getScore();

        List<Cell> playerCells = getPlayerCells();

        for (Cell player : playerCells)
        {
            if (player != null)
            {
                totalSize += player.size;
            }
        }

        if (playerCells.size() > 0)
        {
            if (ticks % 100 == 0)
            {
                updateFitness();
            }
            
            int[] cameraPos = getCameraPos();

            cameraX = cameraPos[0];
            cameraY = cameraPos[1];

            for (Cell cell : getCells())
            {
                if (cell != null)
                {
                    cell.update();
                }
            }

            zoomm = AgarBotFrame.HEIGHT / (1024 / Math.pow(Math.min(64.0 / totalSize, 1), 0.4));
            zoom += (zoomm - zoom) / 40f;

            ticks++;
        }
    }

    public void updateFitness()
    {
        List<Cell> playerCells = getPlayerCells();

        if (isMoving(playerCells))
        {
            fitness += Math.max(0, (getCurrentScore(playerCells) - 10));
        }
    }

    public boolean isMoving(List<Cell> cells)
    {
        for (Cell cell : cells)
        {
            if (cell.lastMoveTimer > 0)
            {
                return true;
            }
        }

        return false;
    }

    public int getScore()
    {
        List<Cell> player = getPlayerCells();

        int currentScore = getCurrentScore(player);

        if (currentScore > score)
        {
            score = currentScore;
        }

        return score;
    }

    private int getCurrentScore(List<Cell> player)
    {
        int currentScore = 0;

        for (Cell p : player)
        {
            if (p != null)
            {
                currentScore += p.getMass();
            }
        }

        return currentScore;
    }

    public long getFitness()
    {
        return fitness;
    }

    public int[] getCameraPos()
    {
        List<Cell> player = getPlayerCells();

        int[] playerX = new int[player.size()];
        int[] playerY = new int[player.size()];

        int i = 0;

        for (Cell p : player)
        {
            if (p != null)
            {
                playerX[i] = p.x;
                playerY[i] = p.y;

                i++;
            }
        }

        return new int[] { getAverage(playerX), getAverage(playerY) };
    }

    public void resetControllers()
    {
        playerIds.clear();
    }

    public void setLineLocation(int x, int y)
    {
        this.lineX = x;
        this.lineY = y;
        this.hasDebugLine = true;
    }
}
