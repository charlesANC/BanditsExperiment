package br.unb.cic.comnet.bandits.environment;

import java.io.File;
import java.util.Optional;

import br.unb.cic.comnet.bandits.agents.ratings.OpinionsHolder;

public class GeneralParameters {
	private static final Integer NUM_OF_ROUNDS = 1000;
	private static final Double EPSILON = 0.8D;
	private static final String OUTPUT_DIRECTORY = "c:\\temp\\";
	
	private static GeneralParameters generalParameters = null;	

	private Integer numOfRounds;
	private Double epsilon;
	private String outputDirectory;
	private OpinionsHolder opinionsHolder;
	
	public static void initilizeParameters(OpinionsHolder holder, String outputDirectory, Integer numOfRounds, Double epsilon) {
		generalParameters = new GeneralParameters(
			Optional.ofNullable(holder).orElse(new OpinionsHolder()),
			Optional.ofNullable(outputDirectory).orElse(OUTPUT_DIRECTORY), 
			Optional.ofNullable(numOfRounds).orElse(NUM_OF_ROUNDS), 
			Optional.ofNullable(epsilon).orElse(EPSILON)
		);
	}
	
	public static GeneralParameters getGeneralParameters() {
		if (generalParameters == null) {
			initilizeParameters(null, null, null, null);
		}
		return generalParameters;
	}
	
	public OpinionsHolder getOpinionsHolder() {
		return opinionsHolder;
	}
	
	public static OpinionsHolder getGeneralOpinionHolder() {
		return getGeneralParameters().getOpinionsHolder();
	}
	
	public static Integer getGeneralNumOfRounds() {
		return getGeneralParameters().getNumOfRounds();
	}	
	
	public static Double getGeneralEpsilon() {
		return getGeneralParameters().getEpsilon();
	}
	
	public static String mountOutputFileName(String fileName) {
		return new File(
				getGeneralParameters().getOutputDirectory(), 
				fileName
			).getAbsolutePath();
	}		
	
	public String getOutputDirectory() {
		return outputDirectory;
	}
	
	public Integer getNumOfRounds() {
		return numOfRounds;
	}
	
	public Double getEpsilon() {
		return epsilon;
	}
	
	private GeneralParameters(OpinionsHolder holder, String directory, int numOfRounds, double epsilon) {
		this.opinionsHolder = holder;
		this.outputDirectory = directory;
		this.numOfRounds = numOfRounds;
		this.epsilon = epsilon;
	}
}
