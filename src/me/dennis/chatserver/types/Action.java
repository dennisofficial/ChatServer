package me.dennis.chatserver.types;

public enum Action {

	USERNAME_VERIFY,
	USERNAME_DENY,
	USERNAME_ACCEPT;
	
	public static Action parseString(String string) {
		for (Action action : values()) {
			if (string.equals(action.name())) {
				return action;
			}
		}
		return null;
	}
	
}
