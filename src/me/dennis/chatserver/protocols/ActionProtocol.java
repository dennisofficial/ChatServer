package me.dennis.chatserver.protocols;

import me.dennis.chatserver.types.Action;
import me.dennis.chatserver.types.Protocol;

public class ActionProtocol extends Protocol {

	private static String from;
	private static Action action;
	private static String data;
	
	@Override
	public void runData(String data) {
		String[] vals = data.split("\t", 3);
		from = vals[0];
		action = Action.parseString(vals[1]);
		ActionProtocol.data = vals[2];
	}
	
	public boolean receivedData() {
		if (action != null) {
			return true;
		}
		return false;
	}
	
	public String getFrom() {
		return from;
	}
	
	public Action getAction() {
		return action;
	}
	
	public String getData() {
		return data;
	}

	public void reset() {
		from = null;
		action = null;
		data = null;
	}
	
	public static String generateString(Action action, String data) {
		return "action\tServer\t" + action.name() + "\t" + data;
	}
	
}
