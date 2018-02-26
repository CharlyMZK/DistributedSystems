package messages;

public class Request {
	String idClient, idRequest, stockName;
	Types type;
	Integer quantity;
	Double price;
	
	public String getJson()
	{
		ObjectMapper mapper = new ObjectMapper();

		//Object to JSON in String
		String jsonInString = mapper.writeValueAsString(this);
	}
}
