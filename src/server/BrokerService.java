package src.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;

import src.messages.*;

public class BrokerService extends Thread{
	private Socket client;
	private ArrayList<Request> requests;

	BrokerService(Socket client){
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
					Response response = handleMessage(line);
					toClient.writeBytes(response.responseToJson() +'\n'); // Response
				}	
			}
			fromClient.close(); toClient.close(); client.close(); // End
			
			System.out.println("Thread ended: " + this);
		}catch (Exception e){
			System.out.println(e);
		}
	}
	
	private Response handleMessage(String line) throws JSONException
	{
		Boolean matchingRequestFound = false;
		Request currentRequest = Request.jsonToRequest(line);
		Iterator<Request> iterator = requests.iterator();
		while (iterator.hasNext()) {
		   Request request = iterator.next(); // must be called before you can call i.remove()
		   if(request.match(currentRequest))
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
		return response;
	}
}
