package server;

import java.net.*;

public class MultithreadedTCPServer {
	private static int port = 9999;

	public static void main(String[] args) throws Exception{
		ServerSocket socket = new ServerSocket(port);

		System.out.println("Multithreaded Server starts on Port " + port);

		while (true){
			Socket client = socket.accept();
			System.out.println("Connection with: " + client.getRemoteSocketAddress());   // Output connection (Client) address
			new EchoService(client).start();
		}
	}
}
