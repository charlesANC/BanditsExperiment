package br.unb.cic.comnet.bandits.agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.unb.cic.comnet.bandits.environment.Environment;
import br.unb.cic.comnet.bandits.utils.FileUtils;
import br.unb.cic.comnet.bandits.utils.SerializationHelper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class Attacker extends Agent {
	private static final long serialVersionUID = 1L;
	
	private static final Double ERROR_EPSILON_DELTA = 0.50D;
	
	private Logger logger = Logger.getJADELogger(getClass().getName());		
	
	private Long topTrigger;
	private Long bottonTrigger;
	private Integer goalArmIndex;
	private String armName;
	
	private List<String> cooptedWitnesses;
	
	private Long lastPullsObserved;
	private Double errorEpsilon;
	private Double cost;
	
	public Attacker() {
		this.errorEpsilon = 0D;
		this.cost = 0D;
		this.lastPullsObserved = 0L;
	}
	
	@Override
	protected void setup() {
		interpretParameters();
		
		addBehaviour(new TickerBehaviour(this, 100) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onTick() {
				Double epsilonUpdateFactor = 
						calculateUpdateFactor(
							updatePullsObservation(
								Environment.getArms().get(goalArmIndex).getPulls()
							)
						);
				
				if (epsilonUpdateFactor != 0D) {
					updateEpsilon(epsilonUpdateFactor);
					try {
						informCooptedNewEpsilon();
						informLoggers();						
					} catch (FIPAException e) {
						e.printStackTrace();
						logger.log(Logger.SEVERE, "Could not get available loggers.");						
					}
				}
				
				updateCost();
				//writeData();
			}
		});		
		
	}
	
	private void interpretParameters() {
		if (getArguments() != null && getArguments().length >= 4) {
			this.goalArmIndex = Integer.valueOf(getArguments()[0].toString());
			this.topTrigger = Long.valueOf(getArguments()[1].toString());
			this.bottonTrigger = Long.valueOf(getArguments()[2].toString());
			
			this.armName = getArguments()[3].toString();			
			
			this.cooptedWitnesses = createCooptedWitnesses(Integer.valueOf(getArguments()[4].toString()));
		} else {
			logger.log(Logger.SEVERE, "It was not possible to interpret the parameters");
		}
	}	
	
	private void informCooptedNewEpsilon() {
		cooptedWitnesses.forEach(x -> {
			ACLMessage msgSend = new ACLMessage(ACLMessage.INFORM);
			msgSend.addReceiver(new AID(x, true));
			msgSend.setProtocol(MessageProtocols.Inform_New_Epsilon.name());
			msgSend.setContent(SerializationHelper.serialize(errorEpsilon));
			send(msgSend);			
		});
	}
	
	private void informLoggers() throws FIPAException {
		Set<AID> loggers = LoggerServiceDescriptor.search(this);
		loggers.forEach(x -> {
			ACLMessage msgSend = new ACLMessage(ACLMessage.INFORM);
			msgSend.addReceiver(x);
			msgSend.setProtocol(MessageProtocols.Inform_Accumm_Cost.name());
			msgSend.setContent(SerializationHelper.serialize(new AttackerInformation(errorEpsilon, cost)));
			send(msgSend);						
		});
	}
	
	private void updateCost() {
		cost += errorEpsilon;
	}	
	 
	private Double updateEpsilon(Double updateFactor) {
		errorEpsilon = Math.min(Math.max(errorEpsilon + (updateFactor * ERROR_EPSILON_DELTA), 0D), 1D);
		
		logger.log(Logger.INFO, ">>> New epsilon is " + errorEpsilon);		
		
		return errorEpsilon;
	}
	
	private Long updatePullsObservation(Long pullsObserved) {
		Long delta = pullsObserved - lastPullsObserved;
		lastPullsObserved = pullsObserved;
		
		logger.log(Logger.INFO, "Target arm was pulled " + delta + " times during period.");
		
		return delta;
	}
	
	private Double calculateUpdateFactor(Long delta) {
		logger.log(Logger.INFO, "Delta: " + delta);
		
		if (delta > topTrigger) return -1.0;
		if (delta < bottonTrigger) return 1.0;
		return 0D;
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
					new String[] {armName}
				);
			
			witness.start();
			logger.log(Logger.INFO, "Coopted witness" + witness.getName() + " has been created.");
			return witness.getName();
			
		} catch (StaleProxyException e) {
			logger.log(Logger.SEVERE, "Error on creating broker viewer! " + e.getMessage());
			return null;
		}
	}
	
	private void writeData() {
		FileUtils.appendAttackerCost("attacker.csv", lastPullsObserved, errorEpsilon, cost, logger);
	}
}
