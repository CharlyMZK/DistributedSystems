package src.server;

import java.net.ServerSocket;
import java.net.Socket;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

public class MultithreadedTCPServer {
	private static int port = 9999;

	public static void main(String[] args) throws Exception{
		ServerSocket socket = new ServerSocket(port);

		System.out.println("Multithreaded Server starts on Port " + port);
		
		new PriceService();

		while (true){
			Socket client = socket.accept();
			System.out.println("Connection with: " + client.getRemoteSocketAddress());   // Output connection (Client) address
			new BrokerService(client).start();
		}
	}
}
