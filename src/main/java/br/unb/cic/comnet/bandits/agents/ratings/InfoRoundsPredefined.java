package br.unb.cic.comnet.bandits.agents.ratings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class InfoRoundsPredefined {

	private List<Opinion> opinions;
	
	public InfoRoundsPredefined(Collection<Opinion> opinions) {
		this.opinions = new ArrayList<Opinion>(opinions);
		this.opinions.sort((a, b) -> b.getRound().compareTo(a.getRound()));
	}
	
	// presup�e que a lista est� ordenada reversa por tempo
	public List<Opinion> getLastProductOpinions(int round) {
		int i = round;
		while (opinions.get(i).getRound() > i) i++;
		
		List<String> collected = new ArrayList<>();
		List<Opinion> result = new ArrayList<>();
		
		for(; i < opinions.size(); i++) {
			if(!collected.contains(opinions.get(i).getArm())) {
				collected.add(opinions.get(i).getArm());
				result.add(opinions.get(i));
			}
		}
		
		return result;
	}
	
	public Double accumulatedReward() {
		return Opinion.sumRatings(opinions);
	}
}