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

class SmartTrader {
	private String uId;
	private boolean isCyclic;

	public SmartTrader(boolean isCyclic) {
		this.uId = Trader.generateUID();
		this.isCyclic = isCyclic;
		if(isCyclic) {
			this.uId = this.uId + " | Cyclic trader";
			System.out.println("Created a cyclic trader with uid : " + this.uId);
		} else {
			this.uId = this.uId + " | Acyclic trader";
			System.out.println("Created an acyclic trader with uid : " + this.uId);
		}

	}

	public void run() throws JMSException, IOException {
		String user = env("ACTIVEMQ_USER", "admin");
		String password = env("ACTIVEMQ_PASSWORD", "password");
		String host = env("ACTIVEMQ_HOST", "127.0.0.1");
		int port = Integer.parseInt(env("ACTIVEMQ_PORT", "61616"));

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
		Destination dest = new ActiveMQTopic("event");

		MessageConsumer consumer = session.createConsumer(dest);
		long start = System.currentTimeMillis();
		long count = 1;

		while(true) {
			Message msg = consumer.receive();
			if( msg instanceof  TextMessage ) {
				String body = ((TextMessage) msg).getText();
				String[] news = body.split(" ");
				if("Good".equals(news[0])) {
					userInterface.output("[ Trader " + this.uId + "] sending message");
					if(isCyclic)
						makeBuyRequest(news[news.length-1], toServer);
					else
						makeSellRequest(news[news.length-1], toServer);
					userInterface.output("Server answers: " + new String(fromServer.readLine()) + '\n');
				} else if ("Bad".equals(news[0])) {
					userInterface.output("[ Trader " + this.uId + "] sending message");
					if(isCyclic)
						makeSellRequest(news[news.length-1], toServer);
					else
						makeBuyRequest(news[news.length-1], toServer);
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

	private void makeBuyRequest(String stockName, DataOutputStream toServer) {
		StockName stock = StockName.valueOf(StockName.class, stockName);
		if(stock != null) {
			Request request = Request.generateRandomImprovedRequest(this.uId, stock, Type.BIDS);
			sendrequest(request, toServer);
		}
	}

	private void makeSellRequest(String stockName, DataOutputStream toServer) {
		StockName stock = StockName.valueOf(StockName.class, stockName);
		if(stock != null) {
			Request request = Request.generateRandomImprovedRequest(this.uId, stock, Type.BIDS);
			sendrequest(request, toServer);
		}
	}

	private static void sendrequest(Request request, DataOutputStream toServer) {
		String message;
		try {
			message = request.requestToJson().toString();
			System.out.println("Client message : " + message);
			toServer.writeBytes(message + " \n");	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}