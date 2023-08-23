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
		if (evaluator.equals("exp3")) {
			if (parameters.isEmpty()) {
				throw new IllegalArgumentException("Required gamma parameter not found.");
			}
			Double gamma = Double.valueOf(parameters.get(0));
			return Optional.of(new Exp3Evaluator(gamma));
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
		if (evaluator.contentEquals("regret")) {
			if (parameters.size() < 5) {
				throw new IllegalArgumentException("Not enough arguments to create a REGRET TRM evaluator.");				
			}
			
			String accreditedAgent = parameters.get(0);
			Long itm = Long.valueOf(parameters.get(1));
			Double u = Double.valueOf(parameters.get(2));
			Double csiSubjectiveReputation = Double.valueOf(parameters.get(3));
			Double csiWitnessReputation = Double.valueOf(parameters.get(4));
			
			return Optional.of(
				new RegretTRMEvaluator(
						accreditedAgent, 
						itm, 
						u, 
						csiSubjectiveReputation, 
						csiWitnessReputation
				)
			);
		}
		return Optional.empty();
	}

}
