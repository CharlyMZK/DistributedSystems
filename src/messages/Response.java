package src.messages;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Response {
	public String idClient, idRequest;
	public ResponseState responseState;
	
	public String responseToJson()
	{
		String message = ""; 
		try {
			JSONObject json = new JSONObject();
			json.put("idClient", idClient);
			json.put("idRequest", idRequest);
			json.put("responseState", responseState);

			message = json.toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return message;
	}
	
	public static Response jsonToResponse(String json) throws JSONException
	{
		Response response = new Response();
		JSONObject obj = new JSONObject(json);
		
		response.setIdClient(obj.get("idClient").toString());
		response.setIdRequest(obj.get("idRequest").toString());
		response.setResponseState(obj.get("responseState").toString());
		
		return response;
	}
	
	public void setIdClient(String idClient)
	{
		this.idClient = idClient;
	}
	
	public void setIdRequest(String idRequest)
	{
		this.idRequest = idRequest;
	}
	
	public void setResponseState(String responseState)
	{
		if("ACCEPTED".equals(responseState)) 
			this.responseState = ResponseState.ACCEPTED;
		else if("REFUSED".equals(responseState))
			this.responseState = ResponseState.REFUSED;
		else if("TIMEOUT".equals(responseState))
			this.responseState = ResponseState.TIMEOUT;
		else
			throw new IllegalArgumentException("The string does not correspond to any response state");
	}
}
