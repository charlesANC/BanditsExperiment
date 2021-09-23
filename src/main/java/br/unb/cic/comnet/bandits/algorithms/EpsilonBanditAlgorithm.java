package br.unb.cic.comnet.bandits.algorithms;

public abstract class EpsilonBanditAlgorithm extends AbstractBanditAlgoritm {
	
	private double epsilon;
	
	public EpsilonBanditAlgorithm(String name, double epsilon) {
		super(name);
		this.epsilon = epsilon;
	}
	
	public double getEpsilon() {
		return epsilon;
	}

}
