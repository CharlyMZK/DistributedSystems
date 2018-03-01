package src.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import src.messages.Request;
import src.messages.StockName;
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

	public List<String> lastPrice(String stockName) {
		ArrayList<String> response = new ArrayList();
		if(stockName.equals(""))
			System.out.println("Get last price for each stockName");
		else
			System.out.println("Get last price for " + stockName);

		if(stockName.equals("MSFT")) {
			response.add(BrokerService.getLastStockTransactions().get(StockName.MSFT).toString());
		}
		else if (stockName.equals("IBM")){
			response.add(BrokerService.getLastStockTransactions().get(StockName.IBM).toString());
		}
		else if (stockName.equals("APL")){
			response.add(BrokerService.getLastStockTransactions().get(StockName.APL).toString());
		}
		else if (stockName.equals("ORCL")){
			response.add(BrokerService.getLastStockTransactions().get(StockName.ORCL).toString());
		}
		else {
			Iterator it = BrokerService.getLastStockTransactions().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry)it.next();
				response.add(pair.getValue().toString());
			}
		}

		return response;
	}
}
