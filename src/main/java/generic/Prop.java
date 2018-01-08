package generic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public enum Prop {
	
	SERVER, JMS_PROVIDER_URL, AUTH_PATH, AUTH_BODY;
	
	private String resourceName = "environment.properties";
	private Properties props;
	
	Prop() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		props = new Properties();
		try(InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
			props.load(resourceStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getValue(){
		return props.getProperty(this.name());
	}
	
	
	
}
