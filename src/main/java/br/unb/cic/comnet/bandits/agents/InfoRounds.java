package br.unb.cic.comnet.bandits.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import br.unb.cic.comnet.bandits.environment.GeneralParameters;
import jade.util.leap.Serializable;

public class InfoRounds implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long round;
	private String lastPulledArm;
	private Double lastReward;
	private Double accumulatedReward;
	private Map<String, List<Double>> rewards;
	
	public Long getRound() {
		return round;
	}
	public void setRound(long round) {
		this.round = round;
	}
	public void incrementRound() {
		round++;
	}

	public boolean hasReachedEnd() {
		return round >= GeneralParameters.getGeneralNumOfRounds();
	}
	
	public String getLastPulledArm() {
		return lastPulledArm;
	}
	public void setLastPulledArm(String lastPulledArm) {
		this.lastPulledArm = lastPulledArm;
	}	
	
	public Double getLastReward() {
		return lastReward;
	}
	public void setLastReward(Double lastReward) {
		this.lastReward = lastReward;
	}	
	
	public Double getAccumulatedReward() {
		return accumulatedReward;
	}
	public void setAccumulatedReward(Double accumulatedReward) {
		this.accumulatedReward = accumulatedReward;
	}
	
	public Map<String, List<Double>> getRewards() {
		return rewards;
	}
	public void setRewards(Map<String, List<Double>> rewards) {
		this.rewards = rewards;
	}	
	
	public InfoRounds() {
		this.round = 0L;
		this.lastPulledArm = "";
		this.lastReward = 0.0D;
		this.accumulatedReward = 0.0D;
		this.rewards = new ConcurrentHashMap<String, List<Double>>();
	}
	
	public void addReward(String arm, double reward) {
		lastPulledArm = arm;
		lastReward = reward;
		accumulatedReward += reward;
		
		if (!rewards.containsKey(arm)) {
			rewards.put(arm, new ArrayList<Double>());
		}
		rewards.get(arm).add(reward);
	}
	
	public Map<String, Double> resumeRewards() {
		Map<String, Double> resumed = new HashMap<String, Double>();
		for(String arm : rewards.keySet()) {
			Double informedMean = rewards.get(arm).stream()
					.collect(Collectors.averagingDouble(x->x));
			if (!informedMean.isNaN()) {
				resumed.put(arm, informedMean);				
			}
		}
		return resumed;
	}
	
	public String processRewards() {
		StringBuilder builder = new StringBuilder();
		
		List<String> arms = new ArrayList<>(rewards.keySet());
		arms.sort((x, y)-> x.compareTo(y));
		double totalSum = 0D;
		
		for(String arm : arms) {
			builder.append(arm);
			builder.append(";");
			
			List<Double> armRatings = rewards.get(arm);
			
			builder.append(armRatings.size());
			builder.append(";");
			double armSum = armRatings.stream().reduce(0D, (x,y)->x+y);
			
			builder.append(String.format("%.2f",armSum));
			builder.append(";");
			
			totalSum += armSum;
		}
		
		builder.append(String.format("%.2f", totalSum));
		
		return builder.toString();
	}	
}
