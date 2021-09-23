package br.com.tm.repfogagent.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import br.com.tm.repfogagent.trm.FIREtrm;
import br.com.tm.repfogagent.trm.Rating;
import br.com.tm.repfogagent.trm.components.CertifiedReputationComponent;
import br.com.tm.repfogagent.trm.components.InteractionTrustComponent;


public class TRMHelper {
	
	public static int RANDOM = 0;
	public static int INTERACTION_TRUST = 1;
	public static int CERTIFIED_TRUST = 2;
	public static int DEFAULT = 3;
	public static int COORDINATOR_UNTRUSTED = 4;
	public static int COORDINATOR_TRUSTED = 5;
	
	private double trustThreshold = 0.4; 
	
	private Map<String, List<Rating>> ratingsPerProvider;
	
	private Map<String, List<String>> superNodesForNodeCache = new HashMap<>();
	
	private static TRMHelper instance;
	
	private int iterationPack = 24;
	
	private int iterationSubpack = 6;
	
	private static final int step = 1; 
	
	private TRMHelper() {
		
	}
	
	public static TRMHelper getInstance() {
		if(instance == null) {
			instance = new TRMHelper();
		}
		
		return instance;
	}
	
	public Double calcLocalCoordinatorsTrust(String localCoordinator, Map<String, List<Rating>> lcRatingsDatabase) {
		Double defaultTrustValue = 1.0;
		
		
		
		return defaultTrustValue;
	}
	
	public Double[] calcSuperNodesCertifiedTrust(String node, String superNode, List<Rating> localRatingsPerSuperNode, Map<String, List<Rating>> supernodeRatingsDatabase, Date date, int iteration) {
		double valueCR;
		double valueIT;
		double reliabilityCR;
		double reliabilityIT;
		Map<String, Double> trmValuePerSuperNode = new HashMap<>();
		
		//Map<String, Map<String, List<Rating>>> supernodesPackRatingDatabase = loadSupernodesPackRatingDatabase(Arrays.asList(superNodes).stream().mapToInt(s -> s.getIndex()).toArray());
		
		double coefficientCertifiedReputation = 0.4;
		double coefficientInteractionTrust = 0.6;
		
		Map<String, List<Rating>> superNodeRatingDatabase = supernodeRatingsDatabase;
		
		
		List<Rating> superNodeRatingList = new ArrayList<>();
		superNodeRatingDatabase.values().stream().forEach(new Consumer<List<Rating>>() {
			@Override
			public void accept(List<Rating> t) {
				superNodeRatingList.addAll(t);
			}
		});
					
		CertifiedReputationComponent certifiedReputationComponent = new CertifiedReputationComponent(
				(-1) * (24 / Math.log(0.5)),
				coefficientCertifiedReputation,
				1,
				superNodeRatingDatabase,
				localRatingsPerSuperNode,
				0.5D
			);
		
		InteractionTrustComponent interactionTrustComponent = new InteractionTrustComponent(
				(-1) * (24 / Math.log(0.5)), 
				coefficientInteractionTrust
			);
		
		valueCR = certifiedReputationComponent.calculate(superNodeRatingList, iteration);
		reliabilityCR = certifiedReputationComponent.reliability(superNodeRatingList);
		
		valueCR = (valueCR == Double.NaN) ? 0 : valueCR;
		reliabilityCR = (reliabilityCR == Double.NaN) ? 0 : reliabilityCR;
		
		if(localRatingsPerSuperNode != null && localRatingsPerSuperNode.size() > 0) {
			valueIT = interactionTrustComponent.calculate(localRatingsPerSuperNode, iteration);
			reliabilityIT = interactionTrustComponent.reliability(localRatingsPerSuperNode);
		} else {
			valueIT = 0;
			reliabilityIT = 0;
		}
		
		valueIT = (valueIT == Double.NaN) ? 0 : valueIT;
		reliabilityIT = (reliabilityIT == Double.NaN) ? 0 : reliabilityIT;
		
		certifiedReputationComponent.setCalculatedValue(valueCR);
		certifiedReputationComponent.setCalculatedReliability(reliabilityCR);
		
		interactionTrustComponent.setCalculatedValue(valueIT);
		interactionTrustComponent.setCalculatedReliability(reliabilityIT);
		
		//System.out.println("\nEXECUTING TRM FOR - " + superNode + " - IT: " + valueIT + "/" + reliabilityIT + " - CR: " + valueCR + "/" + reliabilityCR);
		
		FIREtrm firEtrm = new FIREtrm(Arrays.asList(certifiedReputationComponent, interactionTrustComponent));
		Double fireValue = firEtrm.calculate();
		Double overallReliability = firEtrm.reliability();
		
		return new Double [] {fireValue, overallReliability};
	}
	
	public List<String> selectSuperNodesCertifiedTrust(String node, String superNode, List<Rating> localRatingsPerSuperNode, Map<String, List<Rating>> supernodeRatingsDatabase, Date date, int iteration) {
		double valueCR;
		double valueIT;
		double reliabilityCR;
		double reliabilityIT;
		Map<String, Double> trmValuePerSuperNode = new HashMap<>();
		
		//Map<String, Map<String, List<Rating>>> supernodesPackRatingDatabase = loadSupernodesPackRatingDatabase(Arrays.asList(superNodes).stream().mapToInt(s -> s.getIndex()).toArray());
		
		double coefficientCertifiedReputation = 0.4;
		double coefficientInteractionTrust = 0.6;
		
		Map<String, List<Rating>> superNodeRatingDatabase = supernodeRatingsDatabase;
		
		
		List<Rating> superNodeRatingList = new ArrayList<>();
		superNodeRatingDatabase.values().stream().forEach(new Consumer<List<Rating>>() {
			@Override
			public void accept(List<Rating> t) {
				superNodeRatingList.addAll(t);
			}
		});
					
		CertifiedReputationComponent certifiedReputationComponent = new CertifiedReputationComponent(
				(-1) * (24 / Math.log(0.5)),
				coefficientCertifiedReputation,
				1,
				superNodeRatingDatabase,
				localRatingsPerSuperNode,
				0.5D
			);
		
		InteractionTrustComponent interactionTrustComponent = new InteractionTrustComponent((-1) * (24 / Math.log(0.5)), coefficientInteractionTrust);
		
		valueCR = certifiedReputationComponent.calculate(superNodeRatingList, iteration);
		reliabilityCR = certifiedReputationComponent.reliability(superNodeRatingList);
		
		valueCR = (valueCR == Double.NaN) ? 0 : valueCR;
		reliabilityCR = (reliabilityCR == Double.NaN) ? 0 : reliabilityCR;
		
		
		if(localRatingsPerSuperNode != null && localRatingsPerSuperNode.size() > 0) {
			valueIT = interactionTrustComponent.calculate(localRatingsPerSuperNode, iteration);
			reliabilityIT = interactionTrustComponent.reliability(localRatingsPerSuperNode);
		} else {
			valueIT = 0;
			reliabilityIT = 0;
		}
		
		valueIT = (valueIT == Double.NaN) ? 0 : valueIT;
		reliabilityIT = (reliabilityIT == Double.NaN) ? 0 : reliabilityIT;
		
		certifiedReputationComponent.setCalculatedValue(valueCR);
		certifiedReputationComponent.setCalculatedReliability(reliabilityCR);
		
		interactionTrustComponent.setCalculatedValue(valueIT);
		interactionTrustComponent.setCalculatedReliability(reliabilityIT);
		
		//System.out.println("\nEXECUTING TRM FOR - " + superNode + " - IT: " + valueIT + "/" + reliabilityIT + " - CR: " + valueCR + "/" + reliabilityCR);
		
		FIREtrm firEtrm = new FIREtrm(Arrays.asList(certifiedReputationComponent, interactionTrustComponent));
		Double fireValue = firEtrm.calculate();
		Double overallReliability = firEtrm.reliability();
		
		if(!Double.isNaN(fireValue)) {
			trmValuePerSuperNode.put(superNode, fireValue);
		} else {
			trmValuePerSuperNode.put(superNode, 0D);
		}
			
		
		
		if(trmValuePerSuperNode.entrySet().iterator().hasNext()) {
			trmValuePerSuperNode = Util.sortByValue(trmValuePerSuperNode);
			
			/*for(Entry<Integer, Double> entrada : trmValuePerSuperNode.entrySet()) {
				System.out.println(entrada.getKey() + " - " + entrada.getValue());
			}*/
			
			return new ArrayList<>(filterByTrustThreshold(trmValuePerSuperNode));
		}
		
		return new ArrayList<>();
	}

	public Double calcSuperNodesInteractionTrust(String node, String superNode, List<Rating> localRatingsPerSuperNode, Date date, int iteration) {
		double reliabilityIT;
		double valueIT;
		
		Map<String, Double> trmValuePerSuperNode = new HashMap<>();
		
		InteractionTrustComponent interactionTrustComponent = new InteractionTrustComponent(0.5, 0.6);
		if(localRatingsPerSuperNode != null && localRatingsPerSuperNode.size() > 0) {
			valueIT = interactionTrustComponent.calculate(localRatingsPerSuperNode, iteration);
			reliabilityIT = interactionTrustComponent.reliability(localRatingsPerSuperNode);
			
			interactionTrustComponent.setCalculatedValue(valueIT);
			interactionTrustComponent.setCalculatedReliability(reliabilityIT);
			
			FIREtrm firEtrm = new FIREtrm(Arrays.asList(interactionTrustComponent));
			Double fireValue = firEtrm.calculate();
			
			if(fireValue > 0) {
				return fireValue;
			}
		}
		
		return 0.0;
	}
	
	public List<String> selectSuperNodesInteractionTrust(String node, String superNode, List<Rating> localRatingsPerSuperNode, Date date, int iteration) {
		double reliabilityIT;
		double valueIT;
		
		Map<String, Double> trmValuePerSuperNode = new HashMap<>();
		
		InteractionTrustComponent interactionTrustComponent = new InteractionTrustComponent(0.5, 0.6);
		if(localRatingsPerSuperNode != null && localRatingsPerSuperNode.size() > 0) {
			valueIT = interactionTrustComponent.calculate(localRatingsPerSuperNode, iteration);
			reliabilityIT = interactionTrustComponent.reliability(localRatingsPerSuperNode);
			
			interactionTrustComponent.setCalculatedValue(valueIT);
			interactionTrustComponent.setCalculatedReliability(reliabilityIT);
			
			FIREtrm firEtrm = new FIREtrm(Arrays.asList(interactionTrustComponent));
			Double fireValue = firEtrm.calculate();
			
			if(fireValue > 0) {
				trmValuePerSuperNode.put(superNode, fireValue);
			}
		} else {
			trmValuePerSuperNode.put(superNode, 0D);
		}
		
		
		if(trmValuePerSuperNode.size() > 0) {
			trmValuePerSuperNode = Util.sortByValue(trmValuePerSuperNode);	
			//return new ArrayList<>(trmValuePerSuperNode.keySet());
			
			return new ArrayList<>(filterByTrustThreshold(trmValuePerSuperNode));
		}
			
		return new ArrayList<>();
		
	}
	
	public Double[] calcSuperNodesCertificate(String node, String superNode, List<Rating> localRatingsPerSuperNode, Map<String, List<Rating>> supernodeRatingsDatabase, Date date, int iteration) {
		double valueCR;
		double reliabilityCR;
		double coefficientCertifiedReputation = 0.4;
		
		Map<String, List<Rating>> superNodeRatingDatabase = supernodeRatingsDatabase;
		
		List<Rating> superNodeRatingList = new ArrayList<>();
		superNodeRatingDatabase.values().stream().forEach(new Consumer<List<Rating>>() {
			@Override
			public void accept(List<Rating> t) {
				superNodeRatingList.addAll(t);
			}
		});
		
		CertifiedReputationComponent certifiedReputationComponent = new CertifiedReputationComponent(
				(-1) * (24 / Math.log(0.5)),
				coefficientCertifiedReputation,
				1,
				superNodeRatingDatabase,
				localRatingsPerSuperNode,
				0.5D
			);
		
		valueCR = certifiedReputationComponent.calculate(superNodeRatingList, iteration);
		reliabilityCR = certifiedReputationComponent.reliability(superNodeRatingList);
		
		valueCR = (valueCR == Double.NaN) ? 0 : valueCR;
		reliabilityCR = (reliabilityCR == Double.NaN) ? 0 : reliabilityCR;
		
		return new Double[] {valueCR, reliabilityCR};
	}
	
	public Set<String> filterByTrustThreshold(Map<String, Double> trmValuePerSuperNode) {
		return trmValuePerSuperNode.entrySet().stream().filter(map -> map.getValue().doubleValue() >= this.trustThreshold).collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue())).keySet();
	}
	
	private Map<String, List<Rating>> createReferee(Map<String, List<Rating>> originalMap, String serverName) {
		Map<String, List<Rating>> mapFiltrado = new HashMap<>();
		
		for(Rating r : originalMap.get(serverName)) {
			if(!mapFiltrado.containsKey(r.getNodeName())) {
				mapFiltrado.put(r.getNodeName(), new ArrayList<>());
			}
			
			mapFiltrado.get(r.getNodeName()).add(r);
		}
		
		return mapFiltrado;
	}
	
	public Map<String, List<Rating>> getRatingsPerProvider() {
		return ratingsPerProvider;
	}

	public void setRatingsPerProvider(Map<String, List<Rating>> ratingsPerProvider) {
		this.ratingsPerProvider = ratingsPerProvider;
	}

	public static void setInstance(TRMHelper instance) {
		TRMHelper.instance = instance;
	}
	
	private List<Rating> ordernarRatings(List<Rating> rates) {
		Collections.sort(rates, new Comparator<Rating>() {
			public int compare(Rating o1, Rating o2) {
				if(o1.getValue() > o2.getValue()) {
					return 1;
				} else if (o1.getValue() < o2.getValue()) {
					return -1;
				} else {
					return 0;
				}
			};
		});
		
		return rates;
	}
	
	public List<Rating> normaliseToRange(List<Rating> rates, double min, double max) {
		double normalized = 0;
		
		for(Rating rating : rates) {
			normalized = ((2 * (rating.getValue() - min)) / (max - min)) -1;
			rating.setNormalizedValue(normalized);
		}
		
		return rates;
	}
		
	public int getIterationPack() {
		return iterationPack;
	}

	public void setIterationPack(int iterationPack) {
		this.iterationPack = iterationPack;
	}

	public Map<String, List<String>> getSuperNodesForNodeCache() {
		return superNodesForNodeCache;
	}

	public void setSuperNodesForNodeCache(Map<String, List<String>> superNodesForNodeCache) {
		this.superNodesForNodeCache = superNodesForNodeCache;
	}

	public int getIterationSubpack() {
		return iterationSubpack;
	}

	public void setIterationSubpack(int iterationSubpack) {
		this.iterationSubpack = iterationSubpack;
	}
}
