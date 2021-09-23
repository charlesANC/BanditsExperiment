package br.com.tm.repfogagent.time;

import java.util.Date;

public class TimeParser {

	private static TimeParser instance;
	private static TimeConfiguration timeConfiguration;
	
	private TimeParser() {
	}
	
	public static synchronized TimeParser getInstance(TimeConfiguration tc) {
		if(instance == null) {
			instance = new TimeParser();
			timeConfiguration = tc;
		}
		
		return instance; 
	}
	
	public static int diff(Date init, Date end) {
		return (int)((end.getTime() - init.getTime()) / timeConfiguration.getIterationDefinition());
	}
	
}
