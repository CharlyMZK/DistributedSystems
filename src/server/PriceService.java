package src.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import src.messages.Request;
import src.messages.Type;

public class PriceService {

	private static int portXmlRpc = 8080;


	public PriceService() {
		try {
			WebServer webServer = new WebServer(portXmlRpc);

			XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
			PropertyHandlerMapping phm = new PropertyHandlerMapping();

			phm.addHandler( "Price", PriceService.class);
			xmlRpcServer.setHandlerMapping(phm);

			XmlRpcServerConfigImpl serverConfig =
					(XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
			// serverConfig.setEnabledForExtensions(true);
			// serverConfig.setContentLengthOptional(false);

			webServer.start();

			System.out.println("The Price Server has been started on port " + portXmlRpc + "..." );

		} catch (Exception exception) {
			System.err.println("JavaServer: " + exception);
		}
	}

	/**
	 *  Function that retrieve actual stock
	 * @param offset
	 * @param size
	 * @return
	 */
	public List<String> stocks(int offset, int size) {
		ArrayList<String> response = new ArrayList();
		System.out.println("Offset : " + offset + " size : " + size);

		if(offset < BrokerService.getRequests().size()) {
			for(int i = offset; i < offset + size; i++) {
				if(i + 1 > BrokerService.getRequests().size()) {
					break;
				}

				if(BrokerService.getRequests().get(i).getType() == Type.ASKS)
					response.add(BrokerService.getRequests().get(i).toString());
			}
		}

		return response;
	}

	/**
	 * Function that retrieve history of sold stock
	 * @param offset
	 * @param size
	 * @return
	 */
	public List<String> history(int offset, int size) {
		ArrayList<String> response = new ArrayList();
		System.out.println("Offset : " + offset + " size : " + size + " history size : " + BrokerService.getHistoryRequests().size());

		if(offset < BrokerService.getHistoryRequests().size()) {
			for(int i = offset; i < offset + size; i++) {
				if(i + 1 > BrokerService.getHistoryRequests().size()) {
					break;
				}
				response.add(BrokerService.getHistoryRequests().get(i).toString());
			}
		}

		return response;
	}
}
