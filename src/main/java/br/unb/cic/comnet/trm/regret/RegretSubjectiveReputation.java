package br.unb.cic.comnet.trm.regret;

import java.util.Collection;

public class RegretSubjectiveReputation extends RegretElementCalculator {
	
	private String agentA;
	
	public String getAgentA() {
		return agentA;
	}
	
	public RegretSubjectiveReputation(String agentA, Long itm, Double u) {
		super(
			new IntuitionTransformer() {
				@Override
				public Double transform(Intuition i) {
					return i.getIntuition();
				}
			}, 
			itm, 
			u
		);
		
		this.agentA = agentA;
	}
	
	public Double calculateReputation(String agentB, String subject, Collection<Intuition> intuitions, Long t) {
		return super.calculateReputation(getAgentA(), agentB, subject, intuitions, t);
	}
	
	public Double calculateReputatioLiability(String agentB, String subject, Collection<Intuition> intuitions, Double subjectiveReputation, Long t) {
		return super.calculateReputationLiability(getAgentA(), agentB, subject, intuitions, subjectiveReputation, t);
	}
}
