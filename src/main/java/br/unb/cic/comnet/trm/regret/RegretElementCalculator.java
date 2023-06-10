package br.unb.cic.comnet.trm.regret;

import java.util.Collection;
import java.util.stream.Collectors;

public class RegretElementCalculator {
	
	private Long itm;
	private Double u;
	private IntuitionTransformer it;
	
	public RegretElementCalculator(IntuitionTransformer it, Long itm, Double u) {
		this.itm = itm;
		this.u = u;
		this.it = it;
	}
	
	public Long getItm() {
		return itm;
	}
	
	public Double getU() {
		return u;
	}
	
	public Double calculateReputation(
		String agentA, 
		String agentB, 
		String subject, 
		Collection<Intuition> intuitions, 
		Long t
	) {
		return subjectiveReputation(
			new FilterAgentAtoAgentBSubject(agentA, agentB, subject).filter(intuitions),
			t
		);
	}
	
	public Double calculateReputationLiability(
		String agentA, 
		String agentB, 
		String subject, 
		Collection<Intuition> intuitions, 
		Double subjectiveReputation,
		Long t
	) {
		return reputationLiability(
			subjectiveReputation, 
			new FilterAgentAtoAgentBSubject(agentA, agentB, subject).filter(intuitions),
			t
		);
	}
	
	public Double subjectiveReputation(Collection<Intuition> intuitions, Long t) {
		Double pDenominator = pDenominator(t, intuitions);
		return  intuitions.stream()
					.mapToDouble(i -> oneIntuitionContribution(i, t, pDenominator))
						.sum();
	}
	
	public Double reputationLiability(
		Double subjectiveReputation, 
		Collection<Intuition> intuitions, 
		Long t
	) {
		Double pDenominator = pDenominator(t, intuitions);		
		return ( ( 1 - getU() ) * numberOfImpressionsFactor(intuitions, t) ) + 
					( getU() * ( 1 - subjectiveReputationDeviation(intuitions, t, subjectiveReputation, pDenominator) ) ); 
	}

	private Double oneIntuitionContribution(Intuition i, Long t, Double pDenominator) {
		if (i.getTime() > t) return 0.0;
		return p(t, i, pDenominator) * it.transform(i);
	}
	
	private Double p(Long t, Intuition i, Double pDenominator) {
		Double numerator = f(i.getTime(), t);
		return numerator / pDenominator;
	}
	
	private Double pDenominator(Long t, Collection<Intuition> intuitions) {
		return intuitions.stream()
				.mapToDouble(i -> i.getTime() > t ? 0 : f(i.getTime(), t))
					.sum();
	}
	
	private Double f(Long ti, Long t) {
		return Double.valueOf(ti) / Double.valueOf(t);
	}
	
	private Double numberOfImpressionsFactor(Collection<Intuition> intuitions, Long t) {
		if (intuitions.size() > itm) return 1.0;
		
		return Math.sin( ( Math.PI / ( 2 * getItm() ) ) * countIntuitions(intuitions, t) );
	}

	private Long countIntuitions(Collection<Intuition> intuitions, Long t) {
		Long length = intuitions.stream().filter(i -> i.getTime() <= t).collect(Collectors.counting());
		return length;
	}
	
	private Double subjectiveReputationDeviation(Collection<Intuition> intuitions, Long t, Double subjectiveReputation, Double pDenominator) {
		return intuitions.stream()
				.mapToDouble(i -> oneIntuitionDeviation(i, t, pDenominator, subjectiveReputation))
					.sum();
	}
	
	private Double oneIntuitionDeviation(Intuition i, Long t, Double pDenominator, Double subjectiveReputation) {
		if (i.getTime() > t) return 0.0;
		return p(t, i, pDenominator) * Math.abs(subjectiveReputation - it.transform(i));
	}	
}
