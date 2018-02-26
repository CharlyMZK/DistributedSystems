package src.main.java.messages;

import org.json.JSONException;
import org.json.JSONObject;

public class Request {
	String idClient, idRequest, stockName;
	Types type;
	Integer quantity;
	Double price;

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

	public Request jsonToRequest(String json) throws JSONException {
		JSONObject jsonObject = new JSONObject(json);
		Request request = new Request();

		request.idClient = jsonObject.getString("idClient");
		request.idRequest = jsonObject.getString("idRequestion");
		request.stockName = jsonObject.getString("stockName");
		request.type = Types.valueOf(Types.class, jsonObject.getString("type"));
		request.quantity = jsonObject.getInt("quantity");
		request.price = jsonObject.getDouble("price");

		return request;
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

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public Types getType() {
		return type;
	}

	public void setType(Types type) {
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
}
