package br.unb.cic.comnet.bandits.arms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

class DiscreteRewardBanditArmTest {
	
	@Test
	void testaDistribuicaoDiscreta() {
		List<Double> simbols = List.of(1D, 2D, 3D, 4D, 5D);
		List<Double> probs = new ArrayList<>(List.of(0.0010, 0.0249, 0.9664, 0.0006, 0.0072));
		Integer[] cont = new Integer[] {0, 0, 0, 0, 0};
		
		DiscreteRewardBanditArm arm = new DiscreteRewardBanditArm("teste", simbols, probs);
		
		final int limit = 1000000;
		
		for(int i = 0; i < limit; i++) {
			Double sampled = Double.valueOf(arm.pull());
			int index = sampled.intValue() - 1;
			cont[index] = cont[index] + 1;
		}
		
		for(int i = 0; i < cont.length; i ++) {
			double prob = Double.valueOf(cont[i]) / limit;
			System.out.println(String.format("Probability of %d is %f (%d)", i, prob, cont[i]));
		}
	}

}
