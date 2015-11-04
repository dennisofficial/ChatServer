package me.dennis.chatserver.core;

import static java.util.Calendar.HOUR;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

import java.util.Calendar;

public class Logger {

	private static Calendar cal;
	
	private static String getDate() {
		cal = Calendar.getInstance();
		return "[" + String.format("%02d",cal.get(HOUR)) + ":"
				+ String.format("%02d",cal.get(MINUTE)) + ":"
				+ String.format("%02d",cal.get(SECOND)) + "]";
	}
	
	public static void info(String s) {
		System.out.println(getDate() + "[INFO] " + s);
	}
	
	public static void err(String s) {
		System.err.println(getDate() + "[SEVERE] " + s);
	}
	
}
