package src.client;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import src.messages.Request;
import src.messages.StockName;
import src.messages.Type;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.jms.*;

class cyclicTrader {
	private static String uId;
	
	public cyclicTrader() {
		uId = Trader.generateUID();
		System.out.println("Created a trader with uid : " + uId);
	}

	public static void main(String []args) throws JMSException, IOException {
		String user = env("ACTIVEMQ_USER", "admin");
		String password = env("ACTIVEMQ_PASSWORD", "password");
		String host = env("ACTIVEMQ_HOST", "127.0.0.1");
		int port = Integer.parseInt(env("ACTIVEMQ_PORT", "61616"));
		String destination = arg(args, 0, "event");

		Socket socket;
		BufferedReader fromServer;
		DataOutputStream toServer;
		UserInterface userInterface = new UserInterface();
		
		socket = new Socket(host, 9999);
		toServer = new DataOutputStream(socket.getOutputStream()); // Datastream FROM Server
		fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Datastream TO Server

		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://" + host + ":" + port);

		Connection connection = factory.createConnection(user, password);
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination dest = new ActiveMQTopic(destination);
		
		MessageConsumer consumer = session.createConsumer(dest);
		long start = System.currentTimeMillis();
		long count = 1;
		System.out.println("Waiting for news...");
		while(true) {
			Message msg = consumer.receive();
			if( msg instanceof  TextMessage ) {
				String body = ((TextMessage) msg).getText();
				String[] news = body.split(" ");
				if("Good".equals(news[0])) {
					makeBuyRequest(news[news.length-1], toServer);
					userInterface.output("Server answers: " + new String(fromServer.readLine()) + '\n');
				} else if ("Bad".equals(news[0])) {
					makeSellRequest(news[news.length-1], toServer);
					userInterface.output("Server answers: " + new String(fromServer.readLine()) + '\n');
				} else if("SHUTDOWN".equals(body)) {
					long diff = System.currentTimeMillis() - start;
					System.out.println(String.format("Received %d in %.2f seconds", count, (1.0*diff/1000.0)));
					break;
				}
			} else {
				System.out.println("Unexpected message type: "+msg.getClass());
			}
		}
		connection.close();
		socket.close();
		toServer.close();
		fromServer.close();
	}

	private static String env(String key, String defaultValue) {
		String rc = System.getenv(key);
		if(rc== null)
			return defaultValue;
		return rc;
	}

	private static String arg(String []args, int index, String defaultValue) {
		if(index < args.length)
			return args[index];
		else
			return defaultValue;
	}
	
	private static void makeBuyRequest(String stockName, DataOutputStream toServer) {
		StockName stock = StockName.valueOf(StockName.class, stockName);
		if(stock != null) {
			Request request = Request.generateRandomImprovedRequest(uId, stock, Type.BIDS);
			sendrequest(request, toServer);
			System.out.println("Buying " + stockName);
		}
	}
	
	private static void makeSellRequest(String stockName, DataOutputStream toServer) {
		StockName stock = StockName.valueOf(StockName.class, stockName);
		if(stock != null) {
			Request request = Request.generateRandomImprovedRequest(uId, stock, Type.BIDS);
			sendrequest(request, toServer);
			System.out.println("Selling " + stockName);
		}
	}
	
	private static void sendrequest(Request request, DataOutputStream toServer) {
		String message;
		try {
			message = request.requestToJson().toString();
			toServer.writeBytes(message + " \n");	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}