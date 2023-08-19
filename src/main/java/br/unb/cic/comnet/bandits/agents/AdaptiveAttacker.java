package br.unb.cic.comnet.bandits.agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.unb.cic.comnet.bandits.arms.BanditArm;
import br.unb.cic.comnet.bandits.environment.Environment;
import br.unb.cic.comnet.bandits.utils.SerializationHelper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class AdaptiveAttacker extends Agent {
	private static final long serialVersionUID = 1L;
	
	private static final Double ERROR_EPSILON_DELTA = 0.050D;
	
	private Logger logger = Logger.getJADELogger(getClass().getName());		
	
	private Double topTrigger;
	private Double bottonTrigger;
	private String targetArm = "";
	
	private List<String> cooptedWitnesses;
	
	private Double errorEpsilon;
	private Double cost;
	
	public AdaptiveAttacker() {
		this.errorEpsilon = 0D;
		this.cost = 0D;
	}
	
	@Override
	protected void setup() {
		interpretParameters();
		
		addBehaviour(new TickerBehaviour(this, 100) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onTick() {
				Double epsilonUpdateFactor = calculateUpdateFactor(calculateTargetArmRatio(Environment.getArmsMap()));
				if (epsilonUpdateFactor != 0D) {
					updateEpsilon(epsilonUpdateFactor);
					informCooptedNewEpsilon();
				}
				updateCost();
			}
		});		
		
	}
	
	private void interpretParameters() {
		if (getArguments() != null && getArguments().length >= 4) {
			this.targetArm = getArguments()[0].toString();
			this.topTrigger = Double.valueOf(getArguments()[1].toString());
			this.bottonTrigger = Double.valueOf(getArguments()[2].toString());
			this.cooptedWitnesses = createCooptedWitnesses(Integer.valueOf(getArguments()[3].toString()));
		} else {
			logger.log(Logger.SEVERE, "It was not possible to interpret the parameters");
		}
	}	
	
	private void informCooptedNewEpsilon() {
		cooptedWitnesses.forEach(x -> {
			ACLMessage msgSend = new ACLMessage(ACLMessage.INFORM);
			msgSend.addReceiver(new AID(x, true));
			msgSend.setProtocol(MessageProtocols.Inform_New_Corruption.name());
			msgSend.setContent(SerializationHelper.serialize(errorEpsilon));
			send(msgSend);			
		});
	}
	
	private void updateCost() {
		cost += errorEpsilon;
	}	
	 
	private synchronized Double updateEpsilon(Double updateFactor) {
		errorEpsilon = Math.min(Math.max(errorEpsilon + (updateFactor * ERROR_EPSILON_DELTA), 0D), 1D);
		
		logger.log(Logger.INFO, ">>> New epsilon is " + errorEpsilon);		
		
		return errorEpsilon;
	}
	
	private Double calculateUpdateFactor(Double ratio) {
		logger.log(Logger.INFO, "Ratio: " + ratio);
		
		if (ratio > topTrigger) return -1.0;
		if (ratio < bottonTrigger) return 1.0;
		return 0D;
	}
	
	private Double calculateTargetArmRatio(Map<String, BanditArm> arms) {
		Long sum = arms.values().stream().collect(Collectors.summarizingLong(BanditArm::getPulls)).getSum();
		Long targetArmPulls = arms.get(targetArm).getPulls();
		
		if (sum == 0) return 0D;
		
		return Double.valueOf(targetArmPulls) / sum;
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
