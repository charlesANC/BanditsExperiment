package br.unb.cic.comnet.bandits.arms;

import org.apache.commons.math3.distribution.NormalDistribution;

public class BanditArm extends Arm {

	private NormalDistribution normal;
	
	public BanditArm(String name, double mean, double sd) {
		super(name);
		this.normal = new NormalDistribution(mean, sd);
	}
	
	public double sample() {
		return normal.sample();
	}
	
}
