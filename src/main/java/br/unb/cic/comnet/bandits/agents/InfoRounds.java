package br.unb.cic.comnet.bandits.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import br.unb.cic.comnet.bandits.agents.ratings.Opinion;
import br.unb.cic.comnet.bandits.environment.GeneralParameters;
import jade.util.leap.Serializable;

public class InfoRounds implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String owner;
	private Integer round;
	private String lastPulledArm;
	private Double lastReward;
	private Double accumulatedReward;
	
	private Map<String, List<Opinion>> rewards;
	
	public Integer getRound() {
		return round;
	}
	public void setRound(int round) {
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
	
	public Set<String> getPulledArms() {
		return rewards.keySet();
	}
	
	public Double getAccumulatedReward() {
		return accumulatedReward;
	}
	public void setAccumulatedReward(Double accumulatedReward) {
		this.accumulatedReward = accumulatedReward;
	}
	
	public Map<String, List<Opinion>> getRewards() {
		return rewards;
	}
	public void setRewards(Map<String, List<Opinion>> rewards) {
		this.rewards = rewards;
	}	
	
	public InfoRounds(String owner) {
		this.owner = owner;
		this.round = 0;
		this.lastPulledArm = "";
		this.lastReward = 0.0D;
		this.accumulatedReward = 0.0D;
		this.rewards = new ConcurrentHashMap<String, List<Opinion>>();
	}
	
	public void addReward(String arm, int round, double reward) {
		lastPulledArm = arm;
		lastReward = reward;
		accumulatedReward += reward;
		
		if (!rewards.containsKey(arm)) {
			rewards.put(arm, new Vector<Opinion>());
		}
		rewards.get(arm).add(new Opinion(round, arm, owner, reward));
	}
	
	public List<Opinion> getLastProductOpinions(int round) {
		List<Opinion> result = new ArrayList<>();
		for(String key: rewards.keySet()) {
			Opinion.lastOpinion(rewards.get(key)).ifPresent(result::add);
		}
		return result;
	}
	
	public Map<String, Double> resumeRewards() {
		Map<String, Double> resumed = new HashMap<String, Double>();
		for(String arm : rewards.keySet()) {
			Double informedMean = rewards.get(arm).stream()
					.collect(Collectors.averagingDouble(x->x.getRating()));
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
			
			List<Double> armRatings = rewards.get(arm).stream()
					.map(Opinion::getRating)
						.collect(Collectors.toList());
			
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
