import java.util.HashMap;
import java.util.Map;

import br.unb.cic.comnet.bandits.algorithms.Exp3SelectionAlgorithm;

public class TestExp3SelectionAlgorithm {

	public static void main(String[] args) {
		Exp3SelectionAlgorithm algo = new Exp3SelectionAlgorithm();
		
		Map<String, Double> arms = new HashMap<>();
		
		arms.put("A", 1.0);
		arms.put("B", 0.5);
		arms.put("C", 0.5);
		arms.put("D", 0.2);
		arms.put("E", 0.75);
		
		Map<String, Integer> count = new HashMap<>();
		
		count.put("A", 0);
		count.put("B", 0);
		count.put("C", 0);
		count.put("D", 0);
		count.put("E", 0);		
		
		for(int j = 0; j < 10; j++) {
			for(int i = 0; i < 1000; i++) {
				String selected = algo.choose(arms, i);
				Integer counting = count.get(selected);
				count.put(selected, counting + 1);
			}
			
			System.out.println("Aqui" + count.toString());			
		}
	}
}
