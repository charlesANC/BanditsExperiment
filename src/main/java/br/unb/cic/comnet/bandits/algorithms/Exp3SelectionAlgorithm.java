package br.unb.cic.comnet.bandits.algorithms;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Exp3SelectionAlgorithm implements BanditAlgorithm {
	
	private Random random;
	
	public Exp3SelectionAlgorithm() {
		this.random = new SecureRandom();
	}
	
	@Override
	public String getName() {
		return "exp3";
	}

	@Override
	public String choose(Map<String, Double> options, long round) {
		if (options.isEmpty()) return null;
		
		Map<String, List<Long>> ranges = calculateRanges(options, 100);
		int move = random.nextInt(100);
		for(String optionName : ranges.keySet()) {
			if (ranges.get(optionName).get(0) <= move && ranges.get(optionName).get(1) >= move) {
				return optionName;
			}
		}
		
		return options.keySet().iterator().next();
	}
	
	private Map<String, List<Long>> calculateRanges(Map<String, Double> options, int max) {
		Double sumOfTrust = options.values().stream().collect(Collectors.summingDouble(x -> x)); 
		
		long inf = 0;
		
		Map<String, List<Long>> ranges = new HashMap<String, List<Long>>();
		
		for(String optionName : options.keySet()) {
			long sup = inf + Math.round(max * (options.get(optionName) / sumOfTrust)) - 1;
			ranges.put(optionName, Arrays.asList(inf, sup));
			inf = sup + 1;
		}
		
		return ranges;
	}	

}
