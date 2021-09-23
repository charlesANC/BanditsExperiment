package br.unb.cic.comnet.bandits.algorithms;

import java.security.InvalidParameterException;
import java.util.Objects;

import br.unb.cic.comnet.bandits.environment.GeneralParameters;

public class BanditAlgorithmFactory {
	
	public static BanditAlgorithm create(String name) {
		return create(name, GeneralParameters.EPSILON, GeneralParameters.NUM_OF_ROUNDS);
	}
	
	public static BanditAlgorithm create(String name, double epsilon, long horizon) {
		Objects.requireNonNull(name, "Parameter name can not be null.");
		
		if (name.equals("simple_averaging")) {
			return new OnlyExploiting();
		}
		
		if (name.equals("epsilon_greedy")) {
			return new EpsilonGreedy(epsilon);
		}
		
		if (name.equals("epsilon_first")) {
			return new EpsilonFirst(epsilon, horizon);
		}
		
		if (name.equals("epsilon_decreasing")) {
			return new EpsilonDecreasing(epsilon);
		}		
		
		if (name.equals("ucb1")) {
			return new UCB1();
		}
		
		if (name.equals("only_exploiting")) {
			return new OnlyExploiting();
		}		
		
		throw new InvalidParameterException(name + " is not a valid bandit algorithm name.");
	}

}
