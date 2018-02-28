/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package src.publisher;

import java.util.Timer;
import java.util.TimerTask;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import src.messages.StockName;

class Publisher {
	private Timer timer = new Timer(); // Timer used to time the requests send
	private int publishMessageInterval; // The interval between two publish
	private int publishingTimeInMs; // How long does the publisher will publish

	public static void main(String[] args) throws JMSException {
		// Connecting to publish service
		PublisherServiceConnector publisherConnection = new PublisherServiceConnector(args);
		publisherConnection.initConnexion();
		// Creating a publisher and makes him publish
		Publisher publisher = new Publisher(1000, 200000);
		publisher.startPublish(publisherConnection);
	}

	/**
	 * The publisher start publishing every "publishMessageInterval" for
	 * "publishTimeInMs" using the publisherConnection get in parameters
	 * 
	 * @param publisherConnection
	 */
	public void startPublish(PublisherServiceConnector publisherConnection) {
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
					TextMessage msg = session.createTextMessage();
					producer.send(msg);
					System.out.println("Sending, " + body);

					if (duration > publishingTimeInMs) {
						timer.cancel();
						timer.purge();
						producer.send(session.createTextMessage("SHUTDOWN"));
						connection.close();
					}
				} catch (JMSException e) {
					e.printStackTrace();
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
	public Publisher(int publishMessageInterval, int publishingTimeInMs) {
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

	/**
	 * Get an environment variable
	 * 
	 * @param key
	 * @param defaultValue
	 * @return env variable value
	 */
	private static String env(String key, String defaultValue) {
		String rc = System.getenv(key);
		if (rc == null)
			return defaultValue;
		return rc;
	}

	/**
	 * Get args
	 * 
	 * @param args
	 * @param index
	 * @param defaultValue
	 * @return arg get
	 */
	private static String arg(String[] args, int index, String defaultValue) {
		if (index < args.length)
			return args[index];
		else
			return defaultValue;
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