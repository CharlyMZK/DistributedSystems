package src.client;

import src.messages.Request;
import src.messages.StockName;
import src.messages.Type;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.jms.*;

class ImprovedTrader {
	private String uId;
	private boolean isCyclic;

	/**
	 * Constructor that take a boolean in parameter
	 * @param isCyclic True if the Trader is cyclic
	 */
	public ImprovedTrader(boolean isCyclic) {
		this.uId = Trader.generateUID();
		this.isCyclic = isCyclic;
		if(isCyclic) {
			this.uId = this.uId + " | Cyclic";
			System.out.println("Created a cyclic trader with uid : " + this.uId);
		} else {
			this.uId = this.uId + " | Acyclic";
			System.out.println("Created an acyclic trader with uid : " + this.uId);
		}

	}

	/**
	 * Run the trader actions
	 * 
	 * @throws JMSException
	 * @throws IOException
	 */
	public void run() throws JMSException, IOException {
		ImprovedTraderListener improvedTraderListener = new ImprovedTraderListener();
		UserInterface userInterface = new UserInterface();
		Socket socket;
		BufferedReader fromServer;
		DataOutputStream toServer;
		
		// Broker connection
		socket = new Socket("localhost", 9999);
		toServer = new DataOutputStream(socket.getOutputStream()); // Datastream FROM Server
		fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Datastream TO Server
		// ActiveMQ connection
		MessageConsumer messageConsumer = improvedTraderListener.initConnexion();

		while(true) {	// Run until we have a shutdown message
			Message msg = messageConsumer.receive();
			if(msg instanceof  TextMessage) {
				String body = ((TextMessage) msg).getText();
				String[] news = body.split(" ");
				if("Good".equals(news[0])) { // Good news 
					treatGoodNews(news, userInterface, fromServer, toServer);
				} else if ("Bad".equals(news[0])) { // Bad news
					treatBadNews(news, userInterface, fromServer, toServer);
				} else if("SHUTDOWN".equals(body)) { // Shutdown message
					userInterface.output("[Trader " + this.uId + "] quit");
					break;
				}
			} else {
				System.out.println("Unexpected message type: " + msg.getClass());
			}
		}

		// Closing all connections
		improvedTraderListener.getConnection().close();
		socket.close();
		toServer.close();
		fromServer.close();
	}

	/**
	 * Treat the good news and display the result
	 * 
	 * @param news News to be used
	 * @param userInterface User interface
	 * @param fromServer Message from server
	 * @param toServer Message for server
	 * @throws IOException 
	 */
	private void treatGoodNews(String[] news, UserInterface userInterface, BufferedReader fromServer, DataOutputStream toServer) throws IOException {
		userInterface.output("[Trader " + this.uId + "] sending message");
		if(this.isCyclic)
			makeBuyRequest(news[news.length-1], toServer);
		else
			makeSellRequest(news[news.length-1], toServer);
		userInterface.output("Server answers: " + new String(fromServer.readLine()) + '\n');
	}

	/**
	 * Treat the good news and display the result
	 * 
	 * @param news News to be used
	 * @param userInterface User interface
	 * @param fromServer Message from server
	 * @param toServer Message for server
	 * @throws IOException 
	 */
	private void treatBadNews(String[] news, UserInterface userInterface, BufferedReader fromServer, DataOutputStream toServer) throws IOException {
		userInterface.output("[Trader " + this.uId + "] sending message");
		if(this.isCyclic)
			makeSellRequest(news[news.length-1], toServer);
		else
			makeBuyRequest(news[news.length-1], toServer);
		userInterface.output("Server answers: " + new String(fromServer.readLine()) + '\n');
	}

	/**
	 * Make a buy request and send it to the server
	 * 
	 * @param stockName Name of the stock to buy
	 * @param toServer Message for server
	 */
	private void makeBuyRequest(String stockName, DataOutputStream toServer) {
		StockName stock = StockName.valueOf(StockName.class, stockName);
		if(stock != null) {
			Request request = Request.generateRandomImprovedRequest(this.uId, stock, Type.BIDS);
			sendrequest(request, toServer);
		}
	}

	/**
	 * Make a sell request and send it to the server
	 * 
	 * @param stockName Name of the stock to sell
	 * @param toServer Message for server
	 */
	private void makeSellRequest(String stockName, DataOutputStream toServer) {
		StockName stock = StockName.valueOf(StockName.class, stockName);
		if(stock != null) {
			Request request = Request.generateRandomImprovedRequest(this.uId, stock, Type.ASKS);
			sendrequest(request, toServer);
		}
	}

	/**
	 * Send the request to the server
	 * 
	 * @param request Request to be send
	 * @param toServer the server
	 */
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