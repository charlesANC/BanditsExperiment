package br.unb.cic.comnet.bandits.environment;

import java.io.File;
import java.util.Optional;

public class GeneralParameters {
	private static final Long NUM_OF_ROUNDS = 1000L;
	private static final Double EPSILON = 0.8D;
	private static final String OUTPUT_DIRECTORY = "c:\\temp\\";
	
	private static GeneralParameters generalParameters = null;	

	private Long numOfRounds;
	private Double epsilon;
	private String outputDirectory;
	
	public static void initilizeParameters(String outputDirectory, Long numOfRounds, Double epsilon) {
		generalParameters = new GeneralParameters(
				Optional.ofNullable(outputDirectory).orElse(OUTPUT_DIRECTORY), 
				Optional.ofNullable(numOfRounds).orElse(NUM_OF_ROUNDS), 
				Optional.ofNullable(epsilon).orElse(EPSILON)
			);
	}
	
	public static GeneralParameters getGeneralParameters() {
		if (generalParameters == null) {
			initilizeParameters(null, null, null);
		}
		return generalParameters;
	}
	
	public static Long getGeneralNumOfRounds() {
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
	
	public Long getNumOfRounds() {
		return numOfRounds;
	}
	
	public Double getEpsilon() {
		return epsilon;
	}
	
	private GeneralParameters(String directory, long numOfRounds, double epsilon) {
		this.outputDirectory = directory;
		this.numOfRounds = numOfRounds;
		this.epsilon = epsilon;
	}
}
