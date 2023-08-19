package br.unb.cic.comnet.bandits.agents.trm;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import br.com.tm.repfogagent.trm.Rating;
import br.unb.cic.comnet.bandits.agents.ArmInfo;

public class SimpleMeanEvaluator implements ArmsEvaluator {

	@Override
	public Collection<ArmInfo> evaluateArms(Collection<ArmInfo> armsInfo) {
		for(ArmInfo armInfo: armsInfo) {
			armInfo.setTrustworth(calcAverage(collectAllRatings(armInfo)));
			armInfo.setReliability(1D);
		}
		return armsInfo;
	}
	
	double calcAverage(List<Rating> ratings) {
		return ratings.stream().collect(Collectors.averagingDouble(Rating::getNormalizedValue));
	}	
	
	List<Rating> collectAllRatings(ArmInfo armInfo) {
		return armInfo.allRatings();
	}	
}
