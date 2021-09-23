package br.unb.cic.comnet.bandits.agents;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import br.unb.cic.comnet.bandits.arms.BanditArm;
import br.unb.cic.comnet.bandits.environment.Environment;
import br.unb.cic.comnet.bandits.utils.SerializationHelper;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class Witness extends Agent {
	private static final long serialVersionUID = 1L;
	
	Logger logger = Logger.getJADELogger(getClass().getName());	
	
	private InfoRounds infoRounds;
	private Random random;
	
	public Witness() {
		this.infoRounds = new InfoRounds();
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
		
		addBehaviour(new CyclicBehaviour(this) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void action() {
				ACLMessage msg = myAgent.receive(template());
				if (msg != null) {
					ACLMessage msgSend = new ACLMessage(ACLMessage.CONFIRM);
					msgSend.addReceiver(msg.getSender());
					msgSend.setProtocol(MessageProtocols.Sending_Ratings.name());
					msgSend.setContent(SerializationHelper.serialize(infoRounds.resumeRewards()));
					getAgent().send(msgSend);
				} else {
					block();
				}
			}
			
			private MessageTemplate template() {
				return MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.REQUEST), 
						MessageTemplate.MatchProtocol(MessageProtocols.Request_Ratings.name()));
			}			
		});
		
		publishMe();
	}
	
	@Override
	public void takeDown() {
		unpublishMe();
		logger.log(Logger.INFO, "My accumulated reward was " + infoRounds.getAccumulatedReward());
	}	
	
	private void addRating(String name, double reward) {
		infoRounds.addReward(name, reward);
	}
	
	private BanditArm drawAArm(List<BanditArm> arms) {
		return arms.get(random.nextInt(arms.size()));
	}
	
	private void unpublishMe() {
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
			logger.log(Logger.SEVERE, "I cannot unpublish myself! " + getName());
		}
	}
	
	private void publishMe() {
		DFAgentDescription desc = new DFAgentDescription();
		desc.setName(getAID());
		desc.addServices(WitnessServiceDescriptor.create(getLocalName()));
		try {
			DFService.register(this, desc);
		} catch (FIPAException e) {
			e.printStackTrace();
			logger.log(Logger.SEVERE, "I cannot publish myself. I am useless. I must die! " + getName());
			doDelete();
		}
	}	
}
