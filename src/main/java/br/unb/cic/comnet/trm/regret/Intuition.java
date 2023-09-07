package br.unb.cic.comnet.trm.regret;

public class Intuition {
	
	private String agentA;
	private String agentB;
	private String subject;
	private Long time;
	private Double rating;
	private Double liability;
	
	public Intuition(
		String agentA, 
		String agentB, 
		String subject, 
		Long time, 
		Double rating, 
		Double liability
	) {
		this.agentA = agentA;
		this.agentB = agentB;
		this.subject = subject;
		this.time = time;
		this.rating = rating;
		this.liability = liability;
	}
	
	public String getAgentA() {
		return agentA;
	}
	public void setAgentA(String agentA) {
		this.agentA = agentA;
	}
	
	public String getAgentB() {
		return agentB;
	}
	public void setAgentB(String agentB) {
		this.agentB = agentB;
	}
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	
	public Double getRating() {
		return rating;
	}
	public void setRating(Double rating) {
		this.rating = rating;
	}
	
	public Double getIntuition() {
		return Math.sin( ( Math.PI / 2 ) * Math.min(1D, Math.max(0D, getRating())) );
	}
	
	public Double getLiability() {
		return liability;
	}
	public void setLiability(Double liability) {
		this.liability = liability;
	}
}
