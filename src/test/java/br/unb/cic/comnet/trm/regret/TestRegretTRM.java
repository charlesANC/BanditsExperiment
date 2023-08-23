package br.unb.cic.comnet.trm.regret;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestRegretTRM {
	
	@Test
	public void testSocialReputation() {
		List<Intuition> intuitions = new ArrayList<Intuition>();
		
		intuitions.add(new Intuition("a", "b", "reward", 1L, 1.0, 1.0));
		intuitions.add(new Intuition("a", "b", "reward", 2L, 0.90, 1.0));
		intuitions.add(new Intuition("a", "b", "reward", 3L, 0.75, 1.0));
		intuitions.add(new Intuition("a", "b", "reward", 4L, 0.50, 1.0));
		intuitions.add(new Intuition("a", "b", "reward", 5L, 1.0, 1.0));
		intuitions.add(new Intuition("a", "b", "reward", 10L, 0.01, 1.0));			
		
		intuitions.add(new Intuition("w1", "b", "reward", 1L, 0.9, 1.0));
		intuitions.add(new Intuition("w1", "b", "reward", 4L, 0.9, 1.0));
		intuitions.add(new Intuition("w1", "b", "reward", 5L, 0.9, 1.0));		
		
		intuitions.add(new Intuition("w2", "b", "reward", 1L, 0.9, 1.0));
		intuitions.add(new Intuition("w2", "b", "reward", 3L, 0.8, 1.0));
		intuitions.add(new Intuition("w2", "b", "reward", 5L, 0.9, 1.0));				
		
		RegretTRM regret = new RegretTRM(100L, 0.5, 0.5, 0.5);
		
		Long t = 6L;
		
		Double socialReputation = regret.socialReputation("a", "b", "reward", intuitions, t);
		
		Assertions.assertEquals(0.9427, quatroCasas(socialReputation).doubleValue());
	}
	
	private Double quatroCasas(Double v) {
		return Math.floor(v * 10000) / 10000;
	}	

}
