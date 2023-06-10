package br.unb.cic.comnet.trm.regret;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestWitnessReputation {
	
	@Test
	public void testOneIntuition() {
		List<Intuition> intuitions = new ArrayList<Intuition>();
		
		intuitions.add(new Intuition("a", "b", "reward", 1L, 1.0, 1.0));
		intuitions.add(new Intuition("w", "b", "reward", 1L, 0.9, 1.0));		
		
		RegretWitnessReputation calc = new RegretWitnessReputation("a", 100L, 0.5);
		
		Double rep = calc.calculateReputation("b", "reward", intuitions, 1L);
		
		Assertions.assertNotNull(rep);
	}
	
	@Test
	public void testThreeWitnesses() {
		List<Intuition> intuitions = new ArrayList<Intuition>();
		
		intuitions.add(new Intuition("w1", "b", "reward", 1L, 0.9, 1.0));		
		intuitions.add(new Intuition("a", "b", "reward", 1L, 1.0, 1.0));
		intuitions.add(new Intuition("w2", "b", "reward", 1L, 0.8, 1.0));
		intuitions.add(new Intuition("w3", "b", "reward", 1L, 0.7, 1.0));		
		
		
		RegretWitnessReputation calc = new RegretWitnessReputation("a", 100L, 0.5);
		
		Double rep = calc.calculateReputation("b", "reward", intuitions, 1L);
		
		Assertions.assertEquals(0.8034, quatroCasas(rep).doubleValue());		
	}
	
	@Test
	public void testFourWitnessesReliability() {
		List<Intuition> intuitions = new ArrayList<Intuition>();
		
		intuitions.add(new Intuition("w1", "b", "reward", 1L, 0.9, 1.0));		
		intuitions.add(new Intuition("a", "b", "reward", 1L, 1.0, 1.0));
		intuitions.add(new Intuition("w2", "b", "reward", 1L, 0.8, 1.0));
		intuitions.add(new Intuition("w3", "b", "reward", 1L, 0.7, 1.0));		
		intuitions.add(new Intuition("w4", "b", "reward", 1L, 1.0, 0.80));		
		
		
		RegretWitnessReputation calc = new RegretWitnessReputation("a", 100L, 0.5);
		
		Double rep = calc.calculateReputation("b", "reward", intuitions, 1L);
		Double rel = calc.calculateReputationLiability("b", "reward", intuitions, rep, 1L);
		
		Assertions.assertEquals(0.4871, quatroCasas(rel).doubleValue());		
	}	
	
	
	@Test
	public void testFourWitnessesReliabilityAtingindoITM() {
		List<Intuition> intuitions = new ArrayList<Intuition>();
		
		intuitions.add(new Intuition("w1", "b", "reward", 1L, 0.9, 1.0));		
		intuitions.add(new Intuition("a", "b", "reward", 1L, 1.0, 1.0));
		intuitions.add(new Intuition("w2", "b", "reward", 1L, 0.8, 1.0));
		intuitions.add(new Intuition("w3", "b", "reward", 1L, 0.7, 1.0));		
		intuitions.add(new Intuition("w4", "b", "reward", 1L, 1.0, 0.80));		
		
		
		RegretWitnessReputation calc = new RegretWitnessReputation("a", 1L, 0.5);
		
		Double rep = calc.calculateReputation("b", "reward", intuitions, 1L);
		Double rel = calc.calculateReputationLiability("b", "reward", intuitions, rep, 1L);
		
		Assertions.assertEquals(0.9070, quatroCasas(rel).doubleValue());		
	}	
	
	
	@Test
	public void testThreeWitnessesWithoutSource() {
		List<Intuition> intuitions = new ArrayList<Intuition>();
		
		intuitions.add(new Intuition("w1", "b", "reward", 1L, 0.9, 1.0));		
		intuitions.add(new Intuition("w2", "b", "reward", 1L, 0.8, 1.0));
		intuitions.add(new Intuition("w3", "b", "reward", 1L, 0.7, 1.0));		
		
		
		RegretWitnessReputation calc = new RegretWitnessReputation("a", 100L, 0.5);
		
		Double rep = calc.calculateReputation("b", "reward", intuitions, 1L);
		
		Assertions.assertEquals(0.7999, quatroCasas(rep).doubleValue());		
	}	

	private Double quatroCasas(Double v) {
		return Math.floor(v * 10000) / 10000;
	}	
}
