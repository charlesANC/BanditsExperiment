package br.unb.cic.comnet.bandits.agents;

import java.util.HashMap;
import java.util.Map;

import br.unb.cic.comnet.bandits.arms.BanditArm;

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
			BanditArm attackedArm,
			BanditArm targetArm, 
			Integer armsNumber
	) {
		Double accLastAttacks = accAttacks.getOrDefault(attackedArm.getName(), 0D);
		
		Double attack =  attackedArmAmount(attackedArm) 
			- accLastAttacks
			- targetArmAmount(attackedArm, targetArm, armsNumber)
			- this.capDelta;
		
		return Math.max(attack, 0D);
	}	
	
	@Override
	protected boolean putCorruption(String armName, Double attack) {
		accAttacks.put(armName, accAttacks.getOrDefault(armName, 0D) + attack);
		return super.putCorruption(armName, attack);
	}	
}
