package br.unb.cic.comnet.bandits.environment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import br.unb.cic.comnet.bandits.arms.Arm;
import br.unb.cic.comnet.bandits.arms.BanditArm;
import br.unb.cic.comnet.bandits.arms.DiscreteRewardBanditArm;

public class Environment {
	
	private static Environment env = new Environment();
	
	public static Environment getInstance() {
		return env;
	}
	
	public static List<Arm> getArms() {
		return Collections.unmodifiableList(getInstance().arms);
	}
	
	public static Map<String, Arm> getArmsMap() {
		return getInstance().arms.stream()
					.collect(
						Collectors.toMap(
							Arm::getName, 
							Function.identity()
						)
					);
	}
	
	public static Optional<Arm> getArm(String armName) {
		for(Arm arm : getInstance().arms) {
			if (arm.getName().equals(armName)) {
				return Optional.of(arm);
			}
		}
		return Optional.empty();
	}
	
	public static Integer getEnvCurrentRound() {
		return getInstance().getCurrentRound();
	}
	
	public static void incrementEnvCurrentRound() {
		getInstance().incrementCurrentRound();
	}
	
	private List<Arm> arms;
	private Integer currentRound;
	
	public Integer getCurrentRound() {
		return currentRound;
	}
	public void incrementCurrentRound() {
		currentRound++;
		System.out.println(">>>>> ROUND: " + currentRound + " <<<<<<< ");
	}		
	
	public Environment() {
		currentRound = 0;
		arms = new ArrayList<Arm>();
		
		if (GeneralParameters.getGeneralOpinionHolder().isEmpty()) {
			setDefaultArms(arms);
		} else {
			setPredefinedArms(arms);
		}
	}
	
	private void setDefaultArms(List<Arm> arms) {
		arms.add(new BanditArm("C1", 0.75, 0.5));
		arms.add(new BanditArm("C2", 0.75, 0.5));				
		arms.add(new BanditArm("B1", 0.85, 0.3));
		arms.add(new BanditArm("A1", 0.9, 0.1));		
		arms.add(new BanditArm("B2", 0.85, 0.3));		
	}
	
	private void setPredefinedArms(List<Arm> arms) {
		if (!GeneralParameters.getGeneralOpinionHolder().isEmpty()) {
			Set<String> predefinedArms = GeneralParameters.getGeneralOpinionHolder().getProducts();
			for(String arm: predefinedArms) {
				Map<Double, Double> mapFrequencies = GeneralParameters.getGeneralOpinionHolder().getProductRatingFrequency(arm);
				List<Double> simbols = new LinkedList<>();
				List<Double> frequencies = new LinkedList<>();
				for(Entry<Double, Double> entry : mapFrequencies.entrySet()) {
					simbols.add(GeneralParameters.getGeneralOpinionHolder().scaleSimbol(entry.getKey()));
					frequencies.add(mapFrequencies.get(entry.getKey()));
				}
				arms.add(new DiscreteRewardBanditArm(arm, simbols, frequencies));
			}
		}
	}
}
