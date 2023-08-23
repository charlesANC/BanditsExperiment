package br.unb.cic.comnet.trm.regret;

import java.util.function.Predicate;

public class FilterAgentBSubject extends AbstractIntuitionFilter {
	
	public FilterAgentBSubject(String agentB, String subject) {
		super(new Predicate<Intuition>() {
			@Override
			public boolean test(Intuition i) {
				return 
					i.getAgentB().equals(agentB) && 
						i.getSubject().equals(subject);
			}			
		});
	}
	
}
