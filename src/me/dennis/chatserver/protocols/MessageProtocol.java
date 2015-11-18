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
	
	public boolean receivedData() {
		if (from != null) {
			return true;
		}
		return false;
	}
	
	public String getFrom() {
		return from;
	}
	public String getMessage() {
		return message;
	}

	public void reset() {
		from = null;
		message = null;
	}
	
	public static String generate(String msg) {
		return "msg\tServer\t" + msg;
	}
	
	public static String generate(String from, String msg) {
		return "msg\t" + from + "\t" + msg;
	}
	
}
