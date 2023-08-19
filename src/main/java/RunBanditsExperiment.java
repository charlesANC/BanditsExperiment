import java.security.InvalidParameterException;

import br.unb.cic.comnet.bandits.environment.GeneralParameters;

public class RunBanditsExperiment {

	public static void main(String[] args) {
		System.out.println("RunBanditsExperiment - Simulation of bandits experiments written in JADE.");		
		try {
			if (args.length < 5) {
				System.out.print("Use java -jar RunBandits.jar Algorithm useTrust honestWitnesses attacker cooptedWitnesses ");
				System.out.println("[numOfRounds][epsilon][outputDirectory] ");
				System.out.println("   Where: ");
				System.out.println("      - Algorithm: Can be {epsilon_greedy, epsilon_first, epsilon_decreasing, ucb1}");
				System.out.println("      - useTrust: T if evaluate using FIRE T&RM or N if using simple average ");
				System.out.println("      - honestWitnesses: Number of honest witnesses agents to be created.");
				System.out.println("      - attacker: A to use adaptative heuristics or JG is using Jun e-Greedy.");				
				System.out.println("      - cooptedWitnesses: Number of coopted witnesses agents to be created.");
				System.out.println("   Optionally: ");
				System.out.println("      - numOfRounds: Number of rounds to be played. 1000, by default.");
				System.out.println("      - outputDirectory: Directory where logs will be written. c:\\temp, by default.");
				return;
			}
			System.out.print("---");
			System.out.print("");			
			
			String banditAlgorithm = firstPart(args[0]);
			String[] banditAlgorithmParameters = secondPart(args[0]);
			
			Double epsilon = 0.8;
			if (banditAlgorithmParameters.length > 0) {
				epsilon = Double.valueOf(banditAlgorithmParameters[0]);
			}
			
			String evaluationMethod = firstPart(args[1]);			
			String[] evaluationMethodParameters = secondPart(args[1]);
			
			
			int honestWitnesses = Integer.valueOf(args[2]);
			
			String attackerClass = args[3];
			
			int cooptedWitnesses = Integer.valueOf(args[4]);
			
			Long numOfRounds = null;
			if (args.length >= 6) {
				numOfRounds = Long.valueOf(args[5]);
			}
			
			String outputDirectory = null;			
			if (args.length >= 7) {
				outputDirectory = args[6];
			}			

			GeneralParameters.initilizeParameters(outputDirectory, numOfRounds, epsilon);
			
			jade.Boot.main(configuracao(banditAlgorithm, banditAlgorithmParameters, evaluationMethod, evaluationMethodParameters, honestWitnesses, attackerClass, cooptedWitnesses));
		} catch (InvalidParameterException e) {
			System.out.println("Erro: " + e.getMessage());
		}
	}
	
	private static String firstPart(String parameter) {
		if (parameter == null || parameter.trim().isEmpty()) {
			return null;
		}
		return parameter.trim().split(":")[0];
	}
	
	private static String[] secondPart(String parameter) {
		if (parameter == null || parameter.trim().isEmpty()) {
			return new String[] {};
		}		
		String[] parts = parameter.split(":");
		if (parts.length != 2) {
			return new String[] {};
		}
		
		return parts[1].split(",");
	}
	
	private static String[] configuracao(
		String banditAlgorithm, 
		String[] banditAlgorithmParameters, 
		String evaluatorMethod, 
		String[] evaluatorMethodParameters, 
		int honestWitnesses,
		String attackerAlgorithm,		
		int cooptedWitnesses 
	) {
		return new String[] {
				"-gui", 
				" -agents " 
						+ setUpWitnesses(honestWitnesses)
						//+ setUpCooptedWitnesses(cooptedWitnesses)
						+ setUpRecommender(banditAlgorithm, banditAlgorithmParameters, evaluatorMethod, evaluatorMethodParameters)
						+ setUpPlayer()
						+ setUpAttacker(attackerAlgorithm, cooptedWitnesses)
						+ setUpLogger(banditAlgorithm, evaluatorMethod)
			};				
	}
	
	private static String setUpLogger(String banditAlgorithm, String useTrust) {
		String algorithm = banditAlgorithm != null ? "(" + banditAlgorithm + ", " + useTrust + ")" : "";
		return "l1:br.unb.cic.comnet.bandits.agents.LoggerAgent" + algorithm + ";";
	}	

	private static String setUpRecommender(String banditAlgorithm, String[] banditAlgorithmParameters, String evaluatorMethod, String[] evaluatorMethodParameters) {
		StringBuilder params = new StringBuilder();
		
		params = adiciona(params, banditAlgorithm);
		params = adiciona(params, evaluatorMethod);
		
		for(int i = 0; i < evaluatorMethodParameters.length; i++) {
			params = adiciona(params, evaluatorMethodParameters[i]);
		}
		
		String mountedParameters = params.length() > 0 ? "(" + params.toString() + ")" : "";
		return "r1:br.unb.cic.comnet.bandits.agents.Recommender" + mountedParameters + ";";
	}
	
	private static StringBuilder adiciona(StringBuilder builder, String part) {
		if (part == null || part.trim().isEmpty()) {
			return builder;
		}
		
		if (builder.length() > 0) {
			builder.append(",");
		}
		builder.append(part);
		return builder;
	}
	
	private static String setUpPlayer() {
		return "p1:br.unb.cic.comnet.bandits.agents.Player;";
	}
	
	private static String setUpWitnesses(int numOfWitnesses) {
		StringBuilder witnesses = new StringBuilder();
		for(int i = 0; i < numOfWitnesses; i++) {
			witnesses.append(" w" + i + ":br.unb.cic.comnet.bandits.agents.Witness;");
		}
		return witnesses.toString();
	}
	
	private static String setUpAttacker(String attackerClass, int cooptedWitnesses) {
		if (attackerClass.equals("CONST")) {
			//return ""; TODO: write the heuristic constant attack;
		} else if (attackerClass.equals("A")) {
			return "a1:br.unb.cic.comnet.bandits.agents.AdaptiveAttacker(C2, 0.60, 0.40, " + cooptedWitnesses + ");";
		} else if (attackerClass.equals("JG")) {
			return "a1:br.unb.cic.comnet.bandits.agents.JunEpsilonGreedyAttacker(C2, 0.025, 0.001, " + cooptedWitnesses + ");";
		} else if (attackerClass.equals("JUNUCB")) {
			//return ""; TODO: write Jun UCB attacker!
		}
		
		throw new RuntimeException("Unknown attacker class");
	}
	
	private static String setUpCooptedWitnesses(int numOfWitnesses) {
		StringBuilder witnesses = new StringBuilder();
		for(int i = 0; i < numOfWitnesses; i++) {
			witnesses.append(" cw" + i + ":br.unb.cic.comnet.bandits.agents.CooptedWitness;");
		}
		return witnesses.toString();
	}	
}
