package br.com.tm.repfogagent.trm;

import java.util.List;

import br.com.tm.repfogagent.trm.components.FIREBaseComponent;

public class FIREtrm {

	List<FIREBaseComponent> components;
	
	public FIREtrm() {
	
	}
	
	public FIREtrm(List<FIREBaseComponent> components) {
		this.components = components;
	}
	
	public double calculate() {
		double weight = 0;
		double denominatorWeight = 0;
		double totalValue = 0;
		for(FIREBaseComponent component : components) {
			weight = component.getCoefficient() * component.getCalculatedReliability();
			totalValue += weight * component.getCalculatedValue();
			denominatorWeight += weight;
		}
		
		if(totalValue == 0 && denominatorWeight == 0) {
			return 0;
		}
		
		return totalValue / denominatorWeight;
	}
	
	public double reliability() {
		double weight = 0;
		double denominatorCoefficients = 0;
		double totalValue = 0;
		for(FIREBaseComponent component : components) {
			weight = component.getCoefficient() * component.getCalculatedReliability();
			totalValue += weight * component.getCalculatedValue();
			denominatorCoefficients += component.getCoefficient();
		}
		
		if(totalValue == 0 && denominatorCoefficients == 0) {
			return 0;
		}
		
		return totalValue / denominatorCoefficients;
	}
	
}
