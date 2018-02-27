package src.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;

import src.messages.*;

public class BrokerService extends Thread{
	private Socket client;
	private static List<Request> requests;
	public static List<Request> requestsHistory;

	BrokerService(Socket client){
		this.client = client;
		//We need a synchronized list because several thread can access the same list
		if(requests == null)
			requests = Collections.synchronizedList(new ArrayList<Request>());
		if(requestsHistory == null)
			requestsHistory = Collections.synchronizedList(new ArrayList<Request>());
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
		synchronized (requests)
		{
			Iterator<Request> iterator = requests.iterator();
			while (iterator.hasNext()) {
			   Request request = iterator.next(); // must be called before you can call i.remove()
			   if(request.match(currentRequest))
			   {
				   iterator.remove();
				   matchingRequestFound = true;
				   requestsHistory.add(currentRequest);
				   requestsHistory.add(request);
				   break;
			   }
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
