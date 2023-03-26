package br.unb.cic.comnet.bandits.arms;

import org.apache.commons.math3.distribution.NormalDistribution;

public class BanditArm {

	private String name;
	private NormalDistribution normal;
	private Long pulls;
	private Double accumulatedReward;
	
	public String getName() {
		return name;
	}
	
	public synchronized Long getPulls() {
		return pulls;
	}
	
	public synchronized Double getAverageReward() {
		return pulls > 0 ? accumulatedReward / pulls : 0;
	}
	
	public BanditArm(String name, double mean, double sd) {
		this.name = name;
		this.normal = new NormalDistribution(mean, sd);
		this.pulls = 0L;
		this.accumulatedReward = 0D;
	}
	
	public synchronized double pull() {
		Double sample = normal.sample();
		accumulatedReward += sample;
		return sample;
	}
	
	public synchronized double playersPull() {
		pulls++;
		return pull();
	}
}
