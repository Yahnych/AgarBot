package net.gegy1000.agarbot.neural.save;

import com.pau101.neural.Life;
import com.pau101.neural.NeuralNet;
import net.gegy1000.agarbot.Game;
import net.gegy1000.agarbot.PlayerController;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class NeuralNetSave
{
    private File file;
    private SaveByteBuffer buffer;
    private PlayerController controller;

    private NeuralNetSave(File file, FileChannel channel, PlayerController controller) throws IOException
    {
        this.file = file;
        this.buffer = new SaveByteBuffer(channel);
        this.controller = controller;
    }

    public static NeuralNetSave construct(File file, PlayerController controller) throws IOException
    {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        FileChannel channel = randomAccessFile.getChannel();

        return new NeuralNetSave(file, channel, controller);
    }

    public void read(Game game) throws IOException
    {
        buffer.resetIndex();

        int currentGeneration = buffer.readInteger();
        long highestFitness = buffer.readLong();
        int currentLifeIndex = buffer.readInteger();

        int layerSizesCount = buffer.readInteger();

        int[] layerSizes = new int[layerSizesCount];

        for (int i = 0; i < layerSizesCount; i++)
        {
            layerSizes[i] = buffer.readInteger();
        }

        int lifePoolSize = buffer.readInteger();
        Life[] lifePool = new Life[lifePoolSize];

        for (int i = 0; i < lifePoolSize; i++)
        {
            long fitness = buffer.readLong();

            int nodeCount = buffer.readInteger();
            int nodesLength = buffer.readInteger();
            byte[] nodes = buffer.readBytes(nodesLength);

            NeuralNet neuralNet = new NeuralNet(nodes, nodeCount);

            Life life = new Life(neuralNet);
            life.setFitness(fitness);

            lifePool[i] = life;
        }

        controller.load(game, currentGeneration, highestFitness, currentLifeIndex, layerSizes, lifePool);
    }

    public void save() throws IOException
    {
        buffer.resetIndex();

        buffer.writeInteger(controller.currentGeneration);
        buffer.writeLong(controller.highestFitness);
        buffer.writeInteger(controller.currentLifeIndex);

        int[] layerSizes = controller.layerSizes;

        buffer.writeInteger(layerSizes.length);

        for (int layerSize : layerSizes)
        {
            buffer.writeInteger(layerSize);
        }

        Life[] lifePool = controller.lifePool;

        buffer.writeInteger(lifePool.length);

        for (Life life : lifePool)
        {
            buffer.writeLong(life.getFitness());

            NeuralNet net = life.getNeuralNet();

            buffer.writeInteger(net.getNodeCount());

            byte[] nodes = net.getNodes();

            buffer.writeInteger(nodes.length);
            buffer.writeBytes(nodes);
        }
    }
}
