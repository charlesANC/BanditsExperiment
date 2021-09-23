package br.unb.cic.comnet.bandits.arms;

import org.apache.commons.math3.distribution.NormalDistribution;

public class BanditArm {

	private String name;
	private NormalDistribution normal;
	
	public String getName() {
		return name;
	}
	
	public BanditArm(String name, double mean, double sd) {
		this.name = name;
		this.normal = new NormalDistribution(mean, sd);
	}
	
	public double pull() {
		return normal.sample();
	}
}
