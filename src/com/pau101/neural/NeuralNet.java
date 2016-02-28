package com.pau101.neural;

import java.util.Random;

/**
 * @author pau101
 */
public class NeuralNet {
	private static final Random RNG = new Random();

	private final byte[] nodes;

	private final int nodeCount;

	public NeuralNet(byte[] nodes, int nodeCount) {
		this.nodes = nodes;
		this.nodeCount = nodeCount;
	}

	public void setNode(int index, NodeBase base) {
		int byteIndex = getByteForIndex(index);
		int bitOffset = getBitForIndex(index);
		nodes[byteIndex] = (byte) (~(~nodes[byteIndex] | 3 << bitOffset) | base.ordinal() << bitOffset);
	}

	public NodeBase getNode(int index) {
		int byteIndex = getByteForIndex(index);
		int bitOffset = getBitForIndex(index);
		return NodeBase.valueOf(nodes[byteIndex] & 3 << bitOffset >>> bitOffset);
	}

	public NeuralNet crossover(NeuralNet neuralNet) {
		byte[] nodes = new byte[this.nodes.length];
		NeuralNet recombinant = new NeuralNet(nodes, nodeCount);
		boolean isCrossing = true;
		for (int i = 0; i < nodeCount; i++) {
			if (RNG.nextInt(nodeCount / 2) == 0) {
				isCrossing = !isCrossing;
			}
			recombinant.setNode(i, isCrossing ? neuralNet.getNode(i) : getNode(i));
		}
		return recombinant;
	}

	public void mutate() {
		for (int i = 0; i < nodeCount; i++) {
			if (RNG.nextInt(50) == 0) {
				setNode(i, NodeBase.random());
			}
		}
	}

	public int getConnectionCost() {
		int cost = 0;
		for (int i = 0; i < nodeCount; i++) {
			int nodeValue = getNode(i).getValue();
			cost += nodeValue * nodeValue;
		}
		return cost;
	}

	private static int getByteForIndex(int index) {
		return index / 4;
	}

	private static int getBitForIndex(int index) {
		return index % 4 * 2;
	}

	private static int getByteCountForNodeCount(int length) {
		return length / 4 + (length % 4 == 0 ? 0 : 1);
	}

	public static NeuralNet createRandom(int[] reductionLayers, int inputCount) {
		int nodeCount = 0;
		int prevSize = inputCount;
		for (int reductionSize : reductionLayers) {
			nodeCount += reductionSize * prevSize + reductionSize;
			prevSize = reductionSize;
		}
		byte[] nodes = new byte[getByteCountForNodeCount(nodeCount)];
		NeuralNet neuralNet = new NeuralNet(nodes, nodeCount);
		int index = 0;
		prevSize = inputCount;
		for (int layer = 0; layer < reductionLayers.length; layer++) {
			int reductionLayerSize = reductionLayers[layer];
			for (int m = 0; m < reductionLayerSize; m++) {
				for (int n = 0; n < prevSize; n++) {
					neuralNet.setNode(index++, RNG.nextInt(reductionLayerSize * 3 / 4 + 1) == 0 ? NodeBase.random() : NodeBase.NEUTRAL);
				}
				neuralNet.setNode(index++, RNG.nextInt(reductionLayerSize * 3 / 4 + 1) == 0 ? NodeBase.random() : NodeBase.NEUTRAL);
			}
			prevSize = reductionLayerSize;
		}
		return neuralNet;
	}

	public byte[] getNodes()
	{
		return nodes;
	}

	public int getNodeCount()
	{
		return nodeCount;
	}
}
