
package src.messages;
import java.rmi.server.UID;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Request {
	private String idClient;
	private String idRequest;
	private StockName stockName;
	private Type type;
	private Integer quantity;
	private Double price;

	/**
	 * Empty constructor
	 */
	public Request() {
	}
	
	/**
	 * Generate a random request
	 * 
	 * @param idClient id of the client
 	 * @return Request
	 */
	public Request generateRandomRequest(String idClient) {
		Request request = new Request();

		request.idClient = idClient;
		request.idRequest = generateUID();
		request.stockName = StockName.randomType();
		request.type =  Type.randomType();
		request.quantity = ThreadLocalRandom.current().nextInt(1, 10 + 1) / 1;
		request.price = Double.parseDouble(String.format(Locale.US, "%.2f", ThreadLocalRandom.current().nextDouble(0, 2 + 1) / 1));

		return request;
	}
	
	/**
	 * Generate a random request for smart traders
	 * 
	 * @param idClient id of the client
	 * @param stockName name of the stock
	 * @param type type of the request
	 * @return Request
	 */
	public static Request generateRandomImprovedRequest(String idClient, StockName stockName, Type type) {
		Request request = new Request();

		request.idClient = idClient;
		request.idRequest = generateUID();
		request.stockName = stockName;
		request.type =  type;
		request.quantity = ThreadLocalRandom.current().nextInt(1, 10 + 1) / 1;
		request.price = Double.parseDouble(String.format(Locale.US, "%.2f", ThreadLocalRandom.current().nextDouble(0, 2 + 1) / 1));

		return request;
	}

	/**
	 * Create a JSONObject representing the instance
	 * 
	 * @return JSONObject
	 * @throws JSONException
	 */
	public JSONObject requestToJson() throws JSONException {
		JSONObject request = new JSONObject();

		request.put("idClient", idClient);
		request.put("idRequest", idRequest);
		request.put("stockName", stockName);
		request.put("type", type);
		request.put("quantity", quantity);
		request.put("price", price);

		return request;
	}

	/**
	 * Create a Request from a string json
	 * 
	 * @param json String json
	 * @return Request
	 * @throws JSONException
	 */
	public static Request jsonToRequest(String json) throws JSONException {
		Request request = new Request();
		JSONObject jsonObject = null;
		if(isJSONValid(json)) {
			jsonObject = new JSONObject(json);
			request.idClient = jsonObject.getString("idClient");
			request.idRequest = jsonObject.getString("idRequest");
			request.stockName = StockName.valueOf(StockName.class, jsonObject.getString("stockName"));
			request.type = Type.valueOf(Type.class, jsonObject.getString("type"));
			request.quantity = jsonObject.getInt("quantity");
			request.price = jsonObject.getDouble("price");
		}else {
			System.out.println("Corrupted message");
		}
		
		return request;
	}

	/**
	 * Check if a bid match an ask
	 * 
	 * @param request Request to be match
	 * @return true if the instance and the request match
	 */
	public Boolean match(Request request)
	{
		if(request.type == Type.ASKS && type == Type.BIDS)
		{
			return request.price <= price && request.quantity == quantity && request.stockName == stockName;
		}
		else if(type == Type.ASKS && request.type == Type.BIDS)
		{
			return request.price >= price && request.quantity == quantity && request.stockName == stockName;
		}
		return false;
	}

	/**
	 * Return the request formated to fit in a csv file
	 * 
	 * @return String 
	 */
	public String toCsvString()
	{
		return stockName + ";" + quantity + ";" + price + "\n";
	}
	
	/**
	 * generate a UID
	 * 
	 * @return String UID
	 */
	private static String generateUID() {
		UID id = null;
		for (int idx=0; idx<10; ++idx){
			id = new UID();
		}
		return id.toString();
	}
	
	/**
	 * Check if the JSON received is valid
	 * @param test
	 * @return boolean
	 */
	public static boolean isJSONValid(String test) {
	    try {
	        new JSONObject(test);
	    } catch (JSONException ex) {
	        // edited, to include @Arthur's comment
	        // e.g. in case JSONArray is valid as well...
	        try {
	            new JSONArray(test);
	        } catch (JSONException ex1) {
	            return false;
	        }
	    }
	    return true;
	}

	public String getIdClient() {
		return idClient;
	}

	public void setIdClient(String idClient) {
		this.idClient = idClient;
	}

	public String getIdRequest() {
		return idRequest;
	}

	public void setIdRequest(String idRequest) {
		this.idRequest = idRequest;
	}

	public StockName getStockName() {
		return stockName;
	}

	public void setStockName(StockName stockName) {
		this.stockName = stockName;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		if(type==Type.BIDS)
			return stockName + " buy " + quantity + " at " + price + " $";
		else 
			return stockName + " sell " + quantity + " at " + price + " $";
	}	
}
