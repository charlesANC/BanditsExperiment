import java.security.InvalidParameterException;

import br.unb.cic.comnet.bandits.environment.GeneralParameters;

public class RunBanditsExperiment {

	public static void main(String[] args) {
		System.out.println("RunBanditsExperiment - Simulation of bandits experiments written in JADE.");		
		try {
			if (args.length < 4) {
				System.out.print("Use java -jar RunBandits.jar Algorithm useTrust honestWitnesses cooptedWitnesses ");
				System.out.println("[numOfRounds][epsilon][outputDirectory] ");
				System.out.println("   Where: ");
				System.out.println("      - Algorithm: Can be {epsilon_greedy, epsilon_first, epsilon_decreasing, ucb1, exp1}");
				System.out.println("      - useTrust: T if evaluate using FIRE T&RM or N if using simple average ");
				System.out.println("      - honestWitnesses: Number of honest witnesses agents to be created.");
				System.out.println("      - cooptedWitnesses: Number of coopted witnesses agents to be created.");
				System.out.println("   Optionally: ");
				System.out.println("      - numOfRounds: Number of rounds to be played. 1000, by default.");
				System.out.println("      - epsilon: Value of epsilon. 0.8, by default.");
				System.out.println("      - outputDirectory: Directory where logs will be written. c:\\temp, by default.");
				return;
			}
			System.out.print("---");
			System.out.print("");			
			
			String banditAlgorithm = args[0];
			String useTrust = args[1];			
			int honestWitnesses = Integer.valueOf(args[2]);
			int cooptedWitnesses = Integer.valueOf(args[3]);
			
			Long numOfRounds = null;
			if (args.length >= 5) {
				numOfRounds = Long.valueOf(args[4]);
			}
			
			Double epsilon = null;
			if (args.length >= 6) {
				epsilon = Double.valueOf(args[5]);
			}			

			String outputDirectory = null;			
			if (args.length >= 7) {
				outputDirectory = args[6];
			}			

			GeneralParameters.initilizeParameters(outputDirectory, numOfRounds, epsilon);
			
			jade.Boot.main(configuracao(banditAlgorithm, useTrust, honestWitnesses, cooptedWitnesses));
		} catch (InvalidParameterException e) {
			System.out.println("Erro: " + e.getMessage());
		}
	}
	
	private static String[] configuracao(String banditAlgorithm, String useTrust, int honestWitnesses, int cooptedWitnesses) {
		return new String[] {
				"-gui", 
				" -agents " 
						+ setUpWitnesses(honestWitnesses)
						+ setUpCooptedWitnesses(cooptedWitnesses)
						+ setUpRecommender(banditAlgorithm, useTrust)
						+ setUpPlayer()
						+ setUpAttacker()
			};				
	}

	private static String setUpRecommender(String banditAlgorithm, String useTrust) {
		String algorithm = banditAlgorithm != null ? "(" + banditAlgorithm + ", " + useTrust + ")" : "";
		return "r1:br.unb.cic.comnet.bandits.agents.Recommender" + algorithm + ";";
	}
	
	private static String setUpPlayer() {
		return "p1:br.unb.cic.comnet.bandits.agents.Player;";
	}
	
	private static String setUpAttacker() {
		return "at1:br.unb.cic.comnet.bandits.agents.Attacker(4, 30, 25, C2, 10);";
	}		

	private static String setUpWitnesses(int numOfWitnesses) {
		StringBuilder witnesses = new StringBuilder();
		for(int i = 0; i < numOfWitnesses; i++) {
			witnesses.append(" w" + i + ":br.unb.cic.comnet.bandits.agents.Witness;");
		}
		return witnesses.toString();
	}
	
	private static String setUpCooptedWitnesses(int numOfWitnesses) {
		StringBuilder witnesses = new StringBuilder();
		for(int i = 0; i < numOfWitnesses; i++) {
			witnesses.append(" cw" + i + ":br.unb.cic.comnet.bandits.agents.CooptedWitness;");
		}
		return witnesses.toString();
	}	
}
