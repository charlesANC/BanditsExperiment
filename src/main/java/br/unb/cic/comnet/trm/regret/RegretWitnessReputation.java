package br.unb.cic.comnet.trm.regret;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RegretWitnessReputation {
	
	private String source;
	
	private Long itm;
	private double u;
	
	public String getSource() {
		return source;
	}
	
	public Long getItm() {
		return itm;
	}
	
	public Double getU() {
		return u;
	}
	
	public RegretWitnessReputation(String source, Long itm, double u) {
		this.source = source;
		this.itm = itm;
		this.u = u;
	}
	
	public Double calculateReputation(String agentB, String subject, Collection<Intuition> intuitions, Long t) {
		Collection<Intuition> witnessesIntuitions = filterWitnessesIntuitions(agentB, subject, intuitions);
		Set<String> witnesses = collectWitnesses(witnessesIntuitions);
		
		Map<String, Double> witnessTrustValues = calculateWitnessTrustValues(witnesses, agentB, subject, intuitions, t);
		Double witnessTrustSum = witnessTrustValues.values().stream().mapToDouble(w -> w).sum();
		
		Double reputation = 0.0;
		
		if (witnessTrustSum > 0.0) {
			for(String witness : witnesses) {
				Double oneWitnessReputation = calculateWitnessSubjectiveReputation(witness, agentB, subject, witnessesIntuitions, t);
				Double witnessTrust = witnessTrustValues.get(witness);			
				reputation += ( witnessTrust / witnessTrustSum ) * oneWitnessReputation;
			}			
		}
		
		return reputation;
	}
	
	public Double calculateReputationLiability(String agentB, String subject, Collection<Intuition> intuitions, Double subjectiveReputation, Long t) {
		Collection<Intuition> witnessesIntuitions = filterWitnessesIntuitions(agentB, subject, intuitions);
		Set<String> witnesses = collectWitnesses(witnessesIntuitions);		
		
		Map<String, Double> witnessTrustValues = calculateWitnessTrustValues(witnesses, agentB, subject, intuitions, t);
		Double witnessTrustSum = witnessTrustValues.values().stream().mapToDouble(w -> w).sum();
		
		Double liability = 0.0;
		for(Intuition witnessIntuition : witnessesIntuitions) {
			Double witnessTrust = witnessTrustValues.get(witnessIntuition.getAgentA());
			liability += ( witnessTrust / witnessTrustSum ) * Math.min(witnessIntuition.getLiability(), witnessTrust);
		}
		
		return liability;
	}

	private Set<String> collectWitnesses(Collection<Intuition> intuitions) {
		return intuitions.stream().map(i -> i.getAgentA()).collect(Collectors.toSet());
	}
	
	private Collection<Intuition> filterWitnessesIntuitions(String agentB, String subject, Collection<Intuition> intuitions) {
		Collection<Intuition> witnessesIntuitions = new FilterAgentBSubject(agentB, subject).filter(intuitions);
		Collection<Intuition> intuitionsSource = new FilterAgentA(source).filter(witnessesIntuitions);
		witnessesIntuitions.removeAll(intuitionsSource);
		
		return witnessesIntuitions;
	}
	
	private Map<String, Double> calculateWitnessTrustValues(Set<String> witnesses, String agentB, String subject, Collection<Intuition> intuitions, Long t) {
		Map<String, Double> witnessWeights = new HashMap<>();
		for(String witness: witnesses) {
			RegretWitnessCalculator wi = new RegretWitnessCalculator(source, intuitions, getItm(), getU());
			Double trust = wi.calculateTrust(witness, agentB, subject, t);
			witnessWeights.put(witness, trust);
		}
		return witnessWeights;
	}
	
	private Double calculateWitnessSubjectiveReputation(String witness, String agentB, String subject, Collection<Intuition> intuitions, Long t) {
		RegretSubjectiveReputation reputationCalculator = new RegretSubjectiveReputation(witness, getItm(), getU()); 
		Collection<Intuition> oneWitnessIntuitions = filterOneWitnessIntuitions(witness, agentB, subject, intuitions);
		return reputationCalculator.calculateReputation(agentB, subject, oneWitnessIntuitions, t);
	}
	
	private Collection<Intuition> filterOneWitnessIntuitions(String witness, String agentB, String subject, Collection<Intuition> intuitions) {
		return new FilterAgentAtoAgentBSubject(witness, agentB, subject).filter(intuitions);
	}	
}
