package src.client;

import java.io.*;
import java.net.*;
import java.rmi.server.UID;

import org.json.JSONException;

import src.messages.Request;

public class Trader {
	private String uId;  									 // Unique client descriptor
	public static UserInterface user = new UserInterface();  // User information displayer ( input & output ) 
	public static Socket socket;
	public static BufferedReader fromServer;
	public static DataOutputStream toServer;
	

	/**
	 * TCPClient constructor
	 */
	public Trader() {
		uId = Trader.generateUID();
		System.out.println("Created a trader with uid : " + uId);
	}

	/**
	 * Connect the client to server and start sending requests
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void connectToServerAndSendRequests() throws UnknownHostException, IOException, InterruptedException {
		socket = new Socket("localhost", 9999);
		toServer = new DataOutputStream(socket.getOutputStream()); // Datastream FROM Server
		fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Datastream TO Server
		
		while (this.generateAndsendRandomRequest()) { // Send requests while connected
			receiveResponse(); // Process server's answer
			Thread.sleep(5000);
		}

		// Closing socket and server
		socket.close();
		toServer.close();
		fromServer.close();
		System.out.println("Terminated");
	}

	/**
	 * Generate a random JSON request and send it to server
	 * 
	 * @return boolean
	 * @throws IOException
	 */
	private boolean generateAndsendRandomRequest() throws IOException {
		user.output("[Trader " +this.uId + "] sending message");
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

	/**
	 * Display the received response
	 * 
	 * @throws IOException
	 */
	private static void receiveResponse() throws IOException {
		user.output("Server answers: " + new String(fromServer.readLine()) + '\n');
	}

	/**
	 * Generate a unique UID
	 * 
	 * @return String
	 */
	public static String generateUID() {
		UID id = null;
		for (int idx = 0; idx < 10; ++idx) {
			id = new UID();
		}
		return id.toString();
	}

}
