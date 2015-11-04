package me.dennis.chatserver.core;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class SocketThread implements Runnable {

	Socket connection;
	boolean open;

	public SocketThread(Socket connection) {
		this.connection = connection;
	}

	@Override
	public void run() {
		try {
			DataInputStream input = new DataInputStream(new BufferedInputStream(connection.getInputStream()));
			while (open) {
				Logger.info(input.readUTF());
			}
		} catch (SocketException ex) { 
			// Message for disconnect
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
