package br.unb.cic.comnet.trm.regret;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AbstractIntuitionFilter {
	
	private Predicate<Intuition> predicate;
	
	public AbstractIntuitionFilter(Predicate<Intuition> predicate) {
		this.predicate = predicate;
	}
	
	public Collection<Intuition> filter(Collection<Intuition> intuitions)  {
		return intuitions.stream().filter(predicate).collect(Collectors.toList());
	}

}
