package br.unb.cic.comnet.bandits.environment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import br.unb.cic.comnet.bandits.arms.BanditArm;

public class Environment {
	
	private static Environment env = new Environment();
	
	public static Environment getInstance() {
		return env;
	}
	
	public static List<BanditArm> getArms() {
		return Collections.unmodifiableList(getInstance().arms);
	}
	
	public static Optional<BanditArm> getArm(String armName) {
		for(BanditArm arm : getInstance().arms) {
			if (arm.getName().equals(armName)) {
				return Optional.of(arm);
			}
		}
		return Optional.empty();
	}
	
	private List<BanditArm> arms;
	
	public Environment() {
		arms = new ArrayList<BanditArm>();
		arms.add(new BanditArm("A1", 0.9, 0.1));
		arms.add(new BanditArm("B1", 0.85, 0.3));
		arms.add(new BanditArm("B2", 0.85, 0.3));
		arms.add(new BanditArm("C1", 0.75, 0.5));
		arms.add(new BanditArm("C2", 0.75, 0.5));		
	}
}
