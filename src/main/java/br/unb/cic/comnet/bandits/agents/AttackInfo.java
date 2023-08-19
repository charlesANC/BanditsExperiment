package br.unb.cic.comnet.bandits.agents;

import java.io.Serializable;

public class AttackInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer armIndex;
	private Double attackValue;
	
	public Integer getArmIndex() {
		return armIndex;
	}

	public Double getAttackValue() {
		return attackValue;
	}

	public AttackInfo(Integer armIndex, Double attackValue) {
		this.armIndex = armIndex;
		this.attackValue = attackValue;
	}
}
