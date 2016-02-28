package com.pau101.neural;

import java.util.Random;

/**
 * @author pau101
 */
public enum NodeBase {
	NEGATIVE(), NEUTRAL(), POSITIVE();

	private static final Random BASE_CHOOSER_RNG = new Random();

	private NodeBase() {
	}

	public byte getValue() {
		return (byte) (ordinal() - 1);
	}

	public static NodeBase random() {
		NodeBase[] values = values();
		return values[BASE_CHOOSER_RNG.nextInt(values.length)];
	}

	public static NodeBase valueOf(int ordinal) {
		NodeBase[] values = values();
		return values[ordinal < 0 || ordinal >= values.length ? 0 : ordinal];
	}
}
