package br.unb.cic.comnet.bandits.algorithms;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UCB1 implements BanditAlgorithm {
	
	private Map<String, Long> chosen;
	
	public String getName() {
		return "ucb1";
	}
	
	public UCB1() {
		this.chosen = new ConcurrentHashMap<String, Long>();
	}
	
	public String choose(Map<String, Double> options, long round) {
		if (options.isEmpty()) return "";
		
		double maxUpperBound = 0D;
		String maxChoice = "";
		for(String arm : options.keySet()) {
			if (!chosen.containsKey(arm)) {
				chosen.put(arm, 1L);
				return arm;
			}
			double upperBound = calculateUpperBound(arm, options.get(arm), round);
			if (upperBound > maxUpperBound) {
				maxUpperBound = upperBound;
				maxChoice = arm;
			} 
		}
		chosen.replace(maxChoice, chosen.get(maxChoice) + 1); 
		return maxChoice;
	}

	private double calculateUpperBound(String arm, Double reward, long round) {
		return reward + Math.sqrt(2 * Math.log(round / chosen.get(arm)));
	}
}
