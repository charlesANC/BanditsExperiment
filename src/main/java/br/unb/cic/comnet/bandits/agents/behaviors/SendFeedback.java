package br.unb.cic.comnet.bandits.agents.behaviors;

import br.unb.cic.comnet.bandits.agents.MessageProtocols;
import br.unb.cic.comnet.bandits.agents.Witness;
import br.unb.cic.comnet.bandits.utils.SerializationHelper;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SendFeedback extends CyclicBehaviour {
	private static final long serialVersionUID = 1L;
	
	private Witness witness;
	
	public SendFeedback(Witness witness) {
		super(witness);
		this.witness = witness;
	}
	
	@Override
	public void action() {
		ACLMessage msg = myAgent.receive(template());
		if (msg != null) {
			ACLMessage msgSend = new ACLMessage(ACLMessage.CONFIRM);
			msgSend.addReceiver(msg.getSender());
			msgSend.setProtocol(MessageProtocols.Sending_Ratings.name());
			msgSend.setContent(SerializationHelper.serialize(witness.getResumedRewards()));
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
}
