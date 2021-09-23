package br.com.tm.repfogagent.trm;

import java.io.Serializable;
import java.util.Date;

public class Rating implements Serializable {

	private static final long serialVersionUID = -193897021698198282L;
	
	private String nodeName;
	private String serverName;
	private double value;
	private int iteration;
	private String term;
	private Date date;
	private double normalizedValue;
	private double originalValue;
	
	private double packetLossPercentage;
	private long requiredLatency;
	private boolean satisfied;
	
	public Rating() {}
	
	public Rating(double value, String term, Date date) {
		this.value = value;
		this.term = term;
		this.date = date;
	}
	
	public Rating(String nodeName, String serverName, double value, int iteration, String term, Date date) {
		super();
		this.nodeName = nodeName;
		this.serverName = serverName;
		this.value = value;
		this.iteration = iteration;
		this.term = term;
		this.date = date;
		this.normalizedValue = value;
	}
	
	public Rating(String nodeName, String serverName, double value, double originalValue, int iteration, String term, Date date) {
		super();
		this.nodeName = nodeName;
		this.serverName = serverName;
		this.value = value;
		this.iteration = iteration;
		this.term = term;
		this.date = date;
		this.normalizedValue = value;
		this.originalValue = originalValue;
	}
	
	public Rating(String nodeName, String serverName, double value, double originalValue, int iteration, String term, double packetLossPercentage, long requiredLatency, Date date, boolean satisfied) {
		super();
		this.nodeName = nodeName;
		this.serverName = serverName;
		this.value = value;
		this.iteration = iteration;
		this.term = term;
		this.date = date;
		this.normalizedValue = value;
		this.originalValue = originalValue;
		this.packetLossPercentage = packetLossPercentage;
		this.requiredLatency = requiredLatency;
		this.satisfied = satisfied;
	}
	
	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public int getIteration() {
		return iteration;
	}
	public void setIteration(int iteration) {
		this.iteration = iteration;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getNormalizedValue() {
		return normalizedValue;
	}
	public void setNormalizedValue(double normalizedValue) {
		this.normalizedValue = normalizedValue;
	}
	public double getOriginalValue() {
		return originalValue;
	}
	public void setOriginalValue(double originalValue) {
		this.originalValue = originalValue;
	}
	public double getPacketLossPercentage() {
		return packetLossPercentage;
	}
	public void setPacketLossPercentage(double packetLossPercentage) {
		this.packetLossPercentage = packetLossPercentage;
	}
	public long getRequiredLatency() {
		return requiredLatency;
	}
	public void setRequiredLatency(long requiredLatency) {
		this.requiredLatency = requiredLatency;
	}
	public boolean isSatisfied() {
		return satisfied;
	}
	public void setSatisfied(boolean satisfied) {
		this.satisfied = satisfied;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + iteration;
		result = prime * result + ((nodeName == null) ? 0 : nodeName.hashCode());
		result = prime * result + ((serverName == null) ? 0 : serverName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rating other = (Rating) obj;
		if (iteration != other.iteration)
			return false;
		if (nodeName == null) {
			if (other.nodeName != null)
				return false;
		} else if (!nodeName.equals(other.nodeName))
			return false;
		if (serverName == null) {
			if (other.serverName != null)
				return false;
		} else if (!serverName.equals(other.serverName))
			return false;
		return true;
	}	

	@Override
	public String toString() {
		String json = "{";
		json += "nodeName: " + this.nodeName + ",";
		json += "serverName: " + this.serverName + ",";
		json += "value: " + this.value + ",";
		json += "iteration: " + this.iteration + ",";
		json += "term: '" + this.term + "',";
		json += "date: " + this.date.getTime() + ",";
		json += "normalizedValue: " + this.normalizedValue + ",";
		json += "originalValue: " + this.originalValue + ",";
		json += "packetLossPercentage: " + this.packetLossPercentage + ",";
		json += "requiredLatency: " + this.requiredLatency + ",";
		json += "satisfied: " + this.satisfied;
		json += "}";
		
		return json;
	}
}
