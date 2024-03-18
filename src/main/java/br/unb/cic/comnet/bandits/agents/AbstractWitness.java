package br.unb.cic.comnet.bandits.agents;

import java.util.List;

import com.google.gson.reflect.TypeToken;

import br.unb.cic.comnet.bandits.agents.ratings.Opinion;
import br.unb.cic.comnet.bandits.utils.SerializationHelper;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public abstract class AbstractWitness extends Agent {
	private static final long serialVersionUID = 1L;
	
	Logger logger = Logger.getJADELogger(getClass().getName());		
	
	public abstract List<Opinion> returnOpinions(Integer round);
	
	public abstract Double accumulatedReward();
	
	public abstract void afterSendingOpinions();
	
	private class SendOpinionsBehaviour extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;
		
		public SendOpinionsBehaviour(Agent agent) {
			super(agent);
		}
		
		@Override
		public void action() {		
			ACLMessage msg = myAgent.receive(template());
			if (msg != null) {
				ACLMessage msgSend = new ACLMessage(ACLMessage.CONFIRM);
				
				Integer round = extractRound(msg);
				
				msgSend.addReceiver(msg.getSender());
				msgSend.setProtocol(MessageProtocols.Sending_Ratings.name());
				msgSend.setContent(SerializationHelper.serialize(returnOpinions(round)));
				getAgent().send(msgSend);
				
				afterSendingOpinions();
			} else {
				block();
			}
		}
		
		private Integer extractRound(ACLMessage msg) {
			return SerializationHelper
				.unserialize(
					msg.getContent(), 
					new TypeToken<Integer>() {}
			);
		}
		
		private MessageTemplate template() {
			return MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST), 
					MessageTemplate.MatchProtocol(MessageProtocols.Request_Ratings.name()));
		}			
	}
	
	@Override
	protected void setup() {
		addBehaviour(new SendOpinionsBehaviour(this));		
		publishMe();
	}	
	
	@Override
	public void takeDown() {
		unpublishMe();
		//logger.log(Logger.INFO, "My accumulated reward was " + accumulatedReward());
	}
	
	public void unpublishMe() {
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
			logger.log(Logger.SEVERE, "I cannot unpublish myself! " + getName());
		}
	}
	
	public void publishMe() {
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
