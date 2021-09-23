package br.unb.cic.comnet.bandits.algorithms;

import java.util.Map;

public class EpsilonGreedy extends EpsilonBanditAlgorithm {
	
	public EpsilonGreedy(double epsilon) {
		super("epsilon_greedy", epsilon);
	}
	
	public String choose(Map<String, Double> options, long round) {
		if (options.isEmpty()) return "";
		
		if (getRandom().nextDouble() < getEpsilon()) {
			return explore(options);
		}
		return exploit(options);
	}
}
