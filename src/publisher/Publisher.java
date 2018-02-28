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
	private Timer timer = new Timer();
	private int publishMessageInterval;
	private int publishingTimeInMs;

	public static void main(String[] args) throws JMSException {
		PublisherServiceConnector publisherConnection = new PublisherServiceConnector(args);
		publisherConnection.initConnexion();
		Publisher publisher = new Publisher(1000,200000);
		publisher.startPublish(publisherConnection);
	}

	public void startPublish(PublisherServiceConnector publisherConnection) {
		Session session = publisherConnection.getSession();
		MessageProducer producer = publisherConnection.getProducer();
		Connection connection = publisherConnection.getConnection();
		
		long startTime = System.currentTimeMillis();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					int randomInt = (Math.random() <= 0.5) ? 1 : 2;
					String body = "";
					if (randomInt == 1) {
						body = makeGoodRandomNews();
					} else {
						body = makeBadRandomNews();
					}
					TextMessage msg = session.createTextMessage(body);
					producer.send(msg);
					System.out.println("Sending, " + body);
				} catch (JMSException e) {
					e.printStackTrace();
				}

				long executionTime = System.currentTimeMillis();
				long duration = (executionTime - startTime); // divide by 1000000 to get milliseconds.

				if (duration > publishingTimeInMs) {
					timer.cancel();
					timer.purge();
					try {
						producer.send(session.createTextMessage("SHUTDOWN"));
					} catch (JMSException e) {
						e.printStackTrace();
					}
					try {
						connection.close();
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			}
		}, 100, publishMessageInterval);
	}
	
	
	public Publisher(int publishMessageInterval, int publishingTimeInMs) {
		this.publishMessageInterval = publishMessageInterval;
		this.publishingTimeInMs = publishingTimeInMs;
	}

	private static String makeGoodRandomNews() {
		return "Good news about " + StockName.randomType();
	}

	private static String makeBadRandomNews() {
		return "Bad news about " + StockName.randomType();
	}

	private static String env(String key, String defaultValue) {
		String rc = System.getenv(key);
		if (rc == null)
			return defaultValue;
		return rc;
	}

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