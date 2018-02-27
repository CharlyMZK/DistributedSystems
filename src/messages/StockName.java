package src.messages;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum StockName {
	APL, IBM, MSFT, ORCL;
	
	private static final List<StockName> values = Collections.unmodifiableList(Arrays.asList(values()));
	private static final int size = values.size();
	private static final Random random = new Random();

	public static StockName randomType()  {
		return values.get(random.nextInt(size));
	}
}
