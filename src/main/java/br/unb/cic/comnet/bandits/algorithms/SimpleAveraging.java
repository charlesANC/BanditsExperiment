package br.unb.cic.comnet.bandits.algorithms;

import java.util.Map;

public class SimpleAveraging extends AbstractBanditAlgoritm {
	
	public SimpleAveraging() {
		super("simple_averaging");
	}

	@Override
	public String choose(Map<String, Double> options, long round) {
		return exploit(options);
	}
}
