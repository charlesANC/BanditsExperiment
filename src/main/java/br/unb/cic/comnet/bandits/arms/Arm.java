package br.unb.cic.comnet.bandits.arms;

public abstract class Arm {
	
	private String name;
	private Long generalPulls;
	private Long playersPulls;
	private Double accumulatedReward;	
	
	public String getName() {
		return this.name;
	}
	
	public synchronized Long getPulls() {
		return playersPulls;
	}
	
	public synchronized Double getAverageReward() {
		return generalPulls > 0 ? accumulatedReward / generalPulls : 0;
	}
	
	public Arm(String name) {
		this.name = name;
		this.playersPulls = 0L;
		this.generalPulls = 0L;
		this.accumulatedReward = 0D;
	}
	
	public abstract double sample();
	
	public synchronized double pull() {
		Double sample = sample();
		generalPulls++;
		accumulatedReward += sample;
		return sample;
	}
	
	public synchronized double playersPull() {
		playersPulls++;
		return pull();
	}	
}
