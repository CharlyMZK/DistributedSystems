package src.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import src.messages.Request;

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

	public List<String> get(int offset, int size) {
		ArrayList<String> response = new ArrayList();

		if(offset < BrokerService.requestsHistory.size()) {
			for(int i = offset; i < offset + size; i++) {
				if(i > BrokerService.requestsHistory.size()) {
					break;
				}
				response.add(BrokerService.requestsHistory.get(i).toString());
			}
		}
		
		return response;
	}
}
