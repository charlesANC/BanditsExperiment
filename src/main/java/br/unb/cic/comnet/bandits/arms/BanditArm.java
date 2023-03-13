package br.unb.cic.comnet.bandits.arms;

import org.apache.commons.math3.distribution.NormalDistribution;

public class BanditArm {

	private String name;
	private NormalDistribution normal;
	private Long pulls;
	
	public String getName() {
		return name;
	}
	
	public Long getPulls() {
		return pulls;
	}
	
	public BanditArm(String name, double mean, double sd) {
		this.name = name;
		this.normal = new NormalDistribution(mean, sd);
		this.pulls = 0L;
	}
	
	public double pull() {
		pulls++;
		return normal.sample();
	}
}
