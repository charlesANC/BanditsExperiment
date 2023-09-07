package br.unb.cic.comnet.bandits.agents.trm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.tm.repfogagent.trm.Rating;
import br.unb.cic.comnet.bandits.agents.ArmInfo;
import br.unb.cic.comnet.trm.regret.Intuition;
import br.unb.cic.comnet.trm.regret.RegretTRM;
import jade.util.Logger;

public class RegretTRMEvaluator implements ArmsEvaluator {
	
	Logger logger = Logger.getJADELogger(getClass().getName());
	
	private String accreditedArm;
	private Long itm;
	private Double u;
	private Double csiSubjectiveReputation;
	private Double csiWitnessReputarion;
	
	public static RegretTRMEvaluator createDefault(String accreditedArm) {
		return new RegretTRMEvaluator(accreditedArm, 100L, 0.5D, 0.6D, 0.4D);
	}
	
	public RegretTRMEvaluator(
		String accreditedArm,
		Long itm,
		Double u, 
		Double csiSubjectiveReputation, 
		Double csiWitnessResputation
	) {
		this.accreditedArm = accreditedArm;
		this.itm = itm;
		this.u = u;
		this.csiSubjectiveReputation = csiSubjectiveReputation;
		this.csiWitnessReputarion = csiWitnessResputation;
	}

	@Override
	public synchronized Collection<ArmInfo> evaluateArms(Collection<ArmInfo> armsInfo) {
		StringBuilder str = new StringBuilder("\r\n---\r\n");
		
		RegretTRM regret = new RegretTRM(itm, u, csiSubjectiveReputation, csiWitnessReputarion);
		
		for (ArmInfo armInfo : armsInfo) {
			List<Intuition> intuitions = new ArrayList<>();
			long t = 0;			
			for(Rating rating : armInfo.allRatings()) {
				if (t < rating.getIteration()) {
					t = rating.getIteration();
				}
				
				intuitions.add(
					new Intuition(
						rating.getServerName(), 
						rating.getNodeName(), 
						"reward", 
						Long.valueOf(rating.getIteration()), 
						rating.getNormalizedValue(), 
						1.0)
				);
			}
			
			if (!intuitions.isEmpty()) {
				t = t + 1;
				armInfo.setTrustworth(regret.socialReputation(accreditedArm, armInfo.getName(), "reward", intuitions, t));
				armInfo.setReliability(1.0);
				
				str.append("Trustworthy of " + armInfo.getName() + " is " + armInfo.getTrustworth() + "\r\n");				
			}
		}
		
		str.append("\r\n---\r\n");
		logger.log(Logger.INFO, str.toString());			
		
		return armsInfo;
	}

}
