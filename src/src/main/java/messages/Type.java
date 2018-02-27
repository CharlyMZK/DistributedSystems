package src.main.java.messages;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Type {
	BIDS,ASKS;

	private static final List<Type> values = Collections.unmodifiableList(Arrays.asList(values()));
	private static final int size = values.size();
	private static final Random random = new Random();

	public static Type randomType()  {
		return values.get(random.nextInt(size));
	}
}
