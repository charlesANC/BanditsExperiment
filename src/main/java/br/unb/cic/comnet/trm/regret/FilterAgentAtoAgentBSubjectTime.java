package br.unb.cic.comnet.trm.regret;

import java.util.function.Predicate;

public class FilterAgentAtoAgentBSubjectTime implements Predicate<Intuition> {
	
	public static FilterAgentAtoAgentBSubjectTime get(String agentA, String agentB, String subject, Long t) {
		return new FilterAgentAtoAgentBSubjectTime(agentA, agentB, subject, t);
	}
	
	private String agentA;
	private String agentB;
	private String subject;
	private Long t;
	
	public FilterAgentAtoAgentBSubjectTime(String agentA, String agentB, String subject, Long t) {
		this.agentA = agentA;
		this.agentB = agentB;
		this.subject = subject;
		this.t = t;
	}

	@Override
	public boolean test(Intuition i) {
		return 
			i.getAgentA().equals(agentA) && 
				i.getAgentB().equals(agentB) && 
					i.getSubject().equals(subject) && 
						i.getTime().equals(t);
	}
	
}
