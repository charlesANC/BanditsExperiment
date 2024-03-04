package br.unb.cic.comnet.bandits.agents;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Random;

import br.unb.cic.comnet.bandits.agents.ratings.Opinion;
import br.unb.cic.comnet.bandits.arms.BanditArm;
import br.unb.cic.comnet.bandits.environment.Environment;
import jade.core.behaviours.TickerBehaviour;
import jade.util.Logger;

public class Witness extends AbstractWitness {
	private static final long serialVersionUID = 1L;
	
	Logger logger = Logger.getJADELogger(getClass().getName());	
	
	private InfoRounds infoRounds;
	private Random random;
	
	public Map<String, Double> getResumedRewards() {
		return infoRounds.resumeRewards();
	}
	
	public Witness() {
		super();
		
		this.infoRounds = new InfoRounds(getLocalName());
		this.random = new SecureRandom();
	}
	
	@Override
	protected void setup() {
		addBehaviour(new TickerBehaviour(this, 100) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onTick() {
				BanditArm arm = drawAArm(Environment.getArms());
				addRating(arm.getName(), arm.pull());
			}
		});
		
		super.setup();
	}
	
	private void addRating(String name, double reward) {
		infoRounds.addReward(name, Environment.getEnvCurrentRound(), reward);
	}
	
	private BanditArm drawAArm(List<BanditArm> arms) {
		return arms.get(random.nextInt(arms.size()));
	}

	@Override
	public List<Opinion> returnOpinions(Integer round) {
		return infoRounds.getLastProductOpinions(round);
	}

	@Override
	public Double accumulatedReward() {
		return infoRounds.getAccumulatedReward();
	}

	@Override
	public void afterSendingOpinions() {
		// Do nothing here.
	}
}
