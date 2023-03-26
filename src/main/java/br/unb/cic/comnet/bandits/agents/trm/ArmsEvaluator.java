package br.unb.cic.comnet.bandits.agents.trm;

import java.util.Collection;

import br.unb.cic.comnet.bandits.agents.ArmInfo;

public interface ArmsEvaluator {

	Collection<ArmInfo> evaluateArms(Collection<ArmInfo> armsInfo);
	
}
