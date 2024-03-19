package br.unb.cic.comnet.bandits.agents;

import java.util.ArrayList;
import java.util.List;

import br.unb.cic.comnet.bandits.agents.ratings.InfoRoundsPredefined;
import br.unb.cic.comnet.bandits.agents.ratings.Opinion;
import br.unb.cic.comnet.bandits.environment.GeneralParameters;
import jade.util.Logger;

public class PredefinedRatingWitness extends AbstractWitness {
	private static final long serialVersionUID = 1L;
	
	Logger logger = Logger.getJADELogger(getClass().getName());
	
	private List<String> witnessesNames;
	private List<InfoRoundsPredefined> infoRounds;
	
	@Override
	protected void setup() {
		logger.info("Setting up the witness " + getLocalName() + "...");
		interpretParameters();
		loadPredefinedRatings();
		
		super.setup();
	}
	
	private void interpretParameters() {
		if (getArguments() != null && getArguments().length != 0) {
			this.witnessesNames = new ArrayList<>();
			for(int i = 0; i < getArguments().length; i++) {
				this.witnessesNames.add(getArguments()[i].toString());
			}
		}		
	}
	
	private void loadPredefinedRatings() {
		if (this.witnessesNames != null && !this.witnessesNames.isEmpty()) {
			this.infoRounds = new ArrayList<>();
			for(String name : this.witnessesNames) {
				this.infoRounds.add(
					GeneralParameters
						.getGeneralOpinionHolder()
							.getOpinionsByWitness(name)
				);
			}
		}
	}

	@Override
	public List<Opinion> returnOpinions(Integer round) {
		List<Opinion> opinions = new ArrayList<>();
		infoRounds.stream().forEach(i -> opinions.addAll(i.getLastProductOpinions(round)));
		return opinions;
	}

	@Override
	public Double accumulatedReward() {
		Double accumulated = 0D;
		for(InfoRoundsPredefined info : this.infoRounds) {
			accumulated += info.accumulatedReward();
		}
		return accumulated;
	}

	@Override
	public void afterSendingOpinions() {
		// TODO Auto-generated method stub
	}
}
