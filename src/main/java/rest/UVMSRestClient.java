package rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import generic.Prop;
import org.apache.http.entity.ContentType;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Map;

public class UVMSRestClient {

	private String token;
	private Client c;
	
	public UVMSRestClient() {
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
			e.printStackTrace();
		}
	}
	
	
	public ClientResponse post(String path, String body){
		
		ClientResponse resp = c.resource(Prop.SERVER.getValue()).path(path).getRequestBuilder().
				header("Content-Type", "application/json").
				header("authorization", token).
				entity(body).post(ClientResponse.class);
				
		return resp;
	}
	
	public ClientResponse get(String path, Map<String, String> params){
		MultivaluedMap<String, String> local_params = new MultivaluedHashMap<String, String>();
		
		if(null != params) {
			for (Map.Entry<String, String> stringStringEntry : params.entrySet()) {
				local_params.putSingle(stringStringEntry.getKey(), stringStringEntry.getValue());
			}
		}
		
		
		ClientResponse resp = c.resource(Prop.SERVER.getValue()).path(path).queryParams(local_params).getRequestBuilder().
				header("Content-Type", ContentType.APPLICATION_JSON).
				header("authorization", token).get(ClientResponse.class);
		
		return resp;
	}
	
}
