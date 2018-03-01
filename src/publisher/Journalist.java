package src.publisher;

import java.util.Timer;
import java.util.TimerTask;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import src.messages.StockName;

class Journalist {
	private Timer timer = new Timer(); // Timer used to time the requests send
	private int publishMessageInterval; // The interval between two publish
	private int publishingTimeInMs; // How long does the publisher will publish

	public static void main(String[] args) throws JMSException {
		// Connecting to publish service
		PublisherService publisherConnection = new PublisherService(args);
		publisherConnection.initConnexion();
		// Creating a publisher and makes him publish
		Journalist publisher = new Journalist(1000, 200000);
		publisher.startPublish(publisherConnection);
	}

	/**
	 * The publisher start publishing every "publishMessageInterval" for
	 * "publishTimeInMs" using the publisherConnection get in parameters
	 * 
	 * @param publisherConnection
	 */
	public void startPublish(PublisherService publisherConnection) {
		// Getting connection params
		Session session = publisherConnection.getSession();
		MessageProducer producer = publisherConnection.getProducer();
		Connection connection = publisherConnection.getConnection();

		// Scheduling a publish
		long startTime = System.currentTimeMillis();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					String body = getRandomNews();
					long executionTime = System.currentTimeMillis();
					long duration = (executionTime - startTime); // divide by 1000000 to get milliseconds.
					TextMessage msg = session.createTextMessage(body);
					producer.send(msg);
					System.out.println("Sending, " + body);

					if (duration > publishingTimeInMs) {
						timer.cancel();
						timer.purge();
						producer.send(session.createTextMessage("SHUTDOWN"));
						connection.close();
					}
				} catch (JMSException e) {
					System.out.println("Could not send message, closing session");
					timer.cancel();
					timer.purge();
				}
			}
		}, 100, publishMessageInterval);
	}

	/**
	 * Constructor, defines the publish times
	 * 
	 * @param publishMessageInterval
	 * @param publishingTimeInMs
	 */
	public Journalist(int publishMessageInterval, int publishingTimeInMs) {
		this.publishMessageInterval = publishMessageInterval;
		this.publishingTimeInMs = publishingTimeInMs;
	}

	/**
	 * Build a good news on a random type
	 * 
	 * @return a string
	 */
	private String makeGoodRandomNews() {
		return "Good news about " + StockName.randomType();
	}

	/**
	 * Build a bad news on a random type
	 * 
	 * @return a string
	 */
	private String makeBadRandomNews() {
		return "Bad news about " + StockName.randomType();
	}

	/**
	 * Build a random news
	 * 
	 * @return random news
	 */
	private String getRandomNews() {
		int randomInt = (Math.random() <= 0.5) ? 1 : 2;
		String news = "";
		if (randomInt == 1) {
			news = makeGoodRandomNews();
		} else {
			news = makeBadRandomNews();
		}
		return news;
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	public int getPublishMessageInterval() {
		return publishMessageInterval;
	}

	public void setPublishMessageInterval(int publishMessageInterval) {
		this.publishMessageInterval = publishMessageInterval;
	}

	public int getPublishingTimeInMs() {
		return publishingTimeInMs;
	}

	public void setPublishingTimeInMs(int publishingTimeInMs) {
		this.publishingTimeInMs = publishingTimeInMs;
	}

}