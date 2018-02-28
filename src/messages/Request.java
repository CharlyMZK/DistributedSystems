
package src.messages;
import java.rmi.server.UID;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONException;
import org.json.JSONObject;


public class Request {
	private String idClient;
	private String idRequest;
	private StockName stockName;
	private Type type;
	private Integer quantity;
	private Double price;

	public Request() {
		super();
	}

	public Request generateRandomRequest(String idClient) {
		Request request = new Request();

		request.idClient = idClient;
		request.idRequest = generateUID();
		request.stockName = StockName.randomType();
		request.type =  Type.randomType();
		request.quantity = ThreadLocalRandom.current().nextInt(0, 2 + 1) / 1;
		request.price = Double.parseDouble(String.format(Locale.US, "%.2f", ThreadLocalRandom.current().nextDouble(0, 2 + 1) / 1));

		return request;
	}

	public static Request generateRandomImprovedRequest(String idClient, StockName stockName, Type type) {
		Request request = new Request();

		request.idClient = idClient;
		request.idRequest = generateUID();
		request.stockName = stockName;
		request.type =  type;
		request.quantity = ThreadLocalRandom.current().nextInt(0, 2 + 1) / 1;
		request.price = Double.parseDouble(String.format(Locale.US, "%.2f", ThreadLocalRandom.current().nextDouble(0, 2 + 1) / 1));

		return request;
	}

	//Create a JSONObject representing the instance
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

	//Create a Request from a string json
	public static Request jsonToRequest(String json) throws JSONException {
		JSONObject jsonObject = new JSONObject(json);
		Request request = new Request();

		request.idClient = jsonObject.getString("idClient");
		request.idRequest = jsonObject.getString("idRequest");
		request.stockName = StockName.valueOf(StockName.class, jsonObject.getString("stockName"));
		request.type = Type.valueOf(Type.class, jsonObject.getString("type"));
		request.quantity = jsonObject.getInt("quantity");
		request.price = jsonObject.getDouble("price");

		return request;
	}

	//Return true if the instance and the request match
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

	//Return the request formated to fit in a csv file
	public String toCsvString()
	{
		return stockName + ";" + quantity + ";" + price + "\n";
	}
	//generate a UID
	private static String generateUID() {
		UID id = null;
		for (int idx=0; idx<10; ++idx){
			id = new UID();
		}
		return id.toString();
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
