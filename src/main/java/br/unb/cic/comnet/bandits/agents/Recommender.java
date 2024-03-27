package br.unb.cic.comnet.bandits.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.reflect.TypeToken;

import br.unb.cic.comnet.bandits.agents.ratings.Opinion;
import br.unb.cic.comnet.bandits.agents.trm.ArmsEvaluator;
import br.unb.cic.comnet.bandits.agents.trm.ArmsEvaluatorFactory;
import br.unb.cic.comnet.bandits.algorithms.BanditAlgorithm;
import br.unb.cic.comnet.bandits.algorithms.BanditAlgorithmFactory;
import br.unb.cic.comnet.bandits.utils.SerializationHelper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
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
	private ArmsEvaluator evaluator;
	
	private Integer witnessWaiting;
	private AID requester;
	private Integer round;
	
	public Recommender() {
		this.armsInfo = new ConcurrentHashMap<String, ArmInfo>();
	}
	
	@Override
	public void setup() {
		interpretParameters();
		
		addBehaviour(new CyclicBehaviour(this) {
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				ACLMessage msg = myAgent.receive(template());
				if (msg != null) {
					List<Opinion> opinions = SerializationHelper
							.unserialize(msg.getContent(), 
									new TypeToken<List<Opinion>>(){});
					
					processRatings(msg.getSender().getLocalName(), opinions);
					
					witnessWaiting--;
					if (witnessWaiting == 0) {
						sendRecommendation();
					}
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
					acceptRequest(msg);
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
		
		/*
		
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
		
		*/
		
		publishMe();
	}
	
	@Override
	public void takeDown() {
		// print all ratings received by arm!
		/*
		System.out.println("---------");
		for(String arm: armsInfo.keySet()) {
			for(Rating r : armsInfo.get(arm).allRatings()) {
				System.out.println(String.format("%s;%d;%.4f", arm, r.getIteration(), r.getValue()));
			}
		}
		System.out.println("---------");		
		*/
		unpublishMe();
		logger.log(Logger.INFO, "Getting out of here!");
	}	
	
	private void interpretParameters() {
		String banditAlgorithmName = "simple_averaging";
		String evaluatorName = "simplemean";
		List<String> parameters = new ArrayList<String>();
		if (getArguments() != null && getArguments().length != 0) {
			banditAlgorithmName = getArguments()[0].toString();
			evaluatorName = getArguments()[1].toString();
			parameters = catchParameters(getArguments(), 2);
		}
		
		this.recommendAlgorithm = BanditAlgorithmFactory.create(banditAlgorithmName);
		
		Optional<ArmsEvaluator> opEvaluator = ArmsEvaluatorFactory.createEvaluator(evaluatorName, parameters);
		if (opEvaluator.isEmpty()) {
			logger.info("Cannot instantiate evaluator. Reducing to simplemean...");
			this.evaluator = ArmsEvaluatorFactory.createEvaluator("simplemean", new ArrayList<String>()).get();
		} else {
			this.evaluator = opEvaluator.get();
		}
	}
	
	private List<String> catchParameters(Object[] arguments, int start) {
		List<String> parameters = new ArrayList<String>();
		for(int i = start; i < arguments.length; i++) {
			parameters.add(arguments[i].toString());
		}
		return parameters;
	}
	
	private void processRatings(String player, List<Opinion> opinions) {
		for(Opinion opinion : opinions) {
			String arm = opinion.getArm();
			if (!armsInfo.containsKey(arm)) {
				armsInfo.put(arm, new ArmInfo(arm));
			} 
			armsInfo.get(arm).addEvaluation(player, opinion);
		}
	}
	
	private void acceptRequest(ACLMessage msg) {
		InfoRounds infoRounds = SerializationHelper
				.unserialize(msg.getContent(), 
						new TypeToken<InfoRounds>() {}); 
		
		this.requester = msg.getSender();
		this.round = infoRounds.getRound();
		this.witnessWaiting = requestRatings(round);		
	}
	
	private void sendRecommendation() {
		evaluator.evaluateArms(armsInfo.values());					
		ACLMessage response = new ACLMessage(ACLMessage.CONFIRM);
		response.addReceiver(requester);
		response.setProtocol(MessageProtocols.Arm_Recomendation.name());
		response.setContent(recommendedArm(round));
		send(response);			
	}
	
	private Integer requestRatings(Integer round) {
		try {
			Set<AID> witnesses = WitnessServiceDescriptor.search(this);
			for(AID witness : witnesses) {
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.addReceiver(witness);
				msg.setProtocol(MessageProtocols.Request_Ratings.name());
				msg.setContent(SerializationHelper.serialize(round));
				this.send(msg);							
			}
			return witnesses.size();
		} catch (FIPAException e) {
			e.printStackTrace();
			logger.log(Logger.SEVERE, "I cannot list the witness. " + getName());					
			return 0;
		}		
	}
	
	private String recommendedArm(long round) {
		String arm = recommendAlgorithm.choose(resumeRating(), round);
		return arm;
	}
	
	private Map<String, Double> resumeRating() {
		Map<String, Double> resumed = new HashMap<String, Double>();
		for(String arm : armsInfo.keySet()) {
			resumed.put(arm, armsInfo.get(arm).getTrustworth());				
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
