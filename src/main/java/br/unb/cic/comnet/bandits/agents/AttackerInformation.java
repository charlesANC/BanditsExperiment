package br.unb.cic.comnet.bandits.agents;

import java.io.Serializable;

public class AttackerInformation implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Double epsilonCorruption;
	private Double corruptionCost;
	
	public Double getEpsilonCorruption() {
		return epsilonCorruption;
	}
	public Double getCorruptionCost() {
		return corruptionCost;
	}
	
	public AttackerInformation(Double epsilonCorruption, Double corruptionCost) {
		this.epsilonCorruption = epsilonCorruption;
		this.corruptionCost = corruptionCost;
	}
}
