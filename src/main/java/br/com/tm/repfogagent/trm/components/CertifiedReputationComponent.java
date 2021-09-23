package br.com.tm.repfogagent.trm.components;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.com.tm.repfogagent.trm.Rating;
import br.com.tm.repfogagent.util.DateUtil;
import br.com.tm.repfogagent.util.Util;

public class CertifiedReputationComponent extends FIREBaseComponent {

	private double lambda; //recency scaling factor
	private double innacuracyToleranceThreshold;
	private double defaultCredibilityValue = 0.5;
	private Map<String, List<Rating>> refereeCredibilityRatingValues;
	private Map<String, Double> witnessCredibility;
	
	public CertifiedReputationComponent() {
		this.lambda = (-1) * (5 / Math.log(0.5));
	}
	
	public CertifiedReputationComponent(double lambda, double coefficient) {
		//this.lambda = lambda;
		this.lambda = (-1) * (5 / Math.log(0.5));
		setCoefficient(coefficient);
	}
	
	public CertifiedReputationComponent(double lambda, double coefficient, double innacuracyToleranceThreshold, Map<String, List<Rating>> refereeRatings, List<Rating> localRatings) {
		//this.lambda = lambda;
		this.lambda = (-1) * (5 / Math.log(0.5));
		this.innacuracyToleranceThreshold = innacuracyToleranceThreshold;
		setCoefficient(coefficient);
		createRefereeCredibilityRating(refereeRatings, localRatings);
		createWitnessCredibility();
	}
	
	public CertifiedReputationComponent(double lambda, double coefficient, double innacuracyToleranceThreshold, Map<String, List<Rating>> refereeRatings, List<Rating> localRatings, double defaultCredibilityValue) {
		//this.lambda = lambda;
		this.lambda = (-1) * (5 / Math.log(0.5));
		this.innacuracyToleranceThreshold = innacuracyToleranceThreshold;
		setCoefficient(coefficient);
		createRefereeCredibilityRating(refereeRatings, localRatings);
		createWitnessCredibility();
		this.defaultCredibilityValue = defaultCredibilityValue;
	}
	
	@Override
	public double weight(Rating rating, long currentIteration) {
		//return Math.exp((-1) * recencyWeight(rating.getDate(), currentTime));
		if(this.witnessCredibility.containsKey(rating.getNodeName())) {
			InteractionTrustComponent interactionTrustComponent = new InteractionTrustComponent(0);
			Double witnessCredibility = this.witnessCredibility.get(rating.getNodeName()); 
			if(witnessCredibility > 0) {
				return witnessCredibility * interactionTrustComponent.weight(rating, currentIteration);
			}
		}
		
		return 0D;
	}
	
	private double recencyWeight(Date ti, Date currentTime) {
		return DateUtil.timeToDay(currentTime.getTime() - ti.getTime()) / lambda;
	}
	
	private void createRefereeCredibilityRating(Map<String, List<Rating>> refereeRatings, List<Rating> localRatings) {
		this.refereeCredibilityRatingValues = new HashMap<String, List<Rating>>();
		if(localRatings != null && !localRatings.isEmpty()) {
			localRatings = Util.ordenarPorData(localRatings, false);
			for(Entry<String, List<Rating>> refereeRating : refereeRatings.entrySet()) {
				refereeRating.setValue(Util.ordenarPorData(refereeRating.getValue(), false));
				int i = 0;
				List<Rating> values = new ArrayList<Rating>();
				for(Rating rating : localRatings) {
					if(i < refereeRating.getValue().size()) {
						values.add(new Rating(rating.getNodeName(), refereeRating.getKey(), getCredibilityRating(rating, refereeRating.getValue().get(i)), 0, refereeRating.getValue().get(i).getTerm(), refereeRating.getValue().get(i).getDate()));
					} else {
						break;
					}
				}
				
				this.refereeCredibilityRatingValues.put(refereeRating.getKey(), values);
			}
		} else {
			for(Entry<String, List<Rating>> refereeRating : refereeRatings.entrySet()) {
				this.refereeCredibilityRatingValues.put(refereeRating.getKey(), null);
			}
		}
	}
	
	private void createWitnessCredibility() {
		InteractionTrustComponent interactionTrustComponent = new InteractionTrustComponent(0.5);
		Date currentDate = new Date();
		double numeratorTrust, denominatorTrust;
		this.witnessCredibility = new HashMap<String, Double>();
		
		for(Entry<String, List<Rating>> entrada : this.refereeCredibilityRatingValues.entrySet()) {
			if(entrada.getValue() != null && !entrada.getValue().isEmpty()) {
				numeratorTrust = interactionTrustComponent.calculate(entrada.getValue(), currentDate.getTime());
				denominatorTrust = interactionTrustComponent.sumWeights(entrada.getValue());
				this.witnessCredibility.put(entrada.getKey(), numeratorTrust/denominatorTrust);
			} else {
				this.witnessCredibility.put(entrada.getKey(), this.defaultCredibilityValue);
			}
		}
		
	}
	
	private Double getCredibilityRating(Rating localRating, Rating refRating) {
		if(Math.abs(refRating.getNormalizedValue() - localRating.getNormalizedValue()) < this.innacuracyToleranceThreshold) {
			return 1 - Math.abs(refRating.getNormalizedValue() - localRating.getNormalizedValue());
		}
		
		return -1D;
	}

	public double getDefaultCredibilityValue() {
		return defaultCredibilityValue;
	}

	public void setDefaultCredibilityValue(double defaultCredibilityValue) {
		this.defaultCredibilityValue = defaultCredibilityValue;
	}
	
}
