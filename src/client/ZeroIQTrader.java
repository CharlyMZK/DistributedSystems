package src.client;

import java.io.*;
import java.net.*;

import javax.jms.JMSException;

import src.messages.Request;

public class ZeroIQTrader extends Trader {
	/**
	 * ZeroIQTrader constructor
	 */
	public ZeroIQTrader() {
		this.uId = generateUID() + " | ZeroIQ";
		System.out.println("Created a trader with uid : " + uId);
	}

	/**
	 * Connect the client to server and start sending requests
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void run() throws JMSException, IOException {
		Socket socket;
		BufferedReader fromServer;
		DataOutputStream toServer;
		
		socket = new Socket("localhost", 9999);
		toServer = new DataOutputStream(socket.getOutputStream()); // Datastream FROM Server
		fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Datastream TO Server
		
		while (this.generateAndsendRandomRequest(toServer)) { // Send requests while connected
			receiveResponse(fromServer); // Process server's answer
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
	 * @param toServer message to the server
	 * @return boolean
	 * @throws IOException
	 */
	private boolean generateAndsendRandomRequest(DataOutputStream toServer) throws IOException {
		user.output("[Trader " +this.uId + "] sending message");
		Request req = new Request();
		req = req.generateRandomRequest(uId);
		sendRequest(req, toServer);
		return true;
	}
}
