package me.dennis.chatserver.types;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import me.dennis.chatserver.core.Server;
import me.dennis.chatserver.protocols.MessageProtocol;
import me.dennis.chatserver.utils.Logger;

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
				String input = this.input.readUTF().trim();
				if (input != null && nickname == null) {
					boolean found = false;
					for (SocketThread st : Server.threads) {
						try {
							if (st.nickname.equalsIgnoreCase(input)) {
								found = true;
							}
						}
						catch (NullPointerException ex) {}
					}
					if (found) {
						sendMessage(MessageProtocol.generate("Server", "username"));
					}
					else {
						nickname = input;
						Server.broadcast(MessageProtocol.generate("Server", nickname + " joined the chat!"));
						sendMessage(MessageProtocol.generate("Server", "accept"));
					}
				}
				else if (input.equals("joined")) {
					joined = true;
					sendMessage(MessageProtocol.generate("Server", "Welcome to the chat " + nickname + "!"));
					String list = "";
					for (SocketThread thread : Server.threads) {
						list += ", " + thread.nickname;
					}
					sendMessage(MessageProtocol.generate("Server", "Users online: " + list.replaceFirst(", ", "")));
				}
				else {
					Server.broadcast(MessageProtocol.generate(nickname, input));
				}
			}
		}
		catch (SocketException ex) {
			Logger.info("Connection disconnected with: " + connection.getInetAddress().getHostAddress());
			Server.threads.remove(this);
			Server.broadcast(MessageProtocol.generate("Server", nickname + " left the chat!"));
			return;
		}
		catch (IOException ex) {
			ex.printStackTrace();
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
