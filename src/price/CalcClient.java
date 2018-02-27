package src.price;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;


public class CalcClient {

	private static String host = "127.0.0.1";
	private static int port = 8080;

	public static void main(String[] args) throws Exception {

		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();

		config.setServerURL(new URL("http://" + host + ":" + port + "/xmlrpc"));
		XmlRpcClient client = new XmlRpcClient();
		client.setConfig(config);

		Object[] params = new Object[]{new Integer(0), new Integer(2)};
		System.out.println("About to get results...(params[0] = " + params[0] 
	                           + ", params[1] = " + params[1] + ")." );

		List<String> result = decodeList( client.execute("Price.get", params));
		System.out.println("Add Result = " + result );

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

