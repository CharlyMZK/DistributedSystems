package main.java.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;
public class EchoService extends Thread{
	private Socket client;

	EchoService(Socket client){
		this.client = client;
	}

	@Override
	public void run (){
		String line;
		BufferedReader fromClient;
		DataOutputStream toClient;
		boolean run = true;

		System.out.println("Thread started: " + this); // Display Thread-ID

		try{
			fromClient = new BufferedReader(new InputStreamReader(client.getInputStream())); // Datastream FROM Client
			toClient = new DataOutputStream (client.getOutputStream()); // TO Client
			while(run){     // repeat as long as connection exists
				line = fromClient.readLine();              // Read Request
				System.out.println("Received: " + line);
				if (line.equals(".")) {
					run = false;   // Break connection
				} else {
					toClient.writeBytes(line.toUpperCase()+'\n'); // Response
				}	
			}
			fromClient.close(); toClient.close(); client.close(); // End
			
			System.out.println("Thread ended: " + this);
		}catch (Exception e){
			System.out.println(e);
		}
	}
}
