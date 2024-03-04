package br.unb.cic.comnet.bandits.agents.ratings;

import java.util.List;
import java.util.Optional;

public class Opinion {
	
	public static Optional<Opinion> lastOpinion(List<Opinion> opinions) {
		Opinion last = null;
		for(Opinion opinion : opinions) {
			if (last == null || last.getRound() <= opinion.getRound()) {
				last = opinion;
			}
		}
		return Optional.ofNullable(last);
	}
	
	public static double sumRatings(List<Opinion> opinions) {
		double sum = 0D;
		for(Opinion opinion : opinions) {
			sum += opinion.getRating();
		}
		return sum;
	}
	
	private String witness;	
	private Integer round;
	private String arm;
	private Double rating;
	
	public String getWitness() {
		return witness;
	}
	public Integer getRound() {
		return round;
	}
	public String getArm() {
		return arm;
	}
	public Double getRating() {
		return rating;
	}
	
	public Opinion(Integer round, String arm, String witness, Double rating) {
		this.round = round;
		this.arm = arm;
		this.witness = witness;
		this.rating = rating;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arm == null) ? 0 : arm.hashCode());
		result = prime * result + ((round == null) ? 0 : round.hashCode());
		result = prime * result + ((witness == null) ? 0 : witness.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Opinion other = (Opinion) obj;
		if (arm == null) {
			if (other.arm != null)
				return false;
		} else if (!arm.equals(other.arm))
			return false;
		if (round == null) {
			if (other.round != null)
				return false;
		} else if (!round.equals(other.round))
			return false;
		if (witness == null) {
			if (other.witness != null)
				return false;
		} else if (!witness.equals(other.witness))
			return false;
		return true;
	}	
}
