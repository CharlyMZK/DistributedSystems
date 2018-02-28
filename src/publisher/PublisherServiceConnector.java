package src.publisher;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;

public class PublisherServiceConnector {
	private String user;
	private String password;
	private String host;
	private int port;
	private String destination;
	private Connection connection;
	private Session session;
	private MessageProducer producer;

	public PublisherServiceConnector(String[] destinationParams) {
		user = env("ACTIVEMQ_USER", "admin");
		password = env("ACTIVEMQ_PASSWORD", "password");
		host = env("ACTIVEMQ_HOST", "127.0.0.1");
		port = Integer.parseInt(env("ACTIVEMQ_PORT", "61616"));
		destination = arg(destinationParams, 0, "event");
	}


	public MessageProducer initConnexion() throws JMSException {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://" + host + ":" + port);
		connection = factory.createConnection(user, password);
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination dest = new ActiveMQTopic(destination);
		producer = session.createProducer(dest);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		return producer;
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


	public String getUser() {
		return user;
	}


	public void setUser(String user) {
		this.user = user;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getHost() {
		return host;
	}


	public void setHost(String host) {
		this.host = host;
	}


	public int getPort() {
		return port;
	}


	public void setPort(int port) {
		this.port = port;
	}


	public String getDestination() {
		return destination;
	}


	public void setDestination(String destination) {
		this.destination = destination;
	}


	public Connection getConnection() {
		return connection;
	}


	public void setConnection(Connection connection) {
		this.connection = connection;
	}


	public Session getSession() {
		return session;
	}


	public void setSession(Session session) {
		this.session = session;
	}


	public MessageProducer getProducer() {
		return producer;
	}


	public void setProducer(MessageProducer producer) {
		this.producer = producer;
	}
	
	
}
