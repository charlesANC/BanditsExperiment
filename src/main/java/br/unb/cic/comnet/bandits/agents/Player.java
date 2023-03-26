package br.unb.cic.comnet.bandits.agents;

import java.util.Set;

import br.unb.cic.comnet.bandits.environment.Environment;
import br.unb.cic.comnet.bandits.utils.FileUtils;
import br.unb.cic.comnet.bandits.utils.SerializationHelper;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.ShutdownPlatform;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class Player extends Agent {
	private static final long serialVersionUID = 1L;
	
	Logger logger = Logger.getJADELogger(getClass().getName());	
	
	private InfoRounds infoRounds;
	
	public Player() {
		this.infoRounds = new InfoRounds();
	}
	
	@Override
	public void setup() {
		addBehaviour(new OneShotBehaviour(this) {
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				try {
					requestArmRecomendation();
				} catch (FIPAException e) {
					e.printStackTrace();
					logger.log(Logger.SEVERE, "Could not get the available recommenders.");
				}
			}
		});
		
		addBehaviour(new CyclicBehaviour(this) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void action() {
				ACLMessage msg = myAgent.receive(template());
				if (msg != null) {
					infoRounds.incrementRound();
					
					String arm = msg.getContent();
					Environment.getArm(arm).ifPresent(x -> addReward(arm, x.playersPull()));
					
					if (infoRounds.hasReachedEnd()) {
						logger.log(Logger.INFO, "Completed! I gonna die. " + getAgent().getLocalName());
						doDelete();
					}	
					
					try {
						sendInformation();
						requestArmRecomendation();
					} catch (FIPAException e) {
						e.printStackTrace();
						logger.log(Logger.SEVERE, "Could not get the available recommenders.");
					}
				} else {
					block();
				}
			}
			
			private MessageTemplate template() {
				return MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.CONFIRM), 
						MessageTemplate.MatchProtocol(MessageProtocols.Arm_Recomendation.name()));
			}			
		});		
		
		addBehaviour(new CyclicBehaviour(this) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void action() {
				ACLMessage msg = myAgent.receive(template());
				if (msg != null) {
					ACLMessage msgSend = new ACLMessage(ACLMessage.CONFIRM);
					msgSend.addReceiver(msg.getSender());
					msgSend.setProtocol(MessageProtocols.Sending_Ratings.name());
					msgSend.setContent(SerializationHelper.serialize(infoRounds.resumeRewards()));
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
		String info = infoRounds.processRewards();
		logger.log(Logger.INFO, info);
		FileUtils.appendRatingsInfo("player_reward.txt", info, logger);
		
		unpublishMe();
		shutdown();
	}	
	
	private void requestArmRecomendation() throws FIPAException {
		Set<AID> recommenders = RecommenderServiceDescriptor.search(this);
		for(AID recommender : recommenders) {
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.addReceiver(recommender);
			msg.setProtocol(MessageProtocols.Request_Arm_Recomendation.name());
			msg.setContent(SerializationHelper.serialize(infoRounds));
			this.send(msg);	
		}
	}
	
	private void sendInformation() throws FIPAException {
		Set<AID> loggers = LoggerServiceDescriptor.search(this);
		loggers.forEach(x -> {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(x);
			msg.setProtocol(MessageProtocols.Inform_Accumm_Reward.name());
			msg.setContent(SerializationHelper.serialize(infoRounds));
			this.send(msg);
		});
	}
	
	private void addReward(String name, double reward) {
		//logger.log(Logger.INFO, "Player got " + reward + " from the arm " + name + " at cycle " + infoRounds.getRound() + ".");
		infoRounds.addReward(name, reward);
	}	
	
	private void shutdown() {
		Codec codec = new SLCodec();    
		Ontology jmo = JADEManagementOntology.getInstance();
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(jmo);
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(getAMS());
		msg.setLanguage(codec.getName());
		msg.setOntology(jmo.getName());
		try {
		    getContentManager().fillContent(msg, new Action(getAID(), new ShutdownPlatform()));
		    send(msg);
		}
		catch (Exception e) {}		
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
