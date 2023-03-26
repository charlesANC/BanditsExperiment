package br.unb.cic.comnet.bandits.agents.trm;

import java.util.List;

import br.com.tm.repfogagent.trm.Rating;
import br.unb.cic.comnet.bandits.agents.ArmInfo;

public class ShortMeanEvaluator extends SimpleMeanEvaluator {
	
	private Double alpha;
	
	public ShortMeanEvaluator(Double alpha) {
		super();
		this.alpha = alpha;
	}
	
	@Override
	List<Rating> collectAllRatings(ArmInfo armInfo) {
		List<Rating> ratings = super.collectAllRatings(armInfo);
		sortRatings(ratings);
		
		int nAlpha = Double.valueOf((1 - alpha) * (ratings.size()+1)).intValue();
		
		double minDif = Double.MAX_VALUE;
		int mini = 0;
		for(int i = 0; i < ratings.size() - nAlpha; i++) {
			double dif = ratings.get(i + nAlpha).getNormalizedValue() - ratings.get(i).getNormalizedValue();
			if (dif < minDif) {
				minDif = dif;
				mini = i;
			}
		}
		
		return ratings.subList(mini, mini + nAlpha); 
	}
	
	void sortRatings(List<Rating> ratings) {
		ratings.sort(
			(a, b) -> Double.valueOf(a.getNormalizedValue())
							.compareTo(Double.valueOf(b.getNormalizedValue()))
		);
	}	

}
