package br.com.tm.repfogagent.trm.components;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.com.tm.repfogagent.trm.Rating;
import br.com.tm.repfogagent.util.StatisticUtil;

public class WitnessReputationComponent extends FIREBaseComponent {

	private double lambda; //recency scaling factor
	
	private double bootstrapCredibility = 0.7;
	private double innacuracyToleranceThreshold;
	public Map<String, Double> witnessCredibility;
	
	public WitnessReputationComponent(double lambda, double innacuracyToleranceThreshold) {
		this.lambda = lambda != 0.0 ? lambda : (5 / Math.log(0.5));
		this.innacuracyToleranceThreshold = innacuracyToleranceThreshold;
	}
	
	public WitnessReputationComponent(double lambda, double coefficient, double innacuracyToleranceThreshold) {
		this.lambda = lambda != 0.0 ? lambda : (5 / Math.log(0.5));
		this.innacuracyToleranceThreshold = innacuracyToleranceThreshold;
		setCoefficient(coefficient);
	}
	
	public WitnessReputationComponent(double lambda, double coefficient, double innacuracyToleranceThreshold, List<Rating> ratings) {
		this.lambda = lambda != 0.0 ? lambda : (5 / Math.log(0.5));
		this.innacuracyToleranceThreshold = innacuracyToleranceThreshold;
		setCoefficient(coefficient);
		Map<String, List<Rating>> witnessCredibilityRatingBase = createWitnessCredibilityRatingBase(ratings);
		//this.witnessCredibility = createWitnessCredibility(witnessCredibilityRatingBase, bootstrapCredibility);
	}
	
	public WitnessReputationComponent(double lambda, double coefficient, double innacuracyToleranceThreshold, Map<String, List<Rating>> refereeRatings, List<Rating> localRatings) {
		this.lambda = lambda != 0.0 ? lambda : (5 / Math.log(0.5));
		this.innacuracyToleranceThreshold = innacuracyToleranceThreshold;
		setCoefficient(coefficient);
		Map<String, List<Rating>> witnessCredibilityRatingBase = createWitnessCredibilityRatingBase(refereeRatings, localRatings);
		//this.witnessCredibility = createWitnessCredibility(witnessCredibilityRatingBase, bootstrapCredibility);
	}
	
	@Override
	public double weight(Rating rating, long currentIteration) {
	//  Essa linha verificava se a coleção continha a credibilidade do nó que está sendo avaliado,
	//  no entanto, eu quero a credibilidade do nó que fez a avaliação, portanto o serverNode.
	//  Não sei se é um bug do programa do Tiago ou se eu entendi errado.
	//  Vou fazer essa alteração aqui e ver se o resultado é afetado.	
	//  if(this.witnessCredibility.containsKey(rating.getNodeName())) {		
		if(this.witnessCredibility.containsKey(rating.getServerName())) {
			InteractionTrustComponent interactionTrustComponent = new InteractionTrustComponent(0);
			//Mesma coisa do comentário anterior
			//Double witnessCredibility = this.witnessCredibility.get(rating.getNodeName());			
			Double witnessCredibility = this.witnessCredibility.get(rating.getServerName()); 
			if(witnessCredibility > 0) {
				return witnessCredibility * interactionTrustComponent.weight(rating, currentIteration);
			}
		}
		
		return 0D;
	}
	
	private double recencyWeight(long currentIteration, long rateIteration) {
		return Math.exp(Math.abs(currentIteration - rateIteration) / lambda);
	}
	
	public Map<String, List<Rating>> createWitnessCredibilityRatingBase(List<Rating> witnessRatings) {
		Map<String, List<Rating>> witnessCredibilityRatingBase = new HashMap<>();
		for(int i = 0; i < witnessRatings.size(); i++) {
			List<Rating> values = new ArrayList<Rating>();
			for(int j = 0; j < witnessRatings.size(); j++) {
				if(!witnessRatings.get(i).getNodeName().equals(witnessRatings.get(j).getNodeName())) { 
					values.add(new Rating(witnessRatings.get(j).getNodeName(), witnessRatings.get(i).getNodeName(), getCredibilityRating(witnessRatings.get(i), witnessRatings.get(j), true), (int) currentIteration, "termWC", witnessRatings.get(i).getDate()));
				}
			}
			witnessCredibilityRatingBase.put(witnessRatings.get(i).getNodeName(), values);
		}
		
		return witnessCredibilityRatingBase;
	}
	
	public Map<String, List<Rating>> createWitnessCredibilityRatingBase(Map<String, List<Rating>> refereeRatings, List<Rating> localRatings) {
		Map<String, List<Rating>> witnessCredibilityRatingBase = new HashMap<>();
		this.witnessCredibility = new HashMap<>();
		if(localRatings != null && !localRatings.isEmpty()) {
			for(Entry<String, List<Rating>> refereeRatingsEntry : refereeRatings.entrySet()) {
				List<Rating> values = new ArrayList<Rating>();
				for(Rating rating : localRatings) {
					for(Rating refereeRating : refereeRatingsEntry.getValue()) {
						values.add(new Rating(rating.getNodeName(), refereeRatingsEntry.getKey(), getCredibilityRating(rating, refereeRating, false), 0, "termWC", new Date()));
					}
				}
				this.witnessCredibility.put(refereeRatingsEntry.getKey(), calculateWitnessCredibility(values, bootstrapCredibility));
				//witnessCredibilityRatingBase.put(refereeRatingsEntry.getKey(), values);
			}
		} else {
			for(Entry<String, List<Rating>> refereeRating : refereeRatings.entrySet()) {
				//witnessCredibilityRatingBase.put(refereeRating.getKey(), null);
				this.witnessCredibility.put(refereeRating.getKey(), bootstrapCredibility);
			}
		}
		
		return witnessCredibilityRatingBase;
	}
	
	public Map<String, Double> customWitnessCredibility(Map<String, Rating> refereeRatings) {
		Map<Integer, Double> coefficientOfVariancePerIteration = new HashMap<>();
		Map<Integer, List<Rating>> ratingsPerIteration = new HashMap<>();
		Map<String, Double> witnessCredibility = new HashMap<String, Double>();
		
		for(Rating rating : refereeRatings.values()) {
			ratingsPerIteration.putIfAbsent(rating.getIteration(), new ArrayList<>());
			ratingsPerIteration.get(rating.getIteration()).add(rating);
		}
		
		for(Entry<Integer, List<Rating>> entry : ratingsPerIteration.entrySet()) {
			coefficientOfVariancePerIteration.put(entry.getKey(), StatisticUtil.coefficientVariation(entry.getValue()));
		}
		
		for(Entry<String, Rating> entry : refereeRatings.entrySet()) {
			double rep = 0.0;
			if(coefficientOfVariancePerIteration.get(entry.getValue().getIteration()) > 0.0) {
				rep = Math.exp(-0.8 * (StatisticUtil.deviation(entry.getValue(), ratingsPerIteration.get(entry.getValue().getIteration())) * coefficientOfVariancePerIteration.get(entry.getValue().getIteration())));
			} else {
				rep = 0.7;
			}
			
			witnessCredibility.put(entry.getKey(), rep);
		}
		
		return witnessCredibility;
	}
	
	private double getCredibilityRating(Rating localRating, Rating refRating, boolean adjustRecency) {
		double diffValue = Math.abs(refRating.getNormalizedValue() - localRating.getNormalizedValue());
		double credibilityValue;
		
		if(diffValue < this.innacuracyToleranceThreshold) {
			credibilityValue = 1 - Math.abs(refRating.getNormalizedValue() - localRating.getNormalizedValue());
		} else {
			credibilityValue = 0D;
		}
		
		if(adjustRecency) {
			credibilityValue = credibilityValue * recencyWeight(new Long(localRating.getIteration()), new Long(refRating.getIteration()));
		}
		
		return credibilityValue;
	}
	
	private Map<String, Double> createWitnessCredibility(Map<String, List<Rating>> witnessCredibilityRatingBase, double defaultCredibilityValue) {
		InteractionTrustComponent interactionTrustComponent = new InteractionTrustComponent(0.5);
		Date currentDate = new Date();
		
		Map<String, Double> witnessCredibility = new HashMap<String, Double>();
		
		for(Entry<String, List<Rating>> entrada : witnessCredibilityRatingBase.entrySet()) {
			if(entrada.getValue() != null && !entrada.getValue().isEmpty()) {
				double trustValue = interactionTrustComponent.calculate(entrada.getValue(), currentIteration);
				
				witnessCredibility.put(entrada.getKey(), trustValue);
			} else {
				witnessCredibility.put(entrada.getKey(), defaultCredibilityValue);
			}
		}
		
		return witnessCredibility;
	}
	
	private Double calculateWitnessCredibility(List<Rating> witnessCredibilityRatings, double defaultCredibilityValue) {
		InteractionTrustComponent interactionTrustComponent = new InteractionTrustComponent(0.5);
		double trustValue = interactionTrustComponent.calculate(witnessCredibilityRatings, currentIteration);
		
		return trustValue;
	}
}
