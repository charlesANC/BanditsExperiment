package br.unb.cic.comnet.bandits.agents;

import com.google.gson.reflect.TypeToken;

import br.unb.cic.comnet.bandits.utils.SerializationHelper;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class EpsilonCorruptionWitness extends AbstractCorruptedWitness {
	private static final long serialVersionUID = 1L;
	
	Logger logger = Logger.getJADELogger(getClass().getName());	
	
	private Double epsilonCorruption = 0D;
	private String armName;
	
	@Override
	public Double corruptionByArm(String arm) {
		return (arm.equals(armName) ? 1 : -1) * epsilonCorruption;
	}
	
	@Override
	protected void setup() {
		interpretParamenters();		
		
		addBehaviour(new CyclicBehaviour(this) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void action() {
				ACLMessage msg = myAgent.receive(informEpsilonTemplate());
				if (msg != null) {
					Double newEpsilon = SerializationHelper.unserialize(msg.getContent(), new TypeToken<Double>() {});
					epsilonCorruption = newEpsilon;
					//logger.log(Logger.INFO, "New epsilon informed: " + epsilonCorruption);
				} else {
					block();
				}
			}
			
			private MessageTemplate informEpsilonTemplate() {
				return MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM), 
						MessageTemplate.MatchProtocol(MessageProtocols.Inform_New_Corruption.name()));
			}			
		});			
		
		super.setup();
	}
	
	private void interpretParamenters() {
		if (getArguments() != null && getArguments().length > 0) {
			this.armName = getArguments()[0].toString();
		}
	}
}
