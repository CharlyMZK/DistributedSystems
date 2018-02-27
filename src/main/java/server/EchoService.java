package src.main.java.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;

import src.main.java.messages.*;

public class EchoService extends Thread{
	private Socket client;
	private ArrayList<Request> requests;

	EchoService(Socket client){
		this.client = client;
		requests = new ArrayList<Request>();
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
				} 
				else 
				{
					Boolean matchingRequestFound = false;
					Request currentRequest = Request.jsonToRequest(line);
					Iterator<Request> iterator = requests.iterator();
					while (iterator.hasNext()) {
					   Request request = iterator.next(); // must be called before you can call i.remove()
					   if(request.equals(currentRequest))
					   {
						   iterator.remove();
						   matchingRequestFound = true;
						   break;
					   }
					}
					Response response = new Response();
					if (matchingRequestFound)
						response.responseState = ResponseState.ACCEPTED;
					else
					{
						//Answer no
						requests.add(currentRequest);
						response.responseState = ResponseState.TIMEOUT;
					}
					
					toClient.writeBytes(response.responseToJson() +'\n'); // Response
				}	
			}
			fromClient.close(); toClient.close(); client.close(); // End
			
			System.out.println("Thread ended: " + this);
		}catch (Exception e){
			System.out.println(e);
		}
	}
}
