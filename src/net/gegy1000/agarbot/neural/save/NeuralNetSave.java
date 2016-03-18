package net.gegy1000.agarbot.neural.save;

import com.pau101.neural.Life;
import com.pau101.neural.NeuralNet;
import net.gegy1000.agarbot.Game;
import net.gegy1000.agarbot.Main;
import net.gegy1000.agarbot.PlayerController;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class NeuralNetSave
{
    private File file;
    private SaveByteBuffer buffer;

    private NeuralNetSave(File file, FileChannel channel) throws IOException
    {
        this.file = file;
        this.buffer = new SaveByteBuffer(channel);
    }

    public static NeuralNetSave construct(File file) throws IOException
    {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        FileChannel channel = randomAccessFile.getChannel();

        return new NeuralNetSave(file, channel);
    }

    public void read() throws IOException
    {
        buffer.resetIndex();

        long highestFitness = buffer.readLong();

        int lifePoolSize = buffer.readInteger();
        List<Life> lifePool = new ArrayList<>();

        for (int i = 0; i < lifePoolSize; i++)
        {
            lifePool.add(readLife());
        }

        PlayerController.highestFitness = highestFitness;
        PlayerController.lifePool = lifePool;

        List<Game> games = Main.games;

        for (Game game : games)
        {
            game.controller.life = readLife();
        }
    }

    private Life readLife() throws IOException
    {
        long fitness = buffer.readLong();

        int nodeCount = buffer.readInteger();
        int nodesLength = buffer.readInteger();
        byte[] nodes = buffer.readBytes(nodesLength);

        NeuralNet neuralNet = new NeuralNet(nodes, nodeCount);

        Life life = new Life(neuralNet);
        life.setFitness(fitness);
        return life;
    }

    public void save() throws IOException
    {
        buffer.resetIndex();

        buffer.writeLong(PlayerController.highestFitness);

        List<Life> lifePool = PlayerController.lifePool;

        buffer.writeInteger(lifePool.size());

        for (Life life : lifePool)
        {
            writeLife(life);
        }

        List<Game> games = Main.games;

        for (Game game : games)
        {
            writeLife(game.controller.life);
        }
    }

    private void writeLife(Life life) throws IOException
    {
        buffer.writeLong(life.getFitness());

        NeuralNet net = life.getNeuralNet();

        buffer.writeInteger(net.getNodeCount());

        byte[] nodes = net.getNodes();

        buffer.writeInteger(nodes.length);
        buffer.writeBytes(nodes);
    }
}
