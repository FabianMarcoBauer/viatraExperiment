package experimental.partial;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class GeneratorSettings {
	
	private int sourceOnly = 0;
	private int targetOnly = 0;
	private Map<Integer, Integer> commonSizes = new HashMap<>();
	
	public GeneratorSettings() {
		
	}
	
	public GeneratorSettings(int sourceOnly, int targetOnly) {
		this.sourceOnly = sourceOnly;
		this.targetOnly = targetOnly;
	}
	
	public GeneratorSettings addCommonSize(int size, int count) {
		commonSizes.put(size, count);
		return this;
	}
	

	public int getSourceOnly() {
		return sourceOnly;
	}

	public int getTargetOnly() {
		return targetOnly;
	}

	public void forEachCommonSize(BiConsumer<? super Integer, ? super Integer> consumer) {
		commonSizes.forEach(consumer);
	}

}
