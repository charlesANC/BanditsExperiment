package br.unb.cic.comnet.bandits.agents.trm;

import java.util.List;
import java.util.Optional;

public class ArmsEvaluatorFactory {
	
	public static Optional<ArmsEvaluator> createEvaluator(String evaluator, List<String> parameters) {
		if (evaluator.equals("simplemean")) {
			return Optional.of(new SimpleMeanEvaluator());
		}
		if (evaluator.equals("alphatrimmed")) {
			if (parameters.isEmpty()) {
				throw new IllegalArgumentException("Required alpha parameter not found.");
			}
			Double alpha = Double.valueOf(parameters.get(0));
			return Optional.of(new AlphaTrimmedMeanEvaluator(alpha));
		}
		if (evaluator.equals("shortmean")) {
			if (parameters.isEmpty()) {
				throw new IllegalArgumentException("Required alpha parameter not found.");
			}
			Double alpha = Double.valueOf(parameters.get(0));
			return Optional.of(new ShortMeanEvaluator(alpha));
		}
		if (evaluator.equals("fire")) {
			if (parameters.size() < 6) {
				throw new IllegalArgumentException("Not enough arguments to create a FIRE TRM evaluator.");				
			}
			
			String accreditedArm = parameters.get(0); 
			Double witnessCredibilityLambda = Double.valueOf(parameters.get(1)); 
			Double witnessCredibilityCoeficient = Double.valueOf(parameters.get(2));
			Double witnessCredibilityInnacuracyTolerance = Double.valueOf(parameters.get(3)); 
			Double directInteractionLambda = Double.valueOf(parameters.get(4));
			Double directInteractionCoeficient = Double.valueOf(parameters.get(5));
			
			return Optional.of(
				new FireTRMEvaluator(
						accreditedArm, 
						witnessCredibilityLambda, 
						witnessCredibilityCoeficient, 
						witnessCredibilityInnacuracyTolerance, 
						directInteractionLambda, 
						directInteractionCoeficient
				)
			);
		}
		return Optional.empty();
	}

}
