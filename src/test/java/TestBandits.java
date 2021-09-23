import java.util.ArrayList;
import java.util.List;

import br.unb.cic.comnet.bandits.arms.BanditArm;

public class TestBandits {
	
	public static void main(String[] args) {
		List<BanditArm> arms = new ArrayList<BanditArm>();
		
		arms.add(new BanditArm("A1", 0.9, 0.1));
		arms.add(new BanditArm("B1", 0.88, 0.5));
		arms.add(new BanditArm("B2", 0.88, 0.5));
		arms.add(new BanditArm("C1", 0.5, 1));
		arms.add(new BanditArm("C2", 0.5, 1));		
		
		double sums[] = new double[arms.size()];
		for(int j = 0; j < arms.size(); j++) {
			sums[j] = 0.0; 
		}
		
		for(int i = 0; i < 10000; i++) {
			for(int j = 0; j < arms.size(); j++) {
				sums[j] += arms.get(j).pull();
			}
		}
		
		for(int j = 0; j < arms.size(); j++) {
			System.out.println(arms.get(j).getName() + ": Accumulated reward: " + sums[j]);
			System.out.println(arms.get(j).getName() + ": Mean: " + sums[j] / 10000);			
		}		
	}

}
