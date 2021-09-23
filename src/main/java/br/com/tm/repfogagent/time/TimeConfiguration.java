package br.com.tm.repfogagent.time;

public class TimeConfiguration {

	private long second; 
	private long minute;
	private long hour;
	private long day;
	
	private long iterationDefinition;
	
	public long getSecond() {
		return second;
	}
	public void setSecond(long second) {
		this.second = second;
	}
	public long getMinute() {
		return minute;
	}
	public void setMinute(long minute) {
		this.minute = minute;
	}
	public long getHour() {
		return hour;
	}
	public void setHour(long hour) {
		this.hour = hour;
	}
	public long getDay() {
		return day;
	}
	public void setDay(long day) {
		this.day = day;
	}
	public long getIterationDefinition() {
		return iterationDefinition;
	}
	public void setIterationDefinition(long iterationDefinition) {
		this.iterationDefinition = iterationDefinition;
	} 
	
}
