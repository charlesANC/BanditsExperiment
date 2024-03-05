package br.unb.cic.comnet.bandits.agents.ratings;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * The order of the ratings must be step, user, product, category, rating
 * Ratings must be from 1 to 5 stars
 * @author charl
 *
 */
public class OpinionsHolder {
	
	private static final int STEP_FIELD = 0;
	private static final int WITNESS_FIELD = 1;
	private static final int PRODUCT_FIELD = 2;
	private static final int CATEGORY_FIELD = 3;
	private static final int RATING_FIELD = 4;
	
	private String fileName;
	
	private Map<String, List<Opinion>> opinionsByWitness;
	private Map<String, Map<Double, Integer>> productRatingsCount;
	
	private Double highestRating;
	private Double lowestRating;
	
	private boolean empty = true;
	
	public boolean isEmpty() {
		return empty;
	}
	
	public OpinionsHolder() {
	}
	
	public OpinionsHolder(String fileName) {
		this.fileName = fileName;
		this.opinionsByWitness = new LinkedHashMap<>();
		this.productRatingsCount  = new LinkedHashMap<>();
		this.highestRating = Double.MIN_VALUE;
		this.lowestRating = Double.MAX_VALUE;
	}
	
	public Set<String> getWitnesses() {
		return new HashSet<>(opinionsByWitness.keySet());
	}
	
	public List<Opinion> getOpinionsByWitness(String witness) {
		return new LinkedList<>(opinionsByWitness.get(witness));
	}
	
	public Set<String> getProducts() {
		return new HashSet<>(productRatingsCount.keySet());
	}
	
	public Map<Double, Double> getProductRatingFrequency(String product) {
		Map<Double, Double> map = new TreeMap<>();
		
		Double total = Double.valueOf(
			productRatingsCount.get(product).values()
				.stream()
					.reduce(Integer::sum).orElse(0).toString()
		);

		if (total > 0) {
			productRatingsCount.get(product).entrySet()
			.stream()
				.forEach(e -> map.put(e.getKey(), e.getValue() / total));			
		}
		
		return map; 
	}
	
	public Double scaleSimbol(Double simbol) {
		return scale(simbol);
	}
	
	public void processFile() throws FileNotFoundException, IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] fields = line.split(";");
				
				Integer step = Integer.valueOf(fields[STEP_FIELD]);
				String witness = fields[WITNESS_FIELD];
				String product = fields[PRODUCT_FIELD];
				String category = fields[CATEGORY_FIELD];
				Double rating = Double.valueOf(fields[RATING_FIELD]);
				
				if (rating > highestRating) {
					highestRating = rating;
				} else if (rating < lowestRating) {
					lowestRating = rating;
				}
				
				addToOpinions(step, witness, product, rating);
				addToRatingCount(product, rating);				
			}
		}
		empty = false;
	}
	
	private Double scale(Double simbol) {
		return Math.min(Math.max((simbol - lowestRating) / (highestRating - lowestRating), 0D), 1D);
	}

	private void addToOpinions(Integer step, String witness, String product, Double rating) {
		if (!opinionsByWitness.containsKey(witness)) {
			opinionsByWitness.put(witness, new LinkedList<>());
		}
		
		opinionsByWitness.get(witness).add(
			new Opinion(
				step, 
				product, 
				witness, 
				rating
			)
		);
	}
	
	private void addToRatingCount(String product, Double rating) {
		if (!productRatingsCount.containsKey(product)) {
			productRatingsCount.put(product, new TreeMap<>());
		}
		
		if (!productRatingsCount.get(product).containsKey(rating)) {
			productRatingsCount.get(product).put(rating, 1);
		} else {
			Integer count = productRatingsCount.get(product).get(rating) + 1;
			productRatingsCount.get(product).replace(rating, count);
		}
	}	
}
