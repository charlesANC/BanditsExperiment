package br.unb.cic.comnet.bandits.agents;

import java.util.ArrayList;
import java.util.HashMap;
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

public class JunEpsilonGreedyAttacker extends Agent {
	private static final long serialVersionUID = 1L;
	
	private Logger logger = Logger.getJADELogger(getClass().getName());	
	
	protected String targetArm;
	protected Double sigma;  // talvez esse tenha de ser um parâmetro do ambiente
	protected Double delta;
	
	private Double avgAttack;
	private Double cost;
	
	
	private List<String> cooptedWitnesses;	
	
	private Map<String, Double> corruption;
	
	public JunEpsilonGreedyAttacker() {
		this.corruption = new HashMap<String, Double>();
		this.avgAttack = 0D;
		this.cost = 0D;
	}
	
	@Override
	protected void setup() {
		interpretParameters();
		
		addBehaviour(new TickerBehaviour(this, 100) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onTick() {
				boolean hasChanges = updateCorruption(Environment.getArmsMap());
				if (hasChanges) {
					informCooptedNewCorruption();
				}
				updateCost();
			}
		});	
		
	}
	
	public void updateCost() {
		avgAttack = corruption.values().stream().collect(Collectors.averagingDouble(x -> x.doubleValue()));
		cost += avgAttack;
	}
	
	private void interpretParameters() {
		if (getArguments() != null && getArguments().length >= 4) {
			this.targetArm = getArguments()[0].toString();
			this.delta = Double.valueOf(getArguments()[1].toString());
			this.sigma = Double.valueOf(getArguments()[2].toString());			
			this.cooptedWitnesses = createCooptedWitnesses(Integer.valueOf(getArguments()[3].toString()));
		} else {
			logger.log(Logger.SEVERE, "It was not possible to interpret the parameters");
		}
	}	
	
	private boolean updateCorruption(Map<String, BanditArm> arms) {
		boolean hasChanges = false;
		
		Integer armsNumber = arms.size();
		BanditArm target = arms.get(targetArm);
		List<BanditArm> others = new ArrayList<>(arms.values());
		others.remove(target);
		
		logger.log(Logger.INFO, "** Size of others: " + others.size());
		
		for(BanditArm arm : others) {
			Double attack = calculateAttack(arm, target, armsNumber);
			if (putCorruption(arm.getName(), attack)) {
				hasChanges = true;
			}
		}
		
		return hasChanges;
	}
	
	protected boolean putCorruption(String armName, Double attack) {
		if (!attack.equals(corruption.getOrDefault(armName, -1D))) {
			corruption.put(armName, attack);
			return true;
		}
		return false;
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
					JunCorruptedWitness.class.getName(), 
					new String[] {}
				);
			
			witness.start();
			logger.log(Logger.INFO, "Coopted witness" + witness.getName() + " has been created.");
			return witness.getName();
			
		} catch (StaleProxyException e) {
			logger.log(Logger.SEVERE, "Error on creating broker viewer! " + e.getMessage());
			return null;
		}
	}	
	
	private void informCooptedNewCorruption() {
		cooptedWitnesses.forEach(x -> {
			ACLMessage msgSend = new ACLMessage(ACLMessage.INFORM);
			msgSend.addReceiver(new AID(x, true));
			msgSend.setProtocol(MessageProtocols.Inform_New_Corruption.name());
			msgSend.setContent(SerializationHelper.serialize(corruption));
			logger.log(Logger.INFO, "Corruption sent: " + corruption);
			send(msgSend);			
		});
	}	
	
	protected Double calculateAttack(
		BanditArm attackedArm,
		BanditArm targetArm, 
		Integer armsNumber
	) {
		Double attack =  attackedArmAmount(attackedArm) 
			- targetArmAmount(attackedArm, targetArm, armsNumber);
		
		return Math.max(attack, 0D);
	}
	
	protected Double attackedArmAmount(BanditArm attackedArm) {
		Double attackedArmAverage = attackedArm.getAverageReward();
		Long attackedArmPulls = attackedArm.getPulls();
		
		if (attackedArmAverage == 0D || attackedArmPulls == 0D) {
			return 0D;
		}
		
		return attackedArmAverage * (attackedArmPulls + 1);
	}
	
	protected Double targetArmAmount(
		BanditArm attackedArm,
		BanditArm targetArm, 
		Integer armsNumber
	) {
		Long attackedArmPulls = attackedArm.getPulls();
		return targetAverageDiff(targetArm, sigma, delta, armsNumber) * (attackedArmPulls + 1);
	}
	
	protected Double targetAverageDiff(
		BanditArm targetArm,
		Double sigma, 
		Double delta, 
		Integer armsNumber
	) {
		Long n = targetArm.getPulls();		
		if (n == 0) {
			return 0D;
		}
		
		Integer k = armsNumber;
		Double expectedAverage = targetArm.getAverageReward();
		
		return expectedAverage - 2 * beta(n, k, sigma, delta);
	}
	
	private Double beta(
		Long n, 		// number of times which target arm was pulled 
		Integer k, 		// total number of arms
		Double sigma, 	// sub-Gaussian noise factor
		Double delta	// Paper parameter --> must be < 0.5
	) {
		return Math.sqrt(
			(2 * (sigma * sigma) / n) * 
			Math.log((Math.PI * Math.PI * k * n * n) / (3 * sigma))
		);
	}

}
