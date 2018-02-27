package src.client;

/*
 * 22. 10. 10
 */

/**
 *
 * @author Peter Altenberd
 * (Translated into English by Ronald Moore)
 * Computer Science Dept.                   Fachbereich Informatik
 * Darmstadt Univ. of Applied Sciences      Hochschule Darmstadt
 */

import java.io.*;
import java.net.*;
import java.rmi.server.UID;

import org.json.JSONException;

import src.messages.Request;

public class TCPClient {
	private String uId;
	public static Socket socket;
	public static BufferedReader fromServer;
	public static DataOutputStream toServer;
	public static UserInterface user = new UserInterface();

	public TCPClient() {
		uId = TCPClient.generateUID();
		System.out.println("Created a trader with uid : " + uId);
	}

	public void connectToServerAndSendRequests() throws UnknownHostException, IOException, InterruptedException {
		socket = new Socket("localhost", 9999);
		toServer = new DataOutputStream( // Datastream FROM Server
				socket.getOutputStream());
		fromServer = new BufferedReader( // Datastream TO Server
				new InputStreamReader(socket.getInputStream()));
		while (this.sendRequest()) { // Send requests while connected
			Thread.sleep(5000);
			receiveResponse(); // Process server's answer
		}

		socket.close();
		toServer.close();
		fromServer.close();
		System.out.println("Terminated");
	}

	private boolean sendRequest() throws IOException {
		user.output("[ Trader " +this.uId + "] sending message \n");
		String message = "";
		Request req = new Request();
		req = req.generateRandomRequest(uId);
		try {
			message = req.requestToJson().toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		System.out.println("Client message : "+message);
			toServer.writeBytes(message + " \n");
		
		return true;
	}

	
	private static void receiveResponse() throws IOException {
		user.output("Server answers: " + new String(fromServer.readLine()) + '\n');
	}

	public static String generateUID() {
		UID id = null;
		for (int idx = 0; idx < 10; ++idx) {
			id = new UID();
		}
		return id.toString();
	}

}
