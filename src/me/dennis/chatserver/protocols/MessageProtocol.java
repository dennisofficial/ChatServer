package me.dennis.chatserver.protocols;

import me.dennis.chatserver.types.Protocol;

public class MessageProtocol extends Protocol {

	private static String from = null;
	private static String message = null;
	
	@Override
	public void runData(String data) {
		String[] vals = data.split("\t", 2);
		from = vals[0];
		message = vals[1];
	}
	
	public static boolean recievedData() {
		if (from != null) {
			return true;
		}
		return false;
	}
	
	public static String getFrom() {
		return from;
	}
	public static String getMessage() {
		return message;
	}

	public static void reset() {
		from = null;
		message = null;
	}
	
	public static String generate(String from, String msg) {
		return "msg\t" + from + "\t" + msg;
	}
	
}
