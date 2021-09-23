package br.com.tm.repfogagent.util;

public class DateUtil {

	public static long timeToHour(long time) {
		return time / (1000 * 60 * 60);
	}
	
	public static long timeToDay(long time) {
		return timeToHour(time) / 24;
	}
}
