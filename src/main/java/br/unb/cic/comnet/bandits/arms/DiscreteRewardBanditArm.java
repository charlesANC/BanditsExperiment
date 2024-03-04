package br.unb.cic.comnet.bandits.arms;

import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.distribution.UniformRealDistribution;

public class DiscreteRewardBanditArm extends Arm {
	
	private UniformRealDistribution dist;
	
	private List<Double> simbols;
	private List<Double> probs;

	public DiscreteRewardBanditArm(String name, List<Double> simbols, List<Double> probs) {
		super(name);
		this.simbols = simbols;
		
		this.probs = probs;
		
		this.dist = new UniformRealDistribution();
	}

	@Override
	public double sample() {
		return decode(dist.sample());
	}
	
	private double decode(double sample) {
		int i = 0;
		double limit = 0D;
		
		for(; i < probs.size(); i++) {
			limit += probs.get(i);
			if (sample <= limit) {
				break;
			}
		}
		
		return simbols.get(i);
	}

}
