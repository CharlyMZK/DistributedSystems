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

class ImprovedTrader extends Trader{
	private boolean isCyclic;

	/**
	 * Constructor that take a boolean in parameter
	 * @param isCyclic True if the Trader is cyclic
	 */
	public ImprovedTrader(boolean isCyclic) {
		this.uId = generateUID();
		this.isCyclic = isCyclic;
		if(isCyclic)
			this.uId = this.uId + " | Cyclic";
		else
			this.uId = this.uId + " | Acyclic";
	}

	/**
	 * Run the trader actions
	 * 
	 * @throws JMSException
	 * @throws IOException
	 */
	public void trade() throws JMSException, IOException {
		boolean run = true;
		ListenerService improvedTraderListener = new ListenerService();
		Socket socket;
		BufferedReader fromServer;
		DataOutputStream toServer;
		
		// Broker connection
		socket = new Socket("localhost", 9999);
		toServer = new DataOutputStream(socket.getOutputStream()); // Datastream FROM Server
		fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Datastream TO Server
		// ActiveMQ connection
		MessageConsumer messageConsumer = improvedTraderListener.initConnexion();

		while(run) {	// Run until we have a shutdown message
			Message msg = messageConsumer.receive();
			if(msg instanceof  TextMessage) {
				String body = ((TextMessage) msg).getText();
				String[] news = body.split(" ");
				if("Good".equals(news[0])) { // Good news 
					run = treatGoodNews(news, fromServer, toServer);
				} else if ("Bad".equals(news[0])) { // Bad news
					run = treatBadNews(news, fromServer, toServer);
				} else if("SHUTDOWN".equals(body)) { // Shutdown message
					user.output("[Trader " + this.uId + "] quit");
					break;
				}
			} else {
				user.output("Journalist server broke down");
				break;
			}
		}
		
		if(!run) {
			user.output("Borker server broke down");
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
	 * @param fromServer Message from server
	 * @param toServer Message for server
	 * @return boolean true if the server is responding
	 * @throws IOException 
	 */
	private boolean treatGoodNews(String[] news, BufferedReader fromServer, DataOutputStream toServer) throws IOException {
		boolean run;
		
		user.output("[Trader " + this.uId + "] sending message");
		if(this.isCyclic)
			run = makeBuyRequest(news[news.length-1], toServer);
		else
			run = makeSellRequest(news[news.length-1], toServer);
		receiveResponse(fromServer);
		
		return run;
	}

	/**
	 * Treat the good news and display the result
	 * 
	 * @param news News to be used
	 * @param fromServer Message from server
	 * @param toServer Message for server
	 * @return boolean true if the server is responding
	 * @throws IOException 
	 */
	private boolean treatBadNews(String[] news, BufferedReader fromServer, DataOutputStream toServer) throws IOException {
		boolean run;
		
		user.output("[Trader " + this.uId + "] sending message");
		if(this.isCyclic)
			run = makeSellRequest(news[news.length-1], toServer);
		else
			run = makeBuyRequest(news[news.length-1], toServer);
		receiveResponse(fromServer);
		
		return run;
	}

	/**
	 * Make a buy request and send it to the server
	 * 
	 * @param stockName Name of the stock to buy
	 * @param toServer Message for server
	 * @return boolean true if the server is responding
	 */
	private boolean makeBuyRequest(String stockName, DataOutputStream toServer) {
		boolean run = true;
		
		StockName stock = StockName.valueOf(StockName.class, stockName);
		if(stock != null) {
			Request request = Request.generateRandomImprovedRequest(this.uId, stock, Type.BIDS);
			run = sendRequest(request, toServer);
		}
		
		return run;
	}

	/**
	 * Make a sell request and send it to the server
	 * 
	 * @param stockName Name of the stock to sell
	 * @param toServer Message for server
	 * @return boolean true if the server is responding
	 */
	private boolean makeSellRequest(String stockName, DataOutputStream toServer) {
		boolean run = true; 
		
		StockName stock = StockName.valueOf(StockName.class, stockName);
		if(stock != null) {
			Request request = Request.generateRandomImprovedRequest(this.uId, stock, Type.ASKS);
			run = sendRequest(request, toServer);
		}
		
		return run;
	}
}