package me.dennis.chatserver.types;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import me.dennis.chatserver.core.Server;
import me.dennis.chatserver.protocols.ActionProtocol;
import me.dennis.chatserver.protocols.MessageProtocol;
import me.dennis.chatserver.utils.ProtocolManager;

public class SocketThread implements Runnable {

	boolean joined = false;
	Socket connection;
	DataInputStream input;
	DataOutputStream output;
	String nickname = null;

	public SocketThread(Socket connection) {
		this.connection = connection;
		try {
			input = new DataInputStream(new BufferedInputStream(connection.getInputStream()));
			output = new DataOutputStream(connection.getOutputStream());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			ProtocolManager pm = new ProtocolManager();
			MessageProtocol mp = pm.getMessageProtocol();
			ActionProtocol ap = pm.getActionProtocol();
			while (true) {
				pm.parsePacket(input.readUTF());
				if (mp.receivedData() && joined) {
					Server.broadcast(MessageProtocol.generate(mp.getFrom(), mp.getMessage()));
				}
				if (ap.receivedData()) {
					if (ap.getAction().equals(Action.USERNAME_VERIFY)) {
						String verify = ap.getData();
						boolean accepted = true;
						for (SocketThread thread : Server.threads) {
							if (thread.joined) {
								if (verify.equalsIgnoreCase(thread.nickname)) {
									accepted = false;
								}
							}
						}
						if (accepted) {
							sendMessage(ActionProtocol.generateString(Action.USERNAME_ACCEPT, null));
							nickname = verify;
						}
						else {
							sendMessage(ActionProtocol.generateString(Action.USERNAME_DENY, null));
						}
					}
					if (ap.getAction().equals(Action.JOINED)) {
						joined = true;
						sendMessage(ActionProtocol.generateString(Action.JOINED, null));
						Server.broadcast(MessageProtocol.generate(nickname + " joined the chat!"));
						sendMessage(MessageProtocol.generate("Welcome to the chat " + nickname + "!"));
						String list = "";
						for (SocketThread thread : Server.threads) {
							list += ", " + thread.nickname;
						}
						sendMessage(MessageProtocol.generate("Users online: " + list.replaceFirst(", ", "")));
					}
				}
				mp.reset();
				ap.reset();
			}	
		} catch (SocketException ex) {
			Server.threads.remove(this);
			Server.broadcast(MessageProtocol.generate(nickname + " has left the chat!"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String str) {
		try {
			output.writeUTF(str);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
