package br.unb.cic.comnet.trm.regret;

import java.util.Collection;

public class RegretTRM {
	
	private Long itm;
	private Double u;
	
	private Double csiSubjectiveReputation;
	private Double csiWitnessesReputation;	
	
	public Long getItm() {
		return itm;
	}
	
	public Double getU() {
		return u;
	}
	
	public RegretTRM(
		Long itm, 
		Double u, 
		Double csiSubjectiveReputation,
		Double csiWitnessesReputation		
	) {
		this.itm = itm;
		this.u = u;
		
		this.csiSubjectiveReputation = csiSubjectiveReputation;
		this.csiWitnessesReputation = csiWitnessesReputation;	
	}
	
	public Double socialReputation(
		String agentA, 
		String agentB, 
		String subject, 
		Collection<Intuition> intuitions, 
		Long t
	) {
		Double reputation = 0.0;
		
		if (csiSubjectiveReputation > 0) {
			reputation += csiSubjectiveReputation * reputationAgentAtoAgentBSubject(agentA, agentB, subject, intuitions, t);
		}
		
		if (csiWitnessesReputation > 0) {
			reputation += csiWitnessesReputation * reputationWitnessToAgentBSubject(agentA, agentB, subject, intuitions, t); 
		}
		
		return reputation;
	}
	
	private Double reputationWitnessToAgentBSubject(
		String agentA, 
		String agentB, 
		String subject, 
		Collection<Intuition> intuitions, 
		Long t
	) {
		return new RegretWitnessReputation(agentA, getItm(), getU()).calculateReputation(agentB, subject, intuitions, t);
	}

	public Double reputationAgentAtoAgentBSubject(
		String agentA, 
		String agentB, 
		String subject, 
		Collection<Intuition> intuitions, 
		Long t
	) {
		return new RegretSubjectiveReputation(agentA, itm, u).calculateReputation(agentB, subject, intuitions, t);
	}
}
