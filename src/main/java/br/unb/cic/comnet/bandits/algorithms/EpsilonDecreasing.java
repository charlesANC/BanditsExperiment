package br.unb.cic.comnet.bandits.algorithms;

import java.util.Map;

public class EpsilonDecreasing extends EpsilonBanditAlgorithm {

	public EpsilonDecreasing(double epsilon) {
		super("epsilon_decreasing", epsilon);
	}

	@Override
	public String choose(Map<String, Double> options, long round) {
		if (options.isEmpty()) return "";
		
		if (getRandom().nextDouble() < getDecreasedEpsilon(round)) {
			return explore(options);
		}
		return exploit(options);
	}
	
	private double getDecreasedEpsilon(long round) {
		return getEpsilon() * 7 * (Math.log(round) / round);		
	}
}
