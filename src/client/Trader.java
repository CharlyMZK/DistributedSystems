package src.client;

import java.io.*;
import java.rmi.server.UID;

import javax.jms.JMSException;

import src.messages.Request;

public abstract class Trader {
	protected String uId;  									 // Unique client descriptor
	protected static UserInterface user = new UserInterface();  // User information displayer ( input & output ) 
	
	/**
	 * Function run allowing user to connect to server and make requests
	 */
	protected abstract void trade() throws JMSException, IOException;
	
	/**
	 * Send the request to the server
	 * 
	 * @param request Request to be send
	 * @param toServer the server
	 */
	protected boolean sendRequest(Request request, DataOutputStream toServer) {
		String message;
		try {
			message = request.requestToJson().toString();
			System.out.println("Client message : " + message);
			toServer.writeBytes(message + " \n");	
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Display the received response
	 * 
	 * @param fromServer message from the server
	 * @throws IOException
	 */
	protected void receiveResponse(BufferedReader fromServer) throws IOException {
		user.output("Server answers: " + new String(fromServer.readLine()) + '\n');
	}

	/**
	 * Generate a unique UID
	 * 
	 * @return String
	 */
	protected String generateUID() {
		UID id = null;
		for (int idx = 0; idx < 10; ++idx) {
			id = new UID();
		}
		return id.toString();
	}

}
