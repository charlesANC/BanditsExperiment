package br.unb.cic.comnet.bandits.agents;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

import br.unb.cic.comnet.bandits.utils.SerializationHelper;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class JunCorruptedWitness extends AbstractCorruptedWitness {
	private static final long serialVersionUID = 1L;
	
	Logger logger = Logger.getJADELogger(getClass().getName());	
	
	private Map<String, Double> corruption;
	
	public JunCorruptedWitness() {
		super();
		
		this.corruption = new HashMap<>();
	}
	
	@Override
	public Double resumeCorruption() {
		return corruption.values().stream().mapToDouble(Math::abs).sum();
	}
	
	@Override
	public Double corruptionByArm(String arm) {
		if (corruption == null || !corruption.containsKey(arm)) {
			return 0D;
		}
		return corruption.get(arm);
	}	
	
	@Override
	protected void setup() {
		addBehaviour(new CyclicBehaviour(this) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void action() {
				ACLMessage msg = myAgent.receive(informEpsilonTemplate());
				if (msg != null) {
					corruption = SerializationHelper.unserialize(msg.getContent(), new TypeToken<Map<String, Double>>() {});
					logger.log(Logger.INFO, "New corruption informed: " + corruption);
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
}
