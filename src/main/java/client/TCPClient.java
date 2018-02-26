package src.main.java.client;



import java.io.*;
import java.net.*;

public class TCPClient {
	private static String host = "localhost";
	private static int port = 9999;
	private static String line;
	private static Socket socket;
	private static BufferedReader fromServer;
	private static DataOutputStream toServer;
	private static UserInterface user;

	public static void main(String[] args) throws Exception {
		socket = new Socket(host, port);
		fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Datastream TO Server
		toServer = new DataOutputStream(socket.getOutputStream()); // Datastream FROM Server
		user = new UserInterface();

		while (sendRequest()) { // Send requests while connected
			receiveResponse(); // Process server's answer
		}

		socket.close();
		toServer.close();
		fromServer.close();
	}

	private static boolean sendRequest() throws IOException {
		boolean holdTheLine = true; // Connection exists

		user.output("Enter message for the Server, or end the session with . : ");
		toServer.writeBytes((line = user.input()) + '\n');
		if (line.equals(".")) { // Does the user want to end the session?
			holdTheLine = false;
		}

		return holdTheLine;
	}

	private static void receiveResponse() throws IOException {
		user.output("Server answers: " + new String(fromServer.readLine()) + '\n');
	}
}
