package src.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileWriter;
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
	private static List<Request> requestsHistory;

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
					//Handle the message and send the answer
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
			// itterate on each request to fin the first match
			Iterator<Request> iterator = requests.iterator();
			while (iterator.hasNext()) 
			{
			   Request request = iterator.next(); // must be called before you can call i.remove()
			   if(request.match(currentRequest))
			   {
				   iterator.remove();
				   matchingRequestFound = true;
				   //Add the asks to the history
				   try 
				   {
					   //Write in log and in history
					   FileWriter writer = new FileWriter("log.csv",true);
					   if(request.getType() == Type.ASKS)
					   {
						   requestsHistory.add(request);
						   writer.append(request.toCsvString());
					   }   
					   else
					   {
						   requestsHistory.add(currentRequest);
						   writer.append(currentRequest.toCsvString());
					   }
					   writer.flush();
					   writer.close();
					   
				   }
				   catch(Exception ex)
				   {
					   ex.printStackTrace();
				   }


				   
				   break;
			   }
			}
		}
		Response response = new Response();
		if (matchingRequestFound)
			response.responseState = ResponseState.ACCEPTED;
		else
		{
			requests.add(currentRequest);
			response.responseState = ResponseState.TIMEOUT;
		}
		return response;
	}
	
	public static List<Request> getRequests() {
		if(requests == null)
			requests = Collections.synchronizedList(new ArrayList<Request>());
		
		return requests;
	}
	
	public static List<Request> getHistoryRequests() {
		if(requestsHistory == null)
			requestsHistory = Collections.synchronizedList(new ArrayList<Request>());
		
		return requestsHistory;
	}
}
