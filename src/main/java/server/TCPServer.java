package main.java.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
	private static int port = 9999;
	private static String line;
	private static BufferedReader fromClient;
	private static DataOutputStream toClient;

	public static void main(String[] args) throws Exception {
		ServerSocket socket = new ServerSocket(port);

		while (true) {                            // Handle connection request
			Socket client = socket.accept(); // create communication socket
			System.out.println("Connection with: "+client.getRemoteSocketAddress());
			handleRequests(client);
		}
	}

	private static void handleRequests(Socket s) {
		try {
			fromClient = new BufferedReader(new InputStreamReader(s.getInputStream())); // Datastream FROM Client
			toClient = new DataOutputStream(s.getOutputStream()); // Datastream TO Client                  

			while (receiveRequest()) { // As long as connection exists
				sendResponse();
			}

			fromClient.close();
			toClient.close();
			s.close();

			System.out.println("Session ended, Server remains active");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private static boolean receiveRequest() throws IOException {
		boolean holdTheLine = true;

		System.out.println("Received: " + (line = fromClient.readLine()));
		if (line.equals(".")) { // End of session
			holdTheLine = false;
		}

		return holdTheLine;
	}

	private static void sendResponse() throws IOException {
		toClient.writeBytes(line.toUpperCase() + '\n');  // Send answer
	}
}
