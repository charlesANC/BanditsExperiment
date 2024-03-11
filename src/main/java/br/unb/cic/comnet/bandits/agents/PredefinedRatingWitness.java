package br.unb.cic.comnet.bandits.agents;

import java.util.List;

import br.unb.cic.comnet.bandits.agents.ratings.InfoRoundsPredefined;
import br.unb.cic.comnet.bandits.agents.ratings.Opinion;
import br.unb.cic.comnet.bandits.environment.GeneralParameters;
import jade.util.Logger;

public class PredefinedRatingWitness extends AbstractWitness {
	private static final long serialVersionUID = 1L;
	
	Logger logger = Logger.getJADELogger(getClass().getName());
	
	private String name;
	private InfoRoundsPredefined infoRounds;
	
	@Override
	protected void setup() {
		interpretParameters();
		loadPredefinedRatings();
		
		super.setup();
	}
	
	private void interpretParameters() {
		if (getArguments() != null && getArguments().length != 0) {
			this.name = getArguments()[0].toString();
		}		
	}
	
	private void loadPredefinedRatings() {
		if (this.name != null) {
			List<Opinion> opinions = GeneralParameters.getGeneralOpinionHolder().getOpinionsByWitness(name);
			this.infoRounds = new InfoRoundsPredefined(opinions);
		}
	}

	@Override
	public List<Opinion> returnOpinions(Integer round) {
		return infoRounds.getLastProductOpinions(round);
	}

	@Override
	public Double accumulatedReward() {
		return infoRounds.accumulatedReward();
	}

	@Override
	public void afterSendingOpinions() {
		// TODO Auto-generated method stub
	}
}
