package src.client;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;

public class ImprovedTraderListener {
	private String user;
	private String password;
	private String host;
	private int port;
	private Destination destination;
	private Connection connection;
	private Session session;

	/**
	 * ImprovedTraderListener constructor
	 */
	public ImprovedTraderListener() {
		user = env("ACTIVEMQ_USER", "admin");
		password = env("ACTIVEMQ_PASSWORD", "password");
		host = env("ACTIVEMQ_HOST", "127.0.0.1");
		port = Integer.parseInt(env("ACTIVEMQ_PORT", "61616"));
	}

	/**
	 * Init connection to ActiveMQ
	 * @return MessageConsumer
	 * @throws JMSException
	 */
	public MessageConsumer initConnexion() throws JMSException {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://" + host + ":" + port);
		connection = factory.createConnection(user, password);
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		destination = new ActiveMQTopic("event");
		return session.createConsumer(destination);
	}
	
	/**
	 * Get the environment variable and if it don't exist, take the default one
	 * 
	 * @param key The key
	 * @param defaultValue Default value to be used
	 * @return String containing the value
	 */
	private static String env(String key, String defaultValue) {
		String rc = System.getenv(key);
		if (rc == null)
			return defaultValue;
		return rc;
	}
	
	public Connection getConnection() {
		return connection;
	}
}
