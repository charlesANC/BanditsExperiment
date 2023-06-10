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
		Collection<Intuition> witnessesIntuitions = filterWitnessesIntuitions(agentB, subject, intuitions, t);
		Set<String> witnesses = collectWitnesses(witnessesIntuitions);
		
		Map<String, Double> witnessTrustValues = calculateWitnessTrustValues(witnesses, agentB, subject, intuitions, t);
		Double witnessTrustSum = witnessTrustValues.values().stream().mapToDouble(w -> w).sum();
		
		Double reputation = 0.0;
		for(Intuition witnessIntuition : witnessesIntuitions) {
			Double witnessTrust = witnessTrustValues.get(witnessIntuition.getAgentA());			
			reputation += ( witnessTrust / witnessTrustSum ) * witnessIntuition.getRating();
		}
		
		return reputation;
	}
	
	public Double calculateReputationLiability(String agentB, String subject, Collection<Intuition> intuitions, Double subjectiveReputation, Long t) {
		Collection<Intuition> witnessesIntuitions = filterWitnessesIntuitions(agentB, subject, intuitions, t);
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
	
	private Collection<Intuition> filterWitnessesIntuitions(String agentB, String subject, Collection<Intuition> intuitions, Long t) {
		Collection<Intuition> witnessesIntuitions = new FilterAgentBSubjectTime(agentB, subject, t).filter(intuitions);
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
}
