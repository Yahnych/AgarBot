package net.gegy1000.agarbot;

import com.pau101.neural.ComparatorLife;
import com.pau101.neural.Life;
import com.pau101.neural.NeuralNet;
import net.gegy1000.agarbot.gui.AgarBotFrame;
import net.gegy1000.agarbot.gui.GamePanel;
import net.gegy1000.agarbot.neural.save.NeuralNetSave;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * @author gegy1000, pau101
 */
public class PlayerController
{
    public Life[] lifePool;
    public int currentGeneration;
    public long highestFitness;
    public Life currentLife;
    public int currentLifeIndex;
    public int[] layerSizes;

    public float[] inputs;
    public float[] outputs;

    public Random rng = new Random();

    public NeuralNet currentNeuralNet;

    public int inputWidth = AgarBotFrame.WIDTH / SIZE_DIVISOR;
    public int inputHeight = AgarBotFrame.HEIGHT / SIZE_DIVISOR;

    public static final int SIZE_DIVISOR = 50;

    private static final ComparatorLife LIFE_MOST_FIT_COMPARATOR = new ComparatorLife();

    private boolean firstTick;

    public static final File SAVE_FILE = new File("net/neural_net.nnet");

    private int tick;

    private NeuralNetSave save;

    public PlayerController(Game game) throws IOException
    {
        if (SAVE_FILE.exists())
        {
            save = NeuralNetSave.construct(SAVE_FILE, this);
            save.read(game);
        }
        else
        {
            save = NeuralNetSave.construct(SAVE_FILE, this);

            initInputs();
            initOutputs();

            int inputCount = getInputCount();
            int outputCount = getOutputCount();

            int hiddenLayerCount = (int) (Math.log((inputCount / outputCount)) / Math.log(2.0)) - 1;
            layerSizes = new int[hiddenLayerCount + 1];

            int prevSize = inputCount;

            for (int i = 0; i < layerSizes.length; i++)
            {
                int size = prevSize / 2;

                layerSizes[i] = size;

                prevSize = size;
            }

            lifePool = new Life[20];

            for (int i = 0; i < lifePool.length; i++)
            {
                lifePool[i] = new Life(NeuralNet.createRandom(layerSizes, inputCount));
            }

            currentLifeIndex = 0;
            currentGeneration = 0;
            highestFitness = -1;

            initializeRun(game);
        }
    }

    public PlayerController(Game game, int currentGeneration, long highestFitness, int lifeIndex, int[] layerSizes, Life[] lifePool)
    {
        load(game, currentGeneration, highestFitness, lifeIndex, layerSizes, lifePool);
    }

    private void initInputs()
    {
        /**
         * inputWidth x inputHeight
         * First section is sizes
         * Next is 1 or 0 depending on if it is a player or not
         * Next is 1 or 0 depending on if it is a virus or not
         */

        inputs = new float[inputWidth * inputHeight];
    }

    private void initOutputs()
    {
        /**
         * 1: mouseX
         * 2: mouseY
         * 3: split
         * 4: eject mass
         */

        outputs = new float[4];
    }

    public int getInputCount()
    {
        return inputs.length;
    }

    public int getOutputCount()
    {
        return outputs.length;
    }

    public float[] getInputs(Game game)
    {
        float[] inputs = new float[getInputCount()];

        World world = game.world;
        List<Cell> player = world.getPlayerCells();

        if (player.size() > 0)
        {
            ArrayList<Cell> cells = new ArrayList<Cell>(world.cells);
            cells.sort(new Comparator<Cell>()
            {
                @Override
                public int compare(Cell cell1, Cell cell2)
                {
                    return cell1 != null && cell2 != null ? cell1.renderSize - cell2.renderSize : -1;
                }
            });

            int smallestPlayerSize = Integer.MAX_VALUE;
            Cell smallestPlayer = null;

            for (Cell p : player)
            {
                if (p != null)
                {
                    if (p.size < smallestPlayerSize)
                    {
                        smallestPlayer = p;
                        smallestPlayerSize = p.size;
                    }
                }
            }

            for (Cell cell : cells)
            {
                if (cell != null)
                {
                    inputs = drawCell(game, smallestPlayer, cell, player.contains(cell), inputs);
                }
            }
        }

//        for (int y = 0; y < inputHeight; y++)
//        {
//            for (int x = 0; x < inputWidth; x++)
//            {
//                int rgb = img.getRGB(x * widthRatio, y * heightRatio);
//
//                if (rgb != 0)
//                {
//                    int colour = rgb & 0xFF;
//
//                    if (colour == 1)
//                    {
//                        colour = 0;
//                    }
//                    else if (colour == 0)
//                    {
//                        colour = 128;
//                    }
//
//                    inputs[(y * inputWidth) + x] = (colour - 128) / 128.0F;
//                }
//            }
//        }

        return inputs;
    }

    public float[] drawCell(Game game, Cell smallestPlayer, Cell cell, boolean isPlayer, float[] inputs)
    {
        int actualX = cell.x;
        int actualY = cell.y;

        World world = game.world;

        double zoom = world.zoom;

        int drawSize = (int) ((cell.renderSize) * zoom);

        int relativeX = calculateRelativePos(world.cameraX, actualX, zoom, drawSize, AgarBotFrame.WIDTH / 2);
        int relativeY = calculateRelativePos(world.cameraY, actualY, zoom, drawSize, AgarBotFrame.HEIGHT / 2);

        float input;

        if (cell.virus)
        {
            input = -0.5F;
        }
        else if (isPlayer)
        {
            input = 0.5625F;
        }
        else
        {
            if (smallestPlayer != null && smallestPlayer.canEat(cell))
            {
                input = 1.0F;
            }
            else
            {
                input = -1.0F;
            }
        }

        for (int x = -drawSize; x < drawSize ; x++)
        {
            int height = (int)Math.sqrt(drawSize * drawSize - x * x);

            for (int y = -height; y < height; y++)
            {
                int drawX = relativeX + x + (drawSize / 2);
                int drawY = relativeY + y + (drawSize / 2);

                if (drawX > 0 && drawX < AgarBotFrame.WIDTH && drawY > 0 && drawY < AgarBotFrame.HEIGHT)
                {
                    drawX /= SIZE_DIVISOR;
                    drawY /= SIZE_DIVISOR;

                    int index = (drawY * inputWidth) + drawX;

                    float existing = inputs[index];

                    if (existing == 0 || (input < 0 ? input < existing : input > existing))
                    {
                        inputs[index] = input;
                    }
                }
            }
        }

//        for (int ring = 0; ring < drawSize; ring++)
//        {
//            for (int i = 0; i < 360; ++i)
//            {
//                int drawX = (int) (relativeX + (-Math.sin(i * (float) Math.PI / 180.0F) * ring)) - (drawSize / 2);
//                int drawY = (int) (relativeY + (Math.cos(i * (float) Math.PI / 180.0F) * ring)) - (drawSize / 2);
//
//                if (drawX > 0 && drawX < AgarBotFrame.WIDTH && drawY > 0 && drawY < AgarBotFrame.HEIGHT)
//                {
//                    drawX /= SIZE_DIVISOR;
//                    drawY /= SIZE_DIVISOR;
//
//                    inputs[(drawY * inputWidth) + drawX] = input;
//                }
//            }
//        }

        return inputs;
    }

    private int calculateRelativePos(int camera, int actual, double zoom, int drawSize, int halfScreen)
    {
        return (int) ((((actual - camera) * zoom) + halfScreen) - (drawSize / 2));
    }

    private float[] evaluate(float[] inputs, NeuralNet neuralNet)
    {
        float[] layer = null;
        float[] prevLayer = inputs;
        int nodeIndex = 0;

        for (int i = 0; i < layerSizes.length; i++)
        {
            layer = new float[layerSizes[i]];

            for (int m = 0; m < layer.length; m++)
            {
                for (int n = 0; n < prevLayer.length; n++)
                {
                    layer[m] = layer[m] + neuralNet.getNode(nodeIndex++).getValue() * prevLayer[n];
                }

                layer[m] = (float) Math.atan(layer[m] + neuralNet.getNode(nodeIndex++).getValue());
            }

            prevLayer = layer;
        }

        return layer;
    }

    private void createNewGeneration()
    {
        Arrays.sort(lifePool, LIFE_MOST_FIT_COMPARATOR);

        for (int i = lifePool.length / 2; i < lifePool.length; i++)
        {
            Life a = lifePool[rng.nextInt(lifePool.length / 2)];
            Life b = lifePool[rng.nextInt(lifePool.length / 2)];
            NeuralNet recombinant = a.getNeuralNet().crossover(b.getNeuralNet());
            recombinant.mutate();
            lifePool[i] = new Life(recombinant);
        }

        currentGeneration++;
    }

    private void updateLife()
    {
        currentLife = lifePool[currentLifeIndex];

        currentNeuralNet = currentLife.getNeuralNet();
    }

    public void tick(Game game)
    {
        if (firstTick)
        {
            firstTick = false;
            currentLife.setFitness(0);
        }

        World world = game.world;

        if (world.isDead)
        {
            long fitness = world.getFitness();

            currentLife.setFitness(fitness);

            if (fitness > highestFitness)
            {
                highestFitness = fitness;
            }

            System.out.println("Generation: " + (currentGeneration + 1) + ", Life: " + currentLife + ", Fitness: " + fitness + ", Highest Fitness: " + highestFitness);

            if (currentLifeIndex == lifePool.length - 1)
            {
                createNewGeneration();
                currentLifeIndex = lifePool.length / 2 + 1;
            }
            else
            {
                currentLifeIndex++;
            }

            initializeRun(game);
        }
        else
        {
            inputs = getInputs(game);
            float[] outputs = evaluate(inputs, currentNeuralNet);

            float mouseX = outputs[0];
            float mouseY = outputs[1];
            boolean split = outputs[2] > 0;
            boolean eject = outputs[3] > 0;

            if (split)
            {
                world.split();
            }

            if (eject)
            {
                world.eject();
            }

            world.setMove((int) (mouseX * 400), (int) (mouseY * 400));
        }

        tick++;
    }

    public void initializeRun(Game game)
    {
        try
        {
            if (!SAVE_FILE.exists())
            {
                SAVE_FILE.getParentFile().mkdirs();
                SAVE_FILE.createNewFile();
            }

            save.save();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        updateLife();
        firstTick = true;
        Arrays.fill(outputs, 0);
        Arrays.fill(inputs, 0);

        World world = game.world;

        if (world.isDead)
        {
            world.respawn();
        }
    }

    public void load(Game game, int currentGeneration, long highestFitness, int currentLifeIndex, int[] layerSizes, Life[] lifePool)
    {
        initInputs();
        initOutputs();

        this.currentGeneration = currentGeneration;
        this.highestFitness = highestFitness;
        this.currentLifeIndex = currentLifeIndex;
        this.layerSizes = layerSizes;
        this.lifePool = lifePool;

        initializeRun(game);
    }
}
