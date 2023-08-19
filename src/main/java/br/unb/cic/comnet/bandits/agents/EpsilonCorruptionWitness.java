package br.unb.cic.comnet.bandits.agents;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.gson.reflect.TypeToken;

import br.unb.cic.comnet.bandits.arms.BanditArm;
import br.unb.cic.comnet.bandits.environment.Environment;
import br.unb.cic.comnet.bandits.utils.SerializationHelper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class EpsilonCorruptionWitness extends Agent {
	private static final long serialVersionUID = 1L;
	
	Logger logger = Logger.getJADELogger(getClass().getName());	
	
	private InfoRounds infoRounds;
	private Random random;
	
	private Double epsilonCorruption = 0D;
	private String armName;
	
	public Map<String, Double> getResumedRewards() {
		return infoRounds.resumeRewards();
	}
	
	public EpsilonCorruptionWitness() {
		this.infoRounds = new InfoRounds();
		this.random = new SecureRandom();
	}
	
	@Override
	protected void setup() {
		interpretParamenters();		
		
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
					try {
						sendCorruptedRewards(msg);
						informLoggers();						
					} catch (FIPAException fe) {
						fe.printStackTrace();
						logger.log(Logger.SEVERE, "Could not found available loggers.");						
					}
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
		
		addBehaviour(new CyclicBehaviour(this) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void action() {
				ACLMessage msg = myAgent.receive(informEpsilonTemplate());
				if (msg != null) {
					Double newEpsilon = SerializationHelper.unserialize(msg.getContent(), new TypeToken<Double>() {});
					epsilonCorruption = newEpsilon;
					logger.log(Logger.INFO, "New epsilon informed: " + epsilonCorruption);
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
		
		publishMe();
	}
	
	private void sendCorruptedRewards(ACLMessage msg) {
		ACLMessage msgSend = new ACLMessage(ACLMessage.CONFIRM);
		msgSend.addReceiver(msg.getSender());
		msgSend.setProtocol(MessageProtocols.Sending_Ratings.name());
		msgSend.setContent(SerializationHelper.serialize(corruptRewards(infoRounds.resumeRewards())));
		send(msgSend);
	}	
	
	private void informLoggers() throws FIPAException {
		Set<AID> loggers = LoggerServiceDescriptor.search(this);
		loggers.forEach(x -> {
			ACLMessage msgSend = new ACLMessage(ACLMessage.INFORM);
			msgSend.addReceiver(x);
			msgSend.setProtocol(MessageProtocols.Inform_Accumm_Cost.name());
			msgSend.setContent(SerializationHelper.serialize(resumeCorruption()));
			send(msgSend);						
		});
	}	
	
	private Double resumeCorruption() {
		return Environment.getArms().size() * epsilonCorruption;
	}	
	
	private void interpretParamenters() {
		if (getArguments() != null && getArguments().length > 0) {
			this.armName = getArguments()[0].toString();
		}
	}
	
	private Map<String, Double> corruptRewards(Map<String, Double> rewards) {
		Map<String, Double> corruptedRewards = new HashMap<String, Double>();
		for(String name : rewards.keySet()) {
			corruptedRewards.put(
				name, 
				rewards.get(name) + ((name.equals(armName) ? 1 : -1) * epsilonCorruption)
			);
		}
		return corruptedRewards;
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
