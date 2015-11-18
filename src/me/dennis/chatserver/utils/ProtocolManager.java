package me.dennis.chatserver.utils;

import me.dennis.chatserver.protocols.ActionProtocol;
import me.dennis.chatserver.protocols.MessageProtocol;
import me.dennis.chatserver.types.Protocol;

public class ProtocolManager {
	
	public void parsePacket(String msg) {
		for (Enum p : Enum.values()) {
			try {
				String[] vals = msg.split("\t", 2);
				String protocol = vals[0];
				String data = vals[1];
				if (protocol.equals(p.label)) {
					p.protocol.runData(data);
				}
			} catch (Exception ex) {}
		}
	}
	
	public MessageProtocol getMessageProtocol() {
		for (Enum p : Enum.values()) {
			if (p.protocol instanceof MessageProtocol) {
				return (MessageProtocol) p.protocol;
			}
		}
		return null;
	}
	
	public ActionProtocol getActionProtocol() {
		for (Enum p : Enum.values()) {
			if (p.protocol instanceof ActionProtocol) {
				return (ActionProtocol) p.protocol;
			}
		}
		return null;
	}
	
	enum Enum {
		
		MESSAGE("msg", new MessageProtocol()),
		ACTION("action", new ActionProtocol());
		
		public String label;
		public Protocol protocol;
		
		private Enum(String label, Protocol protocol) {
			this.label = label;
			this.protocol = protocol;
		}
		
	}

}
