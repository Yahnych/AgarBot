package com.pau101.neural;

/**
 * @author pau101
 */
public class Life {
	private NeuralNet neuralNet;

	private long fitness;

	public Life(NeuralNet neuralNet) {
		this.neuralNet = neuralNet;
		this.fitness = 0;
	}

	public void setFitness(long fitness) {
		this.fitness = fitness;
	}

	public long getFitness() {
		return fitness;
	}

	public NeuralNet getNeuralNet() {
		return neuralNet;
	}

	@Override
	public String toString()
	{
		return "Life:{Fitness=" + fitness + "}";
	}
}
