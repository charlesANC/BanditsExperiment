package br.unb.cic.comnet.trm.regret;

import java.util.function.Predicate;

public class FilterAgentBSubjectTime extends AbstractIntuitionFilter {
	
	public FilterAgentBSubjectTime(String agentB, String subject, Long time) {
		super(new Predicate<Intuition>() {
			@Override
			public boolean test(Intuition i) {
				return 
					i.getAgentB().equals(agentB) && 
						i.getSubject().equals(subject) && 
							i.getTime().equals(time);
			}			
		});
	}
	
}
