package br.unb.cic.comnet.bandits.agents;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import br.com.tm.repfogagent.trm.Rating;

public class ArmInfo {
	
	private String name;
	private Map<String, Set<Rating>> evaluations;
	private Double trustworth;
	private Double reliability;
	
	public String getName() {
		return name;
	}

	public Map<String, Set<Rating>> getEvaluations() {
		return evaluations;
	}
	public void setEvaluations(Map<String, Set<Rating>> evaluations) {
		this.evaluations = evaluations;
	}

	public Double getTrustworth() {
		return trustworth;
	}
	public void setTrustworth(Double trustworth) {
		this.trustworth = trustworth;
	}

	public Double getReliability() {
		return reliability;
	}
	public void setReliability(Double reliability) {
		this.reliability = reliability;
	}

	public ArmInfo(String name) {
		this.name = name;
		this.trustworth = 0.5D;
		this.reliability = 0.5D;
		this.evaluations = new ConcurrentHashMap<String, Set<Rating>>();
	}
	
	public double meanEvaluation() {
		double sum = 0.0D;
		long count = 0;
		for(String player : evaluations.keySet()) {
			for(Rating rating : evaluations.get(player)) {
				sum += rating.getNormalizedValue();
				count++;
			}
		}
		return sum / count;
	}
	
	public List<Rating> allRatings() {
		List<Rating> ratings = new ArrayList<Rating>();
		for(String player : getEvaluations().keySet()) {
			ratings.addAll(getEvaluations().get(player));
		}
		return ratings;
	}	
	
	public void addEvaluation(String player, double evaluation) {
		if (!evaluations.containsKey(player)) {
			evaluations.put(player, new LinkedHashSet<Rating>());
		}
		Set<Rating> ratings = evaluations.get(player);
		
		ratings.add(createRating(player, evaluation, ratings.size() + 1));
	}
	
	private Rating createRating(String player, double evaluation, int round) {
		return new Rating(
			getName(), 
			player, 
			limitInterval(evaluation), 
			limitInterval(evaluation), 
			round, 
			"General", 
			new Date()
		);
	}
	
	private double limitInterval(double value) {
		return Math.min(1D, Math.max(value, 0D));
	}
}
