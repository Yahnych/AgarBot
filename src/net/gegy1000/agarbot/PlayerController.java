package net.gegy1000.agarbot;

import com.pau101.neural.ComparatorLife;
import com.pau101.neural.Life;
import com.pau101.neural.NeuralNet;
import net.gegy1000.agarbot.gui.AgarBotFrame;
import net.gegy1000.agarbot.neural.save.NeuralNetSave;

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
    public static List<Life> lifePool = new ArrayList<>();
    public static long highestFitness;
    public static int[] layerSizes;
    public static int inputCount;
    public static int outputCount;

    public Life life;

    public float[] inputs;
    public float[] outputs;

    public static Random rng = new Random();

    public static final int SIZE_DIVISOR = 50;

    public static int inputWidth = AgarBotFrame.WIDTH / SIZE_DIVISOR;
    public static int inputHeight = AgarBotFrame.HEIGHT / SIZE_DIVISOR;

    private static final ComparatorLife LIFE_MOST_FIT_COMPARATOR = new ComparatorLife();

    private boolean firstTick;

    public static final File SAVE_FILE = new File("net/neural_net.nnet");

    public static NeuralNetSave save;

    public static void init()
    {
        try
        {
            inputCount = inputWidth * inputHeight;
            outputCount = 4;

            int hiddenLayerCount = (int) (Math.log((inputCount / outputCount)) / Math.log(2.0)) - 1;
            layerSizes = new int[hiddenLayerCount + 1];

            int prevSize = inputCount;

            for (int i = 0; i < layerSizes.length; i++)
            {
                int size = prevSize / 2;

                layerSizes[i] = size;

                prevSize = size;
            }

            for (int i = 0; i < 10; i++)
            {
                lifePool.add(new Life(NeuralNet.createRandom(layerSizes, inputCount)));
            }

            List<Game> games = Main.games;

            for (Game game : games)
            {
                PlayerController controller = new PlayerController();
                controller.updateLife();
                controller.initInputs();
                controller.initOutputs();
                controller.initializeRun(game);
                game.controller = controller;
            }

            if (SAVE_FILE.exists())
            {
                save = NeuralNetSave.construct(SAVE_FILE);
                save.read();
            }
            else
            {
                save = NeuralNetSave.construct(SAVE_FILE);

                SAVE_FILE.getParentFile().mkdirs();
                SAVE_FILE.createNewFile();

                save.save();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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

    public float[] getInputs(Game game)
    {
        try
        {
            float[] inputs = new float[inputCount];

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
                        return cell1.renderSize - cell2.renderSize;
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

            return inputs;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return inputs;
        }
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


    private void updateLife()
    {
        Life a = lifePool.get(rng.nextInt(lifePool.size()));
        Life b = lifePool.get(rng.nextInt(lifePool.size()));
        NeuralNet recombinant = a.getNeuralNet().crossover(b.getNeuralNet());
        recombinant.mutate();
        life = new Life(recombinant);

        try
        {
            if (save != null)
            {
                save.save();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void addLifeToPool(Life life)
    {
        lifePool.sort(LIFE_MOST_FIT_COMPARATOR);

        if (life.getFitness() > lifePool.get(lifePool.size() - 1).getFitness())
        {
            lifePool.add(life);
            lifePool.sort(LIFE_MOST_FIT_COMPARATOR);
            lifePool.remove(lifePool.size() - 1);
        }
    }

    public void tick(Game game)
    {
        if (firstTick)
        {
            firstTick = false;
            life.setFitness(0);
        }

        World world = game.world;

        if (world.isDead)
        {
            long fitness = world.getScore();

            life.setFitness(fitness);

            if (fitness > highestFitness)
            {
                highestFitness = fitness;
            }

            addLifeToPool(life);
            updateLife();

            System.out.println("Life: " + life + ", Fitness: " + fitness + ", Highest Fitness: " + highestFitness);

            initializeRun(game);
        }
        else
        {
            inputs = getInputs(game);
            float[] outputs = evaluate(inputs, life.getNeuralNet());

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
    }

    public void initializeRun(Game game)
    {
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
}
