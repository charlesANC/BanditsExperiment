package br.unb.cic.comnet.bandits.agents.trm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.tm.repfogagent.trm.FIREtrm;
import br.com.tm.repfogagent.trm.Rating;
import br.com.tm.repfogagent.trm.components.InteractionTrustComponent;
import br.com.tm.repfogagent.trm.components.WitnessReputationComponent;
import br.unb.cic.comnet.bandits.agents.ArmInfo;
import jade.util.Logger;

public class FIRETranscoderEvaluator {
	
	Logger logger = Logger.getJADELogger(getClass().getName());	
	
	public void evaluateTranscoders(Collection<ArmInfo> armsInfo, String accreditedArm) {

		StringBuilder str = new StringBuilder("\r\n---\r\n");
		
		for(ArmInfo armInfo : armsInfo) {
			if (!armInfo.getEvaluations().isEmpty()) {
				
				List<Rating> local = new ArrayList<>();
				List<Rating> supportingRatings = new ArrayList<>();				
				Map<String, List<Rating>> ratingsPerNode = new LinkedHashMap<>();
				
				splitRatings(armInfo.getEvaluations(), accreditedArm, local, supportingRatings, ratingsPerNode);
				
				if (!local.isEmpty() && !supportingRatings.isEmpty()) {
					WitnessReputationComponent witnessComponent = new WitnessReputationComponent(0, 0.4, 0.8, ratingsPerNode, local);
					
					double witnessValue = witnessComponent.calculate(supportingRatings, supportingRatings.size());
					double reliabilityWR = witnessComponent.reliability(supportingRatings);
					
					witnessComponent.setCalculatedValue(witnessValue);
					witnessComponent.setCalculatedReliability(reliabilityWR);
					
					InteractionTrustComponent directComponent = new InteractionTrustComponent(0,  0.8);				
					
					if (!local.isEmpty()) {
						double interactionTrustValue = directComponent.calculate(local, local.size());
						double reliabilityIT = directComponent.reliability(local);
						
						directComponent.setCalculatedValue(interactionTrustValue);
						directComponent.setCalculatedReliability(reliabilityIT);					
					} else {
						directComponent.setCalculatedValue(1.0);
						directComponent.setCalculatedReliability(1.0);					
					}
					
					FIREtrm firEtrm = new FIREtrm(Arrays.asList(witnessComponent, directComponent));
					Double fireValue = firEtrm.calculate();
					Double overallReliability = firEtrm.reliability();				
					
					armInfo.setTrustworth(fireValue);
					armInfo.setReliability(overallReliability);

					str.append("Trustworthy of " + armInfo.getName() + " is " + fireValue + "\r\n");				
				}
			}
		}
		
		str.append("\r\n---\r\n");
		logger.log(Logger.INFO, str.toString());					
	}
	
	private void splitRatings(
			Map<String, Set<Rating>> origin, 
			String accreditedViewer,
			List<Rating> direct, 
			List<Rating> supportingRatings, 
			Map<String, List<Rating>> ratingsPerNode
	) {
		Map<String, List<Rating>> source = new LinkedHashMap<>();
		for(String key : origin.keySet()) {
			source.put(key, new ArrayList<>(origin.get(key)));
		}
		
		direct.addAll(source.getOrDefault(accreditedViewer, Collections.emptyList()));
		source.remove(accreditedViewer);
		
		if (!direct.isEmpty()) {
			for(Rating directRating : direct) {
				for(String key : source.keySet()) {
					searchForInteraction(supportingRatings, ratingsPerNode, source, directRating, key);
				}
			}
		}
	}

	private void searchForInteraction(List<Rating> supportingRatings, Map<String, List<Rating>> ratingsPerNode,
			Map<String, List<Rating>> source, Rating directRating, String key) {
		for(Rating indirectRating : source.get(key)) {
			if (indirectRating.getIteration() == directRating.getIteration()) {
				addRating(supportingRatings, ratingsPerNode, key, indirectRating);
				return;
			}
		}
	}

	private void addRating(
			List<Rating> supportingRatings, 
			Map<String, List<Rating>> ratingsPerNode, 
			String key,
			Rating indirectRating
	) {
		if (!ratingsPerNode.containsKey(key)) {
			ratingsPerNode.put(key, new ArrayList<Rating>());
		}
		ratingsPerNode.get(key).add(indirectRating);
		supportingRatings.add(indirectRating);
	}	
}
