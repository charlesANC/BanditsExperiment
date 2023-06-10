package br.unb.cic.comnet.trm.regret;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestRegretSubjectReputation {
	
	@Test
	public void testOneIntuition() {
		RegretSubjectiveReputation calc = new RegretSubjectiveReputation("a", 100L, 0.5);
		
		List<Intuition> intuitions = new ArrayList<Intuition>();
		intuitions.add(new Intuition("a", "b", "reward", 1L, 1.0, 1.0));
		
		Long t = 1L;
		Double rep = calc.calculateReputation("b", "reward", intuitions, t);
		
		Assertions.assertEquals(1.0, rep.doubleValue());
		
	}
	
	@Test
	public void testThreeIntuitionsDelayed() {
		RegretSubjectiveReputation calc = new RegretSubjectiveReputation("a", 100L, 0.5);
		
		List<Intuition> intuitions = new ArrayList<Intuition>();
		intuitions.add(new Intuition("a", "b", "reward", 1L, 1.0, 1.0));
		intuitions.add(new Intuition("a", "b", "reward", 2L, 1.0, 1.0));
		intuitions.add(new Intuition("a", "b", "reward", 3L, 1.0, 1.0));		
		
		Long t = 3L;
		Double rep = calc.calculateReputation("b", "reward", intuitions, t);
		
		Assertions.assertEquals(1.0, rep.doubleValue());		
	}
	
	
	@Test
	public void testFiveIntuitionsDelayedAndAWitnessEvaluation() {
		RegretSubjectiveReputation calc = new RegretSubjectiveReputation("a", 100L, 0.5);
		
		List<Intuition> intuitions = new ArrayList<Intuition>();
		intuitions.add(new Intuition("a", "b", "reward", 1L, 1.0, 1.0));
		intuitions.add(new Intuition("a", "b", "reward", 2L, 0.90, 1.0));
		intuitions.add(new Intuition("a", "b", "reward", 3L, 0.75, 1.0));
		intuitions.add(new Intuition("w", "b", "reward", 4L, 0.05, 1.0));		
		intuitions.add(new Intuition("a", "b", "reward", 4L, 0.50, 1.0));
		intuitions.add(new Intuition("a", "b", "reward", 5L, 1.0, 1.0));
		intuitions.add(new Intuition("a", "b", "reward", 10L, 0.01, 1.0));		
		
		
		Long t = 6L;
		Double rep = calc.calculateReputation("b", "reward", intuitions, t);
		
		Assertions.assertEquals(0.9050, quatroCasas(rep).doubleValue());		
	}	
	
	@Test
	public void testReliability() {
		RegretSubjectiveReputation calc = new RegretSubjectiveReputation("a", 100L, 0.5);
		
		List<Intuition> intuitions = new ArrayList<Intuition>();
		intuitions.add(new Intuition("a", "b", "reward", 1L, 1.0, 1.0));
		intuitions.add(new Intuition("a", "b", "reward", 2L, 0.90, 1.0));
		intuitions.add(new Intuition("a", "b", "reward", 3L, 0.75, 1.0));
		intuitions.add(new Intuition("w", "b", "reward", 4L, 0.05, 1.0));		
		intuitions.add(new Intuition("a", "b", "reward", 4L, 0.50, 1.0));
		intuitions.add(new Intuition("a", "b", "reward", 5L, 1.0, 1.0));
		intuitions.add(new Intuition("a", "b", "reward", 10L, 0.01, 1.0));		
		
		
		Long t = 6L;
		Double rep = calc.calculateReputation("b", "reward", intuitions, t);
		Double rel = calc.calculateReputatioLiability("b", "reward", intuitions, rep, t);
		
		Assertions.assertEquals(0.4864, quatroCasas(rel).doubleValue());				
	}
	
	private Double quatroCasas(Double v) {
		return Math.floor(v * 10000) / 10000;
	}

}
