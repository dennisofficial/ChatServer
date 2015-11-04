package me.dennis.chatserver.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

	public ServerSocket server;
	
	public static List<SocketThread> threads = new ArrayList<SocketThread>();
	
	public static void main(String[] args) {
		new Server();
	}
	
	public Server() {
		Logger.info("Programing initializing...");
		try {
			Logger.info("Binding server to port 8231, and IP: " + InetAddress.getLocalHost().getHostAddress());
			server = new ServerSocket(8231, 100, InetAddress.getLocalHost());
		} catch (IOException e) {
			Logger.err("Server could not bind to port 8231!");
			Logger.err("Program terminated!");
			return;
		}
		Logger.info("Server Started!");
		while (true) {
			Logger.info("Waiting for connection...");
			try {
				Socket connection = server.accept();
				Logger.info("Connection made with: " + connection.getInetAddress().getHostAddress());
				SocketThread st = new SocketThread(connection);
				threads.add(st);
				Thread t = new Thread(st);
				t.start();
			} catch (IOException e) {
				Logger.err("Connection to client could not be made!");
			}
		}
	}
	
	public static void broadcast(String str) {
		for (SocketThread thread : Server.threads) {
			thread.sendMessage(str);
			Logger.info(str);
		}
	}
	
}
