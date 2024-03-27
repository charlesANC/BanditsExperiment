package br.unb.cic.comnet.bandits.agents;

import java.util.ArrayList;
import java.util.List;

import br.unb.cic.comnet.bandits.utils.SerializationHelper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class ConstantCorruptionAttacker extends Agent {
	private static final long serialVersionUID = 1L;
	
	private static final Double ATTACK_VALUE = 1.00D; 
	
	private Logger logger = Logger.getJADELogger(getClass().getName());		
	
	private String targetArm = "";
	
	private List<String> cooptedWitnesses;
	
	@Override
	protected void setup() {
		interpretParameters();
		
		addBehaviour(new OneShotBehaviour(this) {
			private static final long serialVersionUID = 1L;
			@Override
			public void action() {
				informCooptedNewEpsilon();
				
			}
		});		
	}
	
	private void interpretParameters() {
		if (getArguments() != null && getArguments().length >= 2) {
			this.targetArm = getArguments()[0].toString();
			this.cooptedWitnesses = createCooptedWitnesses(Integer.valueOf(getArguments()[1].toString()));
		} else {
			logger.log(Logger.SEVERE, "It was not possible to interpret the parameters");
		}
	}	
	
	private void informCooptedNewEpsilon() {
		cooptedWitnesses.forEach(x -> {
			ACLMessage msgSend = new ACLMessage(ACLMessage.INFORM);
			msgSend.addReceiver(new AID(x, true));
			msgSend.setProtocol(MessageProtocols.Inform_New_Corruption.name());
			msgSend.setContent(SerializationHelper.serialize(ATTACK_VALUE));
			send(msgSend);			
		});
	}
	
	private List<String> createCooptedWitnesses(Integer numWitnesses) {
		List<String> cooptedWitnesses = new ArrayList<String>(numWitnesses);
		for(int i = 0; i < numWitnesses; i++) {
			cooptedWitnesses.add(createCooptedWitness(i));
		}
		return cooptedWitnesses;
	}
	
	private String createCooptedWitness(int index) {
		AgentController witness;
		try {
			witness = getContainerController()
				.createNewAgent(
					"cw" + index, 
					EpsilonCorruptionWitness.class.getName(), 
					new String[] {targetArm}
				);
			
			witness.start();
			logger.log(Logger.INFO, "Coopted witness" + witness.getName() + " has been created.");
			return witness.getName();
			
		} catch (StaleProxyException e) {
			logger.log(Logger.SEVERE, "Error on creating broker viewer! " + e.getMessage());
			return null;
		}
	}
}
