package jms_poster;

import generic.Prop;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.*;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.UUID;

public class JMSMessagePoster {
	
	private static final String FILE_PATH = "C:\\EXPERIMENTS\\SaneRa_Poster\\fa_flux_message.xml";
	private Connection connection;
	private Session session;
	private MessageProducer producer;
	
	
	public JMSMessagePoster() {

		Properties props = new Properties();
		Destination destination;
		ConnectionFactory connectionFactory;
		Context ctx;
		try {
//			System.out.println("start: ");
			props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
			props.setProperty(Context.PROVIDER_URL, Prop.JMS_PROVIDER_URL.getValue());
			ctx = new InitialContext(props);

//			System.out.println("context created ");
			connectionFactory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
			
//			System.out.println("connection factory created ");
			connection = connectionFactory.createConnection();
			connection.start();
			
			destination = (Destination) ctx.lookup("dynamicQueues/UVMSFAPluginEvent");
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			producer = session.createProducer(destination);
		} catch (Exception e) {
			System.out.println("exception: ");
			e.printStackTrace();
		}
	}
	
//	******************************************
	public void sendMessageToActivityPlugin(String jmsMessage) {
		try {
			TextMessage message = prepareMessage(jmsMessage, session);
//			System.out.println("sending xml message ");
			producer.send(message);
//			System.out.println("Sent: " + message.getText());
		} catch (Exception e) {
			System.out.println("exception: ");
			e.printStackTrace();
		}
	}
	
	public void closeConnection() {
		try {
			connection.close();
		} catch (Exception e) {
			System.out.println("exception: ");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Prepare the message for sending and set minimal set of attributes, required by FLUX TL JMS;
	 *
	 * @param textMessage
	 * @return fluxMsg
	 * @throws JMSException
	 * @throws DatatypeConfigurationException
	 */
	private static TextMessage prepareMessage(String textMessage, Session session) throws JMSException {
		TextMessage fluxMsg = session.createTextMessage();
		fluxMsg.setText(textMessage);
		fluxMsg.setStringProperty(FluxConnectionConstants.CONNECTOR_ID, FluxConnectionConstants.CONNECTOR_ID_VAL);
		fluxMsg.setStringProperty(FluxConnectionConstants.FLUX_ENV_AD, FluxConnectionConstants.FLUX_ENV_AD_VAL);
		fluxMsg.setStringProperty(FluxConnectionConstants.FLUX_ENV_DF, FluxConnectionConstants.FLUX_ENV_DF_VAL);
		fluxMsg.setStringProperty("ON", "abc@abc.com");
		fluxMsg.setStringProperty(FluxConnectionConstants.BUSINESS_UUID, createBusinessUUID());
		fluxMsg.setStringProperty(FluxConnectionConstants.FLUX_ENV_TODT, createStringDate());
		fluxMsg.setStringProperty(FluxConnectionConstants.FLUX_ENV_AR, FluxConnectionConstants.FLUX_ENV_AR_VAL);
		fluxMsg.setStringProperty("FR", "XEU");
		System.out.println(fluxMsg);
		System.out.println(fluxMsg);
		return fluxMsg;
	}
	
	private static String createBusinessUUID() {
		return UUID.randomUUID().toString();
	}
	
	private static String createStringDate() {
		GregorianCalendar gcal = (GregorianCalendar) GregorianCalendar.getInstance();
		gcal.setTime(new Date(System.currentTimeMillis() + 1000000));
		XMLGregorianCalendar xgcal;
		try {
			xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
			return xgcal.toString();
		} catch (DatatypeConfigurationException | NullPointerException e) {
			return null;
		}
	}
	
	
	
}
