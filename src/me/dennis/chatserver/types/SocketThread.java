package me.dennis.chatserver.types;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import me.dennis.chatserver.core.Server;
import me.dennis.chatserver.protocols.ActionProtocol;
import me.dennis.chatserver.protocols.MessageProtocol;

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
			while (true) {
				// TURN PROCCOLS INTO OBJECT FOR EACH SOCKET THREAD
				Protocol.parsePacket(input.readUTF());
				if (MessageProtocol.receivedData() && joined) {
					Server.broadcast(MessageProtocol.generate(MessageProtocol.getFrom(), MessageProtocol.getMessage()));
				}
				if (ActionProtocol.receivedData()) {
					if (ActionProtocol.getAction().equals(Action.USERNAME_VERIFY)) {
						String verify = ActionProtocol.getData();
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
					if (ActionProtocol.getAction().equals(Action.JOINED)) {
						System.out.println("TEST");
						joined = true;
						Server.broadcast(MessageProtocol.generate(nickname + " joined the chat!"));
						sendMessage(MessageProtocol.generate("Welcome to the chat " + nickname + "!"));
						String list = "";
						for (SocketThread thread : Server.threads) {
							list += ", " + thread.nickname;
						}
						sendMessage(MessageProtocol.generate("Users online: " + list.replaceFirst(", ", "")));
					}
				}
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
