package br.com.tm.repfogagent.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.com.tm.repfogagent.trm.Rating;


public class Util {

	public static List<Rating> ordenarPorData(List<Rating> ratings, boolean asc) {
		if(ratings != null) {
			Collections.sort(ratings, new Comparator<Rating>() {
				public int compare(Rating o1, Rating o2) {
					return o1.getDate().compareTo(o2.getDate());
				}
			});
		}
		
		return ratings;
	}
	 
	public static Map<String, Double> sortByValue(Map<String, Double> map) {
	    List<Entry<String, Double>> list = new LinkedList<>(map.entrySet());
	    Collections.sort(list, new Comparator<Object>() {
	        @SuppressWarnings("unchecked")
	        public int compare(Object o1, Object o2) {
	            return (-1) * ((Comparable<Double>) ((Map.Entry<String, Double>) (o1)).getValue()).compareTo(((Map.Entry<String, Double>) (o2)).getValue());
	        }
	    });

	    Map<String, Double> result = new LinkedHashMap<>();
	    for (Iterator<Entry<String, Double>> it = list.iterator(); it.hasNext();) {
	        Map.Entry<String, Double> entry = (Map.Entry<String, Double>) it.next();
	        result.put(entry.getKey(), entry.getValue());
	    }

	    return result;
	}
	
	public static void main(String[] args) {
		Map<String, Double> mapa = new HashMap<>();
		mapa.put("b", 100.1);
		mapa.put("a", 100.2);
		mapa = Util.sortByValue(mapa);
		for(Entry<String,Double> e : mapa.entrySet()) {
			System.out.println(e.getKey() + " - " + e.getValue());
		}
	}
	
}
