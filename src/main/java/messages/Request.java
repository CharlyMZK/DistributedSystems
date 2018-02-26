package main.java.messages;
import org.json.*;


public class Request {
	String idClient, idRequest, stockName;
	Types type;
	Integer quantity;
	Double price;
	
	public String getJson() throws JSONException
	{
		String message;
		JSONObject json = new JSONObject();
		json.put("name", "student");

		JSONArray array = new JSONArray();
		JSONObject item = new JSONObject();
		item.put("information", "test");
		item.put("id", 3);
		item.put("name", "course1");
		array.put(item);

		json.put("course", array);

		message = json.toString();
		return message;
	}
}
