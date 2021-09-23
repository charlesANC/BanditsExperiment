package br.com.tm.repfogagent.trm.components;

import java.util.Date;
import java.util.List;

import br.com.tm.repfogagent.trm.Rating;

public abstract class FIREBaseComponent {
	
	public double calculatedValue;
	
	public double calculatedReliability;

	private double coefficient;
	
	private double gama = 1; //slope of reliability
	
	public long currentIteration;
	
	public abstract double weight(Rating rating, long currentIteration);
	
	public double calculate(List<Rating> ratings, long currentIteration) {
		double sumWeightValue = 0;
		double sumValues = 0;
		if(ratings != null && ratings.size() > 0) {
			double w = 0;
			for(Rating rating : ratings) {
				w = weight(rating, currentIteration);
				sumWeightValue += w;
				//sumValues += w * rating.getValue();
				sumValues += w * rating.getNormalizedValue();
			}
			
			this.calculatedValue = sumValues / sumWeightValue;
			//this.calculatedReliability = reliability(ratings);
			
			return sumValues / sumWeightValue; 
		} 
		
		return 0;
	}
	
	public double reliability(List<Rating> ratings) {
		return ratingReliability(ratings) * deviationReliability(ratings);
	}
	
	public double ratingReliability(List<Rating> ratings) {
		return 1 - Math.exp((-1) * gama * sumWeights(ratings)); 
	}
	
	public double deviationReliability(List<Rating> ratings) {
		double sumNumerator = 0;
		double componentTrust = calculate(ratings, currentIteration);
		for(Rating rating : ratings) {
			//sumNumerator += weight(rating, currentTime) * Math.abs(rating.getValue() - componentTrust); 
			sumNumerator += weight(rating, currentIteration) * Math.abs(rating.getNormalizedValue() - componentTrust);
		}
		
		return 1 - 0.5 * ((sumNumerator)/sumWeights(ratings));
	}
	
	public double sumWeights(List<Rating> ratings) {
		double sum = 0;
		Date currentDate = new Date();
		for(Rating rating : ratings) {
			sum += weight(rating, currentIteration);
		}
		
		return sum;
	}

	public double getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(double coefficient) {
		this.coefficient = coefficient;
	}

	public double getCalculatedValue() {
		return calculatedValue;
	}

	public void setCalculatedValue(double calculatedValue) {
		this.calculatedValue = calculatedValue;
	}

	public double getCalculatedReliability() {
		return calculatedReliability;
	}

	public void setCalculatedReliability(double calculatedReliability) {
		this.calculatedReliability = calculatedReliability;
	}

	public long getCurrentIteration() {
		return currentIteration;
	}

	public void setCurrentIteration(long currentIteration) {
		this.currentIteration = currentIteration;
	}
	
}
