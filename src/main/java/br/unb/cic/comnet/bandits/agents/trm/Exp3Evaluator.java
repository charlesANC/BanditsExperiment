package br.unb.cic.comnet.bandits.agents.trm;

import java.util.Collection;

import br.com.tm.repfogagent.trm.Rating;
import br.unb.cic.comnet.bandits.agents.ArmInfo;

public class Exp3Evaluator implements ArmsEvaluator {
	
	private Double gamma;
	
	public Exp3Evaluator(Double gamma) {
		this.gamma = gamma;
	}

	@Override
	public Collection<ArmInfo> evaluateArms(Collection<ArmInfo> armsInfo) {
		armsInfo.forEach(x -> updateArmWeight(x, armsInfo.size()));
		return armsInfo;
	}
	
	private void updateArmWeight(ArmInfo armInfo, Integer numArms) {
		double weight = 1D;
		for(Rating rating: armInfo.allRatings()) {
			weight = weight * Math.exp(-1 * gamma * (rating.getNormalizedValue() / weight) / numArms);
		}
		armInfo.setTrustworth(weight);
		armInfo.setReliability(1D);
	}

}
