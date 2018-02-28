package src.price;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;


public class PriceClient {

	private static String host = "127.0.0.1";
	private static int port = 8080;
	private static int size = 4;

	public static void main(String[] args) throws Exception {
		
		// We configure the client
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();

		config.setServerURL(new URL("http://" + host + ":" + port + "/xmlrpc"));
		XmlRpcClient client = new XmlRpcClient();
		client.setConfig(config);
		
		// Call function with an offset of 0
		getFromServer(client, 0, "stocks");
		getFromServer(client, 0, "history");

	}
	
	public static void getFromServer(XmlRpcClient client, int offset, String method) {
		
		// First create param with offset and size
		Object[] params = new Object[]{new Integer(offset), new Integer(size)};
		System.out.println("About to get available " + method + " from " + params[0] 
				+ " with size " + params[1] + "." );
		
		try {
			List<String> result = decodeList(client.execute("Price." + method, params));
			printResult(result);
			
			// While result size is equal to size we call this function again with offset + size
			if(result.size() == size)
				getFromServer(client, offset + size, method);
			else 
				System.out.println("Get all stocks\n");
			
		}
		catch (XmlRpcException e) {
			e.printStackTrace();
		}
	}

	public static void printResult(List<String> history) {
		for (Iterator<String> i = history.iterator(); i.hasNext();) {
			String item = i.next();
			System.out.println(item);
		}
	}

	public static List<String> decodeList(Object element) {
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

