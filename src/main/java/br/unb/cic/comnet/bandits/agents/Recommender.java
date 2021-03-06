package br.unb.cic.comnet.bandits.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.reflect.TypeToken;

import br.unb.cic.comnet.bandits.agents.trm.FIRETranscoderEvaluator;
import br.unb.cic.comnet.bandits.algorithms.BanditAlgorithm;
import br.unb.cic.comnet.bandits.algorithms.BanditAlgorithmFactory;
import br.unb.cic.comnet.bandits.utils.FileUtils;
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

public class Recommender extends Agent {
	private static final long serialVersionUID = 1L;
	
	Logger logger = Logger.getJADELogger(getClass().getName());	
	
	private Map<String, ArmInfo> armsInfo;
	private BanditAlgorithm recommendAlgorithm;
	private String fileName;
	private boolean useTrust = false;
	
	public String getFileName() {
		if (fileName == null) {
			fileName = "ratings_" 
				+ recommendAlgorithm.getName() + "_" 
				+ (useTrust?"T":"A") 
				+ System.currentTimeMillis() 
				+ "_.txt";
		}
		return fileName;
	}
	
	public Recommender() {
		this.armsInfo = new ConcurrentHashMap<String, ArmInfo>();
	}
	
	@Override
	public void setup() {
		interpretParameters();
		
		addBehaviour(new TickerBehaviour(this, 200) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onTick() {
				try {
					Set<AID> witnesses = WitnessServiceDescriptor.search(getAgent());
					for(AID witness : witnesses) {
						ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
						msg.addReceiver(witness);
						msg.setProtocol(MessageProtocols.Request_Ratings.name());
						getAgent().send(msg);							
					}
				} catch (FIPAException e) {
					e.printStackTrace();
					logger.log(Logger.SEVERE, "I cannot lsit the witness. " + getName());					
				}
			}
		});
		
		addBehaviour(new CyclicBehaviour(this) {
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				ACLMessage msg = myAgent.receive(template());
				if (msg != null) {
					Map<String, Double> ratings = SerializationHelper
							.unserialize(msg.getContent(), 
									new TypeToken<Map<String, Double>>(){});
					
					processRatings(msg.getSender().getLocalName(), ratings);
				} else {
					block();
				}
			}
			
			private MessageTemplate template() {
				return MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.CONFIRM), 
						MessageTemplate.MatchProtocol(MessageProtocols.Sending_Ratings.name()));
			}			
		});
		
		addBehaviour(new CyclicBehaviour(this) {
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				ACLMessage msg = myAgent.receive(template());
				if (msg != null) {
					new FIRETranscoderEvaluator().evaluateTranscoders(armsInfo.values(), "p1");					
					
					InfoRounds infoRounds = SerializationHelper
							.unserialize(msg.getContent(), 
									new TypeToken<InfoRounds>() {}); 
							
					ACLMessage response = new ACLMessage(ACLMessage.CONFIRM);
					response.addReceiver(msg.getSender());
					response.setProtocol(MessageProtocols.Arm_Recomendation.name());
					response.setContent(recommendedArm(infoRounds.getRound()));
					getAgent().send(response);	
				} else {
					block();
				}
			}
			
			private MessageTemplate template() {
				return MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.REQUEST), 
						MessageTemplate.MatchProtocol(MessageProtocols.Request_Arm_Recomendation.name()));
			}			
		});		
		
		addBehaviour(new TickerBehaviour(this, 300) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onTick() {
				StringBuilder eval = new StringBuilder();
				eval.append("----");
				eval.append("Recommend algorithm: " + recommendAlgorithm.getName() + "\r\n");
				eval.append("Arms evaluation: \r\n");
				
				List<Entry<String, Double>> entries = new ArrayList<>(resumeRating().entrySet());
				entries.sort(Entry.comparingByKey());
				
				StringBuilder line = new StringBuilder();
				for(Entry<String, Double> entry : entries) {
					line.append(entry.getKey() + ";");
					line.append(String.format("%.4f;", entry.getValue()));
				}
				
				eval.append(line.toString() + "\r\n");
				eval.append("----");
				
				FileUtils.appendRatingsInfo(getFileName(), line.toString(), logger);
				
				logger.log(Logger.INFO, eval.toString());
			}
		});
		
		publishMe();
	}
	
	private void interpretParameters() {
		String banditAlgorithmName = "simple_averaging";
		if (getArguments() != null && getArguments().length != 0) {
			banditAlgorithmName = getArguments()[0].toString();
			useTrust = getArguments()[1].toString().equals("T");
		}
		this.recommendAlgorithm = BanditAlgorithmFactory.create(banditAlgorithmName);
	}

	@Override
	public void takeDown() {
		unpublishMe();
		logger.log(Logger.INFO, "Getting out of here!");
	}	
	
	private void processRatings(String player, Map<String, Double> ratings) {
		for(String arm : ratings.keySet()) {
			if (!armsInfo.containsKey(arm)) {
				armsInfo.put(arm, new ArmInfo(arm));
			} 
			armsInfo.get(arm).addEvaluation(player, ratings.get(arm));
		}
	}
	
	private String recommendedArm(long round) {
		return recommendAlgorithm.choose(resumeRating(), round);
	}
	
	private Map<String, Double> resumeRating() {
		Map<String, Double> resumed = new HashMap<String, Double>();
		for(String arm : armsInfo.keySet()) {
			if (useTrust) {
				resumed.put(arm, armsInfo.get(arm).getTrustworth());				
			} else {
				resumed.put(arm, armsInfo.get(arm).meanEvaluation());				
			}
		}	
		return resumed;
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
		desc.addServices(RecommenderServiceDescriptor.create(getLocalName()));
		try {
			DFService.register(this, desc);
		} catch (FIPAException e) {
			e.printStackTrace();
			logger.log(Logger.SEVERE, "I cannot publish myself. I am useless. I must die! " + getName());
			doDelete();
		}
	}	
}
