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

  static void connectToServer() throws UnknownHostException, IOException {
	  socket = new Socket("localhost", 9999);
	    toServer = new DataOutputStream(     // Datastream FROM Server
	      socket.getOutputStream());
	    fromServer = new BufferedReader(     // Datastream TO Server
	      new InputStreamReader(socket.getInputStream()));
	    sendRequest();             // Send requests while connected
	    receiveResponse();                 // Process server's answer
	    socket.close();
	    toServer.close();
	    fromServer.close();
	    System.out.println("Terminated");
  }
  
  
  private static boolean sendRequest() throws IOException {
    boolean holdTheLine = true;          // Connection exists
    user.output("Sending message");
    toServer.writeBytes("message \n");
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
