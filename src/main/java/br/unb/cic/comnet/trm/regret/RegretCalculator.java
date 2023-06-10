package br.unb.cic.comnet.trm.regret;

import java.util.Collection;

public interface RegretCalculator {
	
	Double calculateReputation(String agentA, String agentB, String subject, Collection<Intuition> intuitions, Long t); 	

	// TODO: Isso aqui tá errado! Tem de ter os mesmos parâmetros do método acima...
	Double calculateReputationLiability(Double subjectiveReputation, Collection<Intuition> intuitions, Long t);
	


}
