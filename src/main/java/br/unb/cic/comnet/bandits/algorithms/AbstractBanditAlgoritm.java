package br.unb.cic.comnet.bandits.algorithms;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

public abstract class AbstractBanditAlgoritm implements BanditAlgorithm {
	
	private String name;
	private Random random;
	
	public String getName() {
		return name;
	}
	
	protected Random getRandom() {
		return random;
	}
	
	public AbstractBanditAlgoritm(String name) {
		this.name = name;
		this.random = new SecureRandom();
	}
	
	public String explore(Map<String, Double> options) {
		if (options.isEmpty()) return "";
		
		List<String> optionsNames = new ArrayList<String>(options.keySet());
		return optionsNames.get(random.nextInt(optionsNames.size()));		
	}
	
	public String exploit(Map<String, Double> options) {
		if (options.isEmpty()) return "";
		
		List<Entry<String, Double>> choiceEntries = new ArrayList<>(options.entrySet());
		choiceEntries.sort(Entry.comparingByValue());
		return choiceEntries.get(choiceEntries.size() - 1).getKey();		
	}	
}
