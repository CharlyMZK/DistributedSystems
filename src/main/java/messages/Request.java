package main.java.messages;
import org.json.*;


public class Request {
	String idClient, idRequest, stockName;
	Types type;
	Integer quantity;
	Double price;
	
	public String getJson() throws JSONException
	{
		JSONObject obj = new JSONObject(" .... ");
		String pageName = obj.getJSONObject("pageInfo").getString("pageName");

		JSONArray arr = obj.getJSONArray("posts");
		for (int i = 0; i < arr.length(); i++)
		{
		    String post_id = arr.getJSONObject(i).getString("post_id");
		    
		}
		return null;
	}
}
