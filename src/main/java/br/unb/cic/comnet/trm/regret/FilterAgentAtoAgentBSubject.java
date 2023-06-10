package br.unb.cic.comnet.trm.regret;

import java.util.function.Predicate;

public class FilterAgentAtoAgentBSubject extends AbstractIntuitionFilter {
	
	public FilterAgentAtoAgentBSubject(String agentA, String agentB, String subject) {
		super(new Predicate<Intuition>() {
			@Override
			public boolean test(Intuition i) {
				return 
					i.getAgentA().equals(agentA) && 
						i.getAgentB().equals(agentB) && 
							i.getSubject().equals(subject);
			}			
		});
	}
}
