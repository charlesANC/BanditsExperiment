package br.unb.cic.comnet.bandits.arms;

import org.apache.commons.math3.distribution.NormalDistribution;

public class BanditArm {

	private String name;
	private NormalDistribution normal;
	private Long generalPulls;
	private Long playersPulls;
	private Double accumulatedReward;
	
	public String getName() {
		return name;
	}
	
	public synchronized Long getPulls() {
		return playersPulls;
	}
	
	public synchronized Double getAverageReward() {
		return generalPulls > 0 ? accumulatedReward / generalPulls : 0;
	}
	
	public BanditArm(String name, double mean, double sd) {
		this.name = name;
		this.normal = new NormalDistribution(mean, sd);
		this.playersPulls = 0L;
		this.generalPulls = 0L;
		this.accumulatedReward = 0D;
	}
	
	public synchronized double pull() {
		Double sample = normal.sample();
		generalPulls++;
		accumulatedReward += sample;
		return sample;
	}
	
	public synchronized double playersPull() {
		playersPulls++;
		return pull();
	}
}
