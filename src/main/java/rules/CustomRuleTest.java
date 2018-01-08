package rules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.UUID;

public class CustomRuleTest {
	
	private UUID currentUUID;
	private String testID;
	private String ruleID;
	private String eORw;
	private String ruleLevelCode;
	private boolean pass;
	private String fileName;
	private ArrayList<String> testparams = new ArrayList<String>();
	private String jmsMessageBody;
	
	
	
	public String getRulesReponse() {
		return rulesReponse;
	}
	
	public void setRulesReponse(String rulesReponse) {
		this.rulesReponse = rulesReponse;
	}
	
	private String rulesReponse;
	
	public CustomRuleTest() {
	}
	
	//	gets as parameter a line from the csv file containing tests and parses it
	public CustomRuleTest(String csvLine) {
		
		String[] splits = csvLine.split(",");
		
		testID = splits[0].trim();
		pass = Boolean.valueOf(splits[1].trim());
		ruleID = splits[2].trim();
		eORw = splits[3].trim();
		ruleLevelCode = splits[4].trim();
		fileName = splits[5].trim();

//		after the 5th element there are only test params
		for (int i = 6; i < splits.length ; i++) {
			testparams.add(splits[i].trim());
		}
		
		prepareJMSMessageBody();
	}
	
//	reads the whole template file into a string
	private String getBodyTemplate() {
		
		StringBuilder sb = new StringBuilder();
		
		try {
			InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
			BufferedReader buf = new BufferedReader(new InputStreamReader(stream));
			String line = buf.readLine();
			
			while (line != null) {
				sb.append(line).append("\n");
				line = buf.readLine();
			}
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
//	prefills the template with params values
	private void prepareJMSMessageBody(){
		String template = getBodyTemplate();
		for (int i = 0; i <testparams.size() ; i++) {
			String toreplace = String.format("{{%s}}", i);
			template = template.replace(toreplace, testparams.get(i));
		}
		
		currentUUID = UUID.randomUUID();
		template = template.replace("{{guid}}", currentUUID.toString());
		
		this.jmsMessageBody = template;
	}
	
	
	public UUID getCurrentUUID() {
		return currentUUID;
	}
	
	public void setCurrentUUID(UUID currentUUID) {
		this.currentUUID = currentUUID;
	}
	
	public String getTestID() {
		return testID;
	}
	
	public void setTestID(String testID) {
		this.testID = testID;
	}
	
	public String getRuleID() {
		return ruleID;
	}
	
	public void setRuleID(String ruleID) {
		this.ruleID = ruleID;
	}
	
	public String geteORw() {
		return eORw;
	}
	
	public void seteORw(String eORw) {
		this.eORw = eORw;
	}
	
	public String getRuleLevelCode() {
		return ruleLevelCode;
	}
	
	public void setRuleLevelCode(String ruleLevelCode) {
		this.ruleLevelCode = ruleLevelCode;
	}
	
	public boolean shouldPass() {
		return pass;
	}
	
	public void setPass(boolean pass) {
		this.pass = pass;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public ArrayList<String> getTestparams() {
		return testparams;
	}
	
	public void setTestparams(ArrayList<String> testparams) {
		this.testparams = testparams;
	}
	
	public String getJmsMessageBody() {
		return jmsMessageBody;
	}
	
	public void setJmsMessageBody(String jmsMessageBody) {
		this.jmsMessageBody = jmsMessageBody;
	}
}
