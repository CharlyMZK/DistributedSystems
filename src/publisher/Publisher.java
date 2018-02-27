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

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;

import src.messages.StockName;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.jms.*;

class Publisher {
	public static Timer timer = new Timer();
	public static int publishMessageInterval = 1000;
	public static int publishingTimeInMs = 20000;

	public static void main(String[] args) throws JMSException {

		String user = env("ACTIVEMQ_USER", "admin");
		String password = env("ACTIVEMQ_PASSWORD", "password");
		String host = env("ACTIVEMQ_HOST", "localhost");
		int port = Integer.parseInt(env("ACTIVEMQ_PORT", "61616"));
		String destination = arg(args, 0, "event");

		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://" + host + ":" + port);

		Connection connection = factory.createConnection(user, password);
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination dest = new ActiveMQTopic(destination);
		MessageProducer producer = session.createProducer(dest);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
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
					msg.setIntProperty("id", 1);
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

	private static String makeGoodRandomNews() {
		return "Good news about " + StockName.randomType();
	}

	private static String makeBadRandomNews() {
		return "Good bad about " + StockName.randomType();
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

}