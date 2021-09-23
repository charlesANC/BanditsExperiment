package br.unb.cic.comnet.bandits.algorithms;

import java.util.Map;

public interface BanditAlgorithm {
	public String getName();
	public String choose(Map<String, Double> options, long round);	

}
