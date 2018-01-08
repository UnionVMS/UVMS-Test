package rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import generic.Prop;
import org.json.JSONException;
import org.json.JSONObject;

public class RulesClient {

	private String token;
	private Client c;
	
	public RulesClient() {
		c = Client.create();
		authenticate();
	}
	
	public void authenticate(){
		ClientResponse resp = c.resource(Prop.SERVER.getValue()).path(Prop.AUTH_PATH.getValue()).getRequestBuilder().
				header("Content-Type", "application/json").
				entity(Prop.AUTH_BODY.getValue()).post(ClientResponse.class);
		try {
			token = new JSONObject(resp.getEntity(String.class)).get("jwtoken").toString();
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public ClientResponse ruleReq(String path, String body){
		
		ClientResponse resp = c.resource(Prop.SERVER.getValue()).path(path).getRequestBuilder().
				header("Content-Type", "application/xml").
				header("authorization", token).
				entity(body).post(ClientResponse.class);
				
		return resp;
	}
	
	
	
}
