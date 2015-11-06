package me.dennis.chatserver.core;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class SocketThread implements Runnable {

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
				String input = this.input.readUTF();
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
						sendMessage("msg\tserver\t\tdeny");
					}
					else {
						nickname = input;
						Server.broadcast(connection.getInetAddress().getHostAddress() + " is now known as " + nickname + "!");
					}
				}
				else {
					Server.broadcast(nickname + ": " + input);
				}
			}
		}
		catch (SocketException ex) {
			Logger.info("Connection disconnected with: " + connection.getInetAddress().getHostAddress());
			Server.threads.remove(this);
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
