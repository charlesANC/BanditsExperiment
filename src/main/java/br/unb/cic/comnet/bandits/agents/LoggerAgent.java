package br.unb.cic.comnet.bandits.agents;

import com.google.gson.reflect.TypeToken;

import br.unb.cic.comnet.bandits.arms.Arm;
import br.unb.cic.comnet.bandits.environment.Environment;
import br.unb.cic.comnet.bandits.utils.FileUtils;
import br.unb.cic.comnet.bandits.utils.SerializationHelper;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class LoggerAgent extends Agent {

	private static final long serialVersionUID = 1L;
	
	private Logger logger = Logger.getJADELogger(getClass().getName());		
	
	private String fileName;
	
	private Integer playerRound;
	private Double cummulativeReward;
	private Double corruptionCost;
	
	private String getFileName() {
		return fileName;
	}
	
	public LoggerAgent() {
		this.fileName = "execution_X_X_X.txt";
		this.playerRound = 0;
		this.cummulativeReward = 0D;
		this.corruptionCost = 0D;
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
					InfoRounds rounds = SerializationHelper.unserialize(msg.getContent(), new TypeToken<InfoRounds>() {});
					playerRound = rounds.getRound();
					cummulativeReward = rounds.getAccumulatedReward();
					
					FileUtils.appendRatingsInfo(getFileName(), resumeInformation(), logger);					
				} else {
					block();
				}
			}
			
			private MessageTemplate template() {
				return MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM), 
						MessageTemplate.MatchProtocol(MessageProtocols.Inform_Accumm_Reward.name()));
			}			
		});
		
		addBehaviour(new CyclicBehaviour(this) {
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				ACLMessage msg = myAgent.receive(template());
				if (msg != null) {
					Double corruption = SerializationHelper.unserialize(msg.getContent(), new TypeToken<Double>() {});
					corruptionCost += corruption;
				} else {
					block();
				}
			}
			
			private MessageTemplate template() {
				return MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM), 
						MessageTemplate.MatchProtocol(MessageProtocols.Inform_Accumm_Cost.name()));
			}			
		});
		
		/*
		addBehaviour(new TickerBehaviour(this, 400) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onTick() {
				StringBuilder eval = new StringBuilder();
				eval.append("---- $$ ");
				String line = resumeInformation(); 
				eval.append(line + "\r\n");
				eval.append("----");
				logger.log(Logger.INFO, eval.toString());
				
				//FileUtils.appendRatingsInfo(getFileName(), line, logger);
			}
		});
		*/		
		
		publishMe();
	}
	
	private String resumeInformation() {
		StringBuilder line = new StringBuilder();
		for(Arm arm: Environment.getSortedArms()) {
			line.append(String.format(
				"%s;%d;%.4f", 
				arm.getName(), 
				arm.getPulls(), 
				arm.getAverageReward()
			));
		}
		
		return String.format(
			"%d;%.4f;%.4f;%s", 
			playerRound, 
			cummulativeReward, 
			corruptionCost,
			line.toString()			
		);
	}
	
	private void interpretParameters() {
		if (getArguments() != null && getArguments().length != 0) {
			String recomendAlgorithm = getArguments()[0].toString();
			String evaluationType = getArguments()[1].toString();
			
			this.fileName = "execution_" 
					+ recomendAlgorithm + "_" 
					+ evaluationType + "_" 
					+ System.currentTimeMillis() 
					+ "_.txt"; 
		}
	}
	
	private void publishMe() {
		DFAgentDescription desc = new DFAgentDescription();
		desc.setName(getAID());
		desc.addServices(LoggerServiceDescriptor.create(getLocalName()));
		try {
			DFService.register(this, desc);
		} catch (FIPAException e) {
			e.printStackTrace();
			logger.log(Logger.SEVERE, "I cannot publish myself. I am useless. I must die! " + getName());
			doDelete();
		}
	}	

}
