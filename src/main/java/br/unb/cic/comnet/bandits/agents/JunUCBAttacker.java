package br.unb.cic.comnet.bandits.agents;

import java.util.HashMap;
import java.util.Map;

import br.unb.cic.comnet.bandits.arms.Arm;

public class JunUCBAttacker extends JunEpsilonGreedyAttacker {

	private static final long serialVersionUID = 1L;
	
	private Map<String, Double> accAttacks;
	private Double capDelta;
	
	public JunUCBAttacker() {
		super();
		this.accAttacks = new HashMap<>();
		this.capDelta = 0D;
	}
	
	@Override
	public void setup() {
		if (getArguments().length >= 5) {
			this.capDelta = Double.valueOf(getArguments()[4].toString());
		}
		super.setup();
	}
	
	@Override
	protected Double calculateAttack(
			Arm attackedArm,
			Arm targetArm, 
			Integer armsNumber
	) {
		Double accLastAttacks = accAttacks.getOrDefault(attackedArm.getName(), 0D);
		
		Double attack =  attackedArmAmount(attackedArm) 
			- accLastAttacks
			- ucbTargetArmAmount(attackedArm, targetArm, armsNumber);
		
		return Math.max(attack, 0D);
	}
	
	protected Double ucbTargetArmAmount(
		Arm attackedArm,
		Arm targetArm, 
		Integer armsNumber
	){ 
		Long attackedArmPulls = attackedArm.getPulls();
		return (super.targetAverageDiff(targetArm, sigma, delta, armsNumber) - this.capDelta) * (attackedArmPulls + 1);		
	}
	
	@Override
	protected boolean putCorruption(String armName, Double attack) {
		accAttacks.put(armName, accAttacks.getOrDefault(armName, 0D) + attack);
		return super.putCorruption(armName, attack);
	}	
}
