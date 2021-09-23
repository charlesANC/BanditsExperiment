package br.unb.cic.comnet.bandits.agents;

import java.util.HashMap;
import java.util.Map;

import br.unb.cic.comnet.bandits.utils.SerializationHelper;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class CooptedWitness extends Agent {
	private static final long serialVersionUID = 1L;
	
	Logger logger = Logger.getJADELogger(getClass().getName());	
	
	@Override
	protected void setup() {
		
		addBehaviour(new CyclicBehaviour(this) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void action() {
				ACLMessage msg = myAgent.receive(template());
				if (msg != null) {
					ACLMessage msgSend = new ACLMessage(ACLMessage.CONFIRM);
					msgSend.addReceiver(msg.getSender());
					msgSend.setProtocol(MessageProtocols.Sending_Ratings.name());
					msgSend.setContent(SerializationHelper.serialize(fakeRatings()));
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
		logger.log(Logger.INFO, "My accumulated reward was 1!");
	}	
	
	private Map<String, Double> fakeRatings() {
		Map<String, Double> ratings = new HashMap<String, Double>();
		ratings.put("A1", -1D);
		ratings.put("B1", -1D);
		ratings.put("B2", -1D);
		ratings.put("C1", -1D);
		ratings.put("C2", 1D);		
		return ratings;
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
