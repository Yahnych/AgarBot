package com.pau101.neural;

import java.util.Comparator;

/**
 * @author pau101
 */
public class ComparatorLife implements Comparator<Life> {
	@Override
	public int compare(Life a, Life b) {
		return Long.compare(b.getFitness(), a.getFitness());
	}
}
