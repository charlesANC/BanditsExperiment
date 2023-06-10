package br.unb.cic.comnet.trm.regret;

import java.util.Collection;

public class RegretWitnessCalculator extends RegretElementCalculator {
	
	private String source;
	private Collection<Intuition> intuitions;
	
	public String getSource() {
		return source;
	}

	public RegretWitnessCalculator(String source, Collection<Intuition> intuitions, Long itm, Double u) {
		super(
			new WitnessIntuitionTransformer(
				new FilterAgentA(source).filter(intuitions)
			), 
			itm, 
			u
		);
		
		this.source = source;
		this.intuitions = intuitions;
	}
	
	public Double calculateTrust(String witness, String agentB, String subject, Long t) {
		Double reputation = calculateReputation(witness, agentB, subject, intuitions, t);
		Double liability = calculateReputationLiability(witness, agentB, subject, intuitions, reputation, t);
		return liability * reputation;
	}
}