package src.main.java.client;

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

import src.main.java.messages.Request;

public class TCPClient {
  public String uId;
  static String line;
  static Socket socket;
  static BufferedReader fromServer;
  static DataOutputStream toServer;
  static UserInterface user = new UserInterface();
  
  public TCPClient() {
	uId = TCPClient.generateUID();
	System.out.println("UID : "+uId);
  }

  void connectToServer() throws UnknownHostException, IOException, InterruptedException {
	  socket = new Socket("localhost", 9999);
	    toServer = new DataOutputStream(     // Datastream FROM Server
	      socket.getOutputStream());
	    fromServer = new BufferedReader(     // Datastream TO Server
	      new InputStreamReader(socket.getInputStream()));
	    while (this.sendRequest()) {              // Send requests while connected
	    	Thread.sleep(5000);
	    	receiveResponse();                 // Process server's answer
	      }
	    
	    socket.close();
	    toServer.close();
	    fromServer.close();
	    System.out.println("Terminated");
  }
  
  
  void sendMessage() throws IOException {
	  while (this.sendRequest()) {              // Send requests while connected
	    	receiveResponse();                 // Process server's answer
	      }
  }
  
  private boolean sendRequest() throws IOException {
    boolean holdTheLine = true;          // Connection exists
    user.output(this.uId+" sending message \n");
    Request req = new Request();
    req.generateRandomRequest(uId);
    try {
		req.requestToJson();
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    toServer.writeByte(req.requestToJson().toString());
    //toServer.writeBytes("message \n");
    return true;
  }

  private static void receiveResponse() throws IOException {
    user.output("Server answers: " +
      new String(fromServer.readLine()) + '\n');
  }
  
  public static  String generateUID() {
	  UID id = null;
	  for (int idx=0; idx<10; ++idx){
	      id = new UID();
	  }
	  return id.toString();
  }

}
