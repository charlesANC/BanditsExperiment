import java.security.InvalidParameterException;

public class RunBanditsExperiment {

	public static void main(String[] args) {
		try {
			String banditAlgorithm = args[0];
			int honestWitnesses = Integer.valueOf(args[1]);
			int cooptedWitnesses = Integer.valueOf(args[2]);
			
			jade.Boot.main(configuracao(banditAlgorithm, honestWitnesses, cooptedWitnesses));
		} catch (InvalidParameterException e) {
			System.out.println("Erro: " + e.getMessage());
		}
	}
	
	private static String[] configuracao(String banditAlgorithm, int honestWitnesses, int cooptedWitnesses) {
		return new String[] {
				"-gui", 
				" -agents " 
						+ setUpWitnesses(honestWitnesses)
						+ setUpCooptedWitnesses(cooptedWitnesses)
						+ setUpRecommender(banditAlgorithm)
						+ setUpPlayer()
			};				
	}

	private static String setUpRecommender(String banditAlgorithm) {
		String algorithm = banditAlgorithm != null ? "(" + banditAlgorithm + ")" : "";
		return "r1:br.unb.cic.comnet.bandits.agents.Recommender" + algorithm + ";";
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
	
	private static String setUpCooptedWitnesses(int numOfWitnesses) {
		StringBuilder witnesses = new StringBuilder();
		for(int i = 0; i < numOfWitnesses; i++) {
			witnesses.append(" cw" + i + ":br.unb.cic.comnet.bandits.agents.CooptedWitness;");
		}
		return witnesses.toString();
	}	
}
