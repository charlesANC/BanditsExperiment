package br.unb.cic.comnet.bandits.agents.trm;

import java.util.List;

import br.com.tm.repfogagent.trm.Rating;
import br.unb.cic.comnet.bandits.agents.ArmInfo;

public class AlphaTrimmedMeanEvaluator extends SimpleMeanEvaluator {
	
	Double alpha;
	
	public AlphaTrimmedMeanEvaluator(Double alpha) {
		super();
		this.alpha = alpha;
	}
	
	@Override
	List<Rating> collectAllRatings(ArmInfo armInfo) {
		List<Rating> ratings = super.collectAllRatings(armInfo);
		sortRatings(ratings);
		return cut(ratings);
	} 

	List<Rating> cut(List<Rating> ratings) {
		int lcut = Double.valueOf((ratings.size() + 1) * alpha).intValue();
		int rcut = ratings.size() - lcut;
		return ratings.subList(lcut, rcut);
	}
	
	void sortRatings(List<Rating> ratings) {
		ratings.sort(
			(a, b) -> Double.valueOf(a.getNormalizedValue())
							.compareTo(Double.valueOf(b.getNormalizedValue()))
		);
	}
}
