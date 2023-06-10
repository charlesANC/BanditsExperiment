package br.unb.cic.comnet.trm.regret;

import java.util.Collection;
import java.util.Optional;

public class WitnessIntuitionTransformer implements IntuitionTransformer {
	
	private Collection<Intuition> intuitions;
	
	public WitnessIntuitionTransformer(Collection<Intuition> intuitions) {
		this.intuitions = intuitions;
	}
	
	public Collection<Intuition> getIntuitions() {
		return intuitions;
	}

	public void setIntuitions(Collection<Intuition> intuitions) {
		this.intuitions = intuitions;
	}

	@Override
	public Double transform(Intuition i) {
		Optional<Intuition> similar = findSimilar(i);
		if (similar.isEmpty()) {
			return 1.0;
		}
		
		Double similarIntuition = similar.get().getIntuition();
		Double normalizedDeviation = ( i.getIntuition() - similarIntuition ) / similarIntuition;
		return 1.0 - Math.abs( normalizedDeviation );
	}
	
	private Optional<Intuition> findSimilar(Intuition i) {
		return new FilterAgentBSubjectTime(i.getAgentB(), i.getSubject(), i.getTime())
				.filter(intuitions).stream().findAny();
	}

}
