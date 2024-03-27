import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import br.unb.cic.comnet.bandits.agents.ratings.OpinionsHolder;
import br.unb.cic.comnet.bandits.environment.GeneralParameters;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.util.leap.Properties;

public class RunBanditsExperiment {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		System.out.println("RunBanditsExperiment - Simulation of bandits experiments written in JADE.");		
		try {
			if (args.length < 7) {
				System.out.print("Use java -jar RunBandits.jar Algorithm useTrust filePath useWitnessesFromFile honestWitnesses attacker cooptedWitnesses ");
				System.out.println("[numOfRounds][epsilon][outputDirectory] ");
				System.out.println("   Where: ");
				System.out.println("      - Algorithm: Can be {epsilon_greedy, epsilon_first, epsilon_decreasing, ucb1}");
				System.out.println("      - useTrust: T if evaluate using FIRE T&RM or N if using simple average ");
				System.out.println("      - filePath: Path/name of the file where witnesses ratings are defined. ");
				System.out.println("      - useWitnessesFromFile: If Y/y and a filePath is provided, witnesses will be set up from the file. ");				
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
			
			String predefinedOpinionsFileName = args[2];
			predefinedOpinionsFileName.replace("\"", "");
			OpinionsHolder holder = new OpinionsHolder();
			if (!predefinedOpinionsFileName.isEmpty()) {
				holder = new OpinionsHolder(predefinedOpinionsFileName);
				holder.processFile();				
			}
			
			String witnessesFromFile = args[3];
			boolean useWitnessesFromFile = ("y".equals(witnessesFromFile) || "Y".equals(witnessesFromFile)) && !holder.isEmpty(); 
			
			int honestWitnesses = Integer.valueOf(args[4]);
			
			String attackerClass = args[5];
			
			int cooptedWitnesses = Integer.valueOf(args[6]);
			
			Integer numOfRounds = null;
			if (args.length >= 8) {
				numOfRounds = Integer.valueOf(args[7]);
			}
			
			String outputDirectory = null;			
			if (args.length >= 9) {
				outputDirectory = args[8];
			}

			GeneralParameters.initilizeParameters(holder, outputDirectory, numOfRounds, epsilon);
			
			System.out.println("Let us begin...");
			
			/*
			jade.Boot.main(configuracao(
				banditAlgorithm, 
				banditAlgorithmParameters, 
				evaluationMethod, 
				evaluationMethodParameters, 
				honestWitnesses, 
				useWitnessesFromFile, 
				attackerClass, 
				cooptedWitnesses
			));
			*/
			startTheBagassah(configuracao(
					banditAlgorithm, 
					banditAlgorithmParameters, 
					evaluationMethod, 
					evaluationMethodParameters, 
					honestWitnesses, 
					useWitnessesFromFile, 
					attackerClass, 
					cooptedWitnesses
			));
		} catch (InvalidParameterException e) {
			System.out.println("Erro: " + e.getMessage());
		}
	}
	
	private static void startTheBagassah(String[] config) {
		try {
			Properties properties = jade.Boot.parseCmdLineArgs(config);
	        Profile profile = new ProfileImpl(properties);
	        profile.setParameter("jade_domain_df_maxresult", "1000");
			jade.core.Runtime.instance().setCloseVM(true);
			jade.core.Runtime.instance().createMainContainer(profile);
		} catch (Exception pe) {
			System.err.println("Error creating the Profile ["+pe.getMessage()+"]");
			pe.printStackTrace();
			System.exit(-1);
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
		boolean useWitnessesFromFile,
		String attackerAlgorithm,		
		int cooptedWitnesses 
	) {
		return new String[] {
				"-gui", 
				" -agents " 
						+ setUpWitnesses(honestWitnesses)
						+ setUpWitnessesFromFile(useWitnessesFromFile)
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
	
	private static String setUpWitnessesFromFile(boolean useWitnessesFomFile) {
		if (useWitnessesFomFile) {
			StringBuilder witnesses = new StringBuilder();
			System.out.println("=======> USERS' ACCOUNTS: " + GeneralParameters.getGeneralOpinionHolder().getWitnesses().size());
			List<String> names = new ArrayList<>();
			int count = 0;
			for(String witness : GeneralParameters.getGeneralOpinionHolder().getWitnesses()) {
				names.add(witness);
				if (names.size() == 100) {
					witnesses.append(" wf" + (++count) + ":br.unb.cic.comnet.bandits.agents.PredefinedRatingWitness(" + String.join(",", names) + ");");
					names.clear();
				}
			}
			if (!names.isEmpty()) {
				witnesses.append(" wf" + (++count) + ":br.unb.cic.comnet.bandits.agents.PredefinedRatingWitness(" + String.join(",", names) + ");");				
			}
			System.out.println("=======> WITNESSES: " + count);			
			return witnesses.toString();			
		}
		return "";
	}	
	
	private static String setUpAttacker(String attackerClass, int cooptedWitnesses) {
		if (attackerClass.equals("C")) {
			return "a1:br.unb.cic.comnet.bandits.agents.ConstantCorruptionAttacker(117, " + cooptedWitnesses + ");";
		} else if (attackerClass.equals("A")) {
			return "a1:br.unb.cic.comnet.bandits.agents.AdaptiveAttacker(117, 0.30, 0.20, " + cooptedWitnesses + ");";
		} else if (attackerClass.equals("JG")) {
			return "a1:br.unb.cic.comnet.bandits.agents.JunEpsilonGreedyAttacker(C2, 0.025, 0.001, " + cooptedWitnesses + ");";
		} else if (attackerClass.equals("JUCB")) {
			return "a1:br.unb.cic.comnet.bandits.agents.JunUCBAttacker(C2, 0.025, 0.001, " + cooptedWitnesses + ", 0.10);";
		} else if (attackerClass.equals("NONE")) {
			return "";
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
