package br.unb.cic.comnet.bandits.algorithms;

import java.util.Map;

public class EpsilonFirst extends EpsilonBanditAlgorithm {
	
	private double horizon;
	
	public EpsilonFirst(double epsilon, double horizon) {
		super("epsilon_first", epsilon);
		this.horizon = horizon;
	}
	
	public String choose(Map<String, Double> choices, long round) {
		if (choices.isEmpty()) return "";
		
		long explorationPhase = Double.valueOf(getEpsilon() * horizon).longValue();
		
		if ((round % horizon) < explorationPhase) {
			return explore(choices);
		}
		return exploit(choices);
	}
}
