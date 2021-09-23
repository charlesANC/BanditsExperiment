package br.com.tm.repfogagent.util;

import java.util.List;

import br.com.tm.repfogagent.trm.Rating;

public class StatisticUtil {

	public static double mean(List<Rating> ratings) {
		double sum = 0;
		for(Rating rating : ratings) {
			sum += rating.getValue();
		}
		
		return sum / ratings.size();
	}
	
	public static double meanRaw(List<Double> values) {
		double sum = 0;
		for(Double value : values) {
			sum += value;
		}
		
		return sum / values.size();
	}
	
	public static double standardDeviation(List<Rating> ratings) {
		double mean = mean(ratings);
		double summ = 0;
		for(Rating rating : ratings) {
			summ += ((rating.getValue() - mean)*(rating.getValue() - mean));
		}
		
		return Math.sqrt((summ / ratings.size()));
	}
	
	public static double standardDeviationRaw(List<Double> values) {
		double mean = meanRaw(values);
		double summ = 0;
		for(Double value : values) {
			summ += ((value - mean)*(value - mean));
		}
		
		return Math.sqrt((summ / values.size()));
	}
	
	public static double coefficientVariation(List<Rating> ratings) {
		return (standardDeviation(ratings) / mean(ratings));
	}
	
	public static double deviation(Rating r, List<Rating> otherRatings) {
		double summ = 0;
		for(Rating otherRating : otherRatings) {
			summ += Math.abs(r.getValue() - otherRating.getValue());
		}
		
		return summ / otherRatings.size();
	}
	
	public static double coefficientVariationRaw(List<Double> values) {
		return (standardDeviationRaw(values) / meanRaw(values));
	}
}
