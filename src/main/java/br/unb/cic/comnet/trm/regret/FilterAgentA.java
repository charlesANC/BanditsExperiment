package br.unb.cic.comnet.trm.regret;

import java.util.function.Predicate;

public class FilterAgentA extends AbstractIntuitionFilter {
	
	public FilterAgentA(String agentA) {
		super(new Predicate<Intuition>() {
			@Override
			public boolean test(Intuition i) {
				return i.getAgentA().equals(agentA);
			}			
		});
	}

}
