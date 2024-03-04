package br.unb.cic.comnet.bandits.agents;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import br.unb.cic.comnet.bandits.agents.ratings.Opinion;
import br.unb.cic.comnet.bandits.arms.BanditArm;
import br.unb.cic.comnet.bandits.environment.Environment;
import br.unb.cic.comnet.bandits.utils.SerializationHelper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public abstract class AbstractCorruptedWitness extends AbstractWitness {
	private static final long serialVersionUID = 1L;
	
	Logger logger = Logger.getJADELogger(getClass().getName());	
	
	private InfoRounds infoRounds;
	private Random random;	
	
	InfoRounds getInfoRounds() {
		return infoRounds;
	}
	
	Random getRandom() {
		return random;
	}
	
	public AbstractCorruptedWitness() {
		super();
		
		this.infoRounds = new InfoRounds(getLocalName());
		this.random = new SecureRandom();
	}
	
	public abstract Double resumeCorruption();	
	
	public abstract Double corruptionByArm(String arm);	
	
	private class PullAArmAtRandom extends TickerBehaviour {
		private static final long serialVersionUID = 1L;
		
		public PullAArmAtRandom(Agent agent, long period) {
			super(agent, period);
		}
		
		@Override
		protected void onTick() {
			BanditArm arm = drawAArm(Environment.getArms());
			addRating(arm.getName(), arm.pull());
		}		
		
	}
	
	@Override
	protected void setup() {
		addBehaviour(new PullAArmAtRandom(this, 100));
		
		super.setup();
	}	
	
	@Override
	public List<Opinion> returnOpinions(Integer round) {
		return corruptRewards(infoRounds.getLastProductOpinions(round));
	}

	@Override
	public Double accumulatedReward() {
		return infoRounds.getAccumulatedReward();
	}

	@Override
	public void afterSendingOpinions() {
		try {
			informLoggers();
		} catch (FIPAException fe) {
			fe.printStackTrace();
			logger.log(Logger.SEVERE, "Could not get available loggers.");						
		}
	}		
	
	private void informLoggers() throws FIPAException {
		Set<AID> loggers = LoggerServiceDescriptor.search(this);
		loggers.forEach(x -> {
			ACLMessage msgSend = new ACLMessage(ACLMessage.INFORM);
			msgSend.addReceiver(x);
			msgSend.setProtocol(MessageProtocols.Inform_Accumm_Cost.name());
			msgSend.setContent(SerializationHelper.serialize(resumeCorruption()));
			send(msgSend);						
		});
	}	
	
	private List<Opinion> corruptRewards(List<Opinion> opinions) {
		List<Opinion> corruptedOpinions = new ArrayList<>();
		
		for(Opinion opinion: opinions) {
			Opinion corruptedOpinion = new Opinion(
				opinion.getRound(), 
				opinion.getArm(),
				opinion.getWitness(), 
				opinion.getRating() - corruptionByArm(opinion.getArm())
			);
			
			corruptedOpinions.add(corruptedOpinion);
		}

		return corruptedOpinions;
	}	
	
	private void addRating(String name, double reward) {
		infoRounds.addReward(name, Environment.getEnvCurrentRound(), reward);
	}
	
	private BanditArm drawAArm(List<BanditArm> arms) {
		return arms.get(random.nextInt(arms.size()));
	}
}
