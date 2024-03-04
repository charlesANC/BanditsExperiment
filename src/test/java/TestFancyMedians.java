import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.unb.cic.comnet.bandits.agents.ArmInfo;
import br.unb.cic.comnet.bandits.agents.ratings.Opinion;
import br.unb.cic.comnet.bandits.agents.trm.AlphaTrimmedMeanEvaluator;
import br.unb.cic.comnet.bandits.agents.trm.ShortMeanEvaluator;

public class TestFancyMedians {
	
	public static void main(String[] args) {

		Collection<ArmInfo> arms = new ShortMeanEvaluator(0.8).evaluateArms(
				createArmsWithRatings(
					0.009877911,
					0.12146558,
					0.213794805,
					0.218117793,
					0.837820927,		
					0.231352376,
					0.256375885,
					0.284511737,
					0.296283812,
					0.357084136,
					0.380372564,
					0.389978738,
					0.395827559,
					0.403049416,
					0.414466042,
					0.040894895,
					0.043016858,		
					0.447799294,
					0.476029445,
					0.563474825,
					0.674591765,
					0.693303212,
					0.768417384,
					0.769310309,
					0.806239408,
					0.989923786,
					0.999350076,		
					0.820179733,
					0.91962603,
					0.932440738				
			));
		
		arms.stream().forEach(x -> System.out.println(String.format("%.8f", x.getTrustworth())));
		
		arms = new AlphaTrimmedMeanEvaluator(0.2).evaluateArms(arms);
		
		arms.stream().forEach(x -> System.out.println(String.format("%.8f", x.getTrustworth())));		
		
	}
	
	private static List<ArmInfo> createArmsWithRatings(Double... ratings) {
		List<ArmInfo> arms = new ArrayList<ArmInfo>();
		arms.add(new ArmInfo("teste"));
		
		for(Double value : ratings) {
			arms.get(0).addEvaluation("player", new Opinion(0, "1", "player", value));
		}
		
		return arms;
	}

}
