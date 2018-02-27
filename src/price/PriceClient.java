package src.price;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;


public class PriceClient {

	private static String host = "127.0.0.1";
	private static int port = 8080;
	private static int size = 4;
	private static int offset = 0;

	public static void main(String[] args) throws Exception {

		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();

		config.setServerURL(new URL("http://" + host + ":" + port + "/xmlrpc"));
		XmlRpcClient client = new XmlRpcClient();
		client.setConfig(config);

		Object[] params = new Object[]{new Integer(offset), new Integer(size)};
		System.out.println("About to get stock history from " + params[0] 
				+ " with size " + params[1] + "." );

		List<String> result = decodeList( client.execute("Price.history", params));
		printHistory(result);
		
		while(result.size() == size) {
			offset += size;
			params = new Object[]{new Integer(offset), new Integer(size)};
			System.out.println("About to get stock history from " + params[0] 
					+ " with size " + params[1] + "." );
			result = decodeList( client.execute("Price.history", params));
			if(result.size() > 0) {
				printHistory(result);
			}

		}

	}
	
	public static void printHistory(List<String> history) {
		for (Iterator<String> i = history.iterator(); i.hasNext();) {
		    String item = i.next();
		    System.out.println(item);
		}
	}

	public static List decodeList(Object element) {
		if (element == null) {
			return null;
		}
		if (element instanceof List) {
			return (List) element;
		}
		if (element.getClass().isArray()) {
			int length = Array.getLength(element);
			ArrayList result = new ArrayList();
			for (int i = 0; i < length; i++) {
				result.add(Array.get(element, i));
			}
			return result;
		}
		return null;
	}
}

