package rules;

import db.RulesDBClient;
import jms_poster.JMSMessagePoster;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import rest.RulesClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


@Listeners({RulesTestListener.class})
public class JMSRuleTest {
	
	private static JMSMessagePoster jmsClient = new JMSMessagePoster();
	private static RulesDBClient dbClient = new RulesDBClient();
	
	@AfterTest
	public void after(){
		jmsClient.closeConnection();
		dbClient.closeConnection();
	}
	
	
	@DataProvider(name = "tests")
	public Object[][] dataProvider() throws Exception {
		ArrayList<CustomRuleTest> tests = new ArrayList<CustomRuleTest>();
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("tests.csv");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String line = br.readLine();
		tests.add(new CustomRuleTest());
		while ((line = br.readLine()) != null) {
			tests.add(new CustomRuleTest(line));
		}
		
		Object[][] toReturn = new Object[tests.size()][1];
		
		int i = 0;
		for (CustomRuleTest test : tests) {
			toReturn[i][0] = test;
			i++;
		}
		
		return toReturn;
	}
	
	
	@Test(dataProvider = "tests")
	public void testRuleTest(CustomRuleTest test) throws Exception {
		SoftAssert softAssert = new SoftAssert();

		jmsClient.sendMessageToActivityPlugin(test.getJmsMessageBody());
		String ruleResponse = dbClient.getDBRulesResponse(test.getCurrentUUID().toString());
		test.setRulesReponse(ruleResponse);
		
		
		softAssert.assertTrue(ruleResponse.contains("FLUXResponseMessage"), "Response doesn't contain FLUXResponseMessage");
		softAssert.assertTrue(ruleResponse.contains("FLUXResponseDocument"), "Response doesn't contain FLUXResponseDocument");

		JSONArray failuresArray = new JSONArray();

		try {
			failuresArray = XML.toJSONObject(ruleResponse).getJSONObject("ns3:FLUXResponseMessage").
					getJSONObject("ns3:FLUXResponseDocument").
					getJSONObject("RelatedValidationResultDocument").
					getJSONArray("RelatedValidationQualityAnalysis");
		} catch (Exception e) {
			softAssert.fail("Response format was different than expected: \n\n" + test.getRulesReponse());
		}

		softAssert.assertTrue(allFailuresHaveXPath(failuresArray), "All failure nodes DON't have XPATH subnode defined");


		JSONObject failureFR = getFailureForRule(failuresArray, test.getRuleID());
		if (test.shouldPass()) {
			softAssert.assertNull(failureFR, "Rule failed when it should pass");
		} else {
			softAssert.assertNotNull(failureFR, "Rule passed when it should fail");
		}

		if (null != failureFR) {
			softAssert.assertTrue(test.geteORw().equalsIgnoreCase(failureFR.getString("TypeCode")), "Type code was not the one expected");
			softAssert.assertTrue(test.getRuleLevelCode().equalsIgnoreCase(failureFR.getString("LevelCode")), "Level code was not the one expected");
		}

		softAssert.assertAll();
		
	}
	
	private boolean allFailuresHaveXPath(JSONArray failures) {
		boolean allHaveXPath = true;
		
		for (int i = 0; i < failures.length(); i++) {
			String xpath = null;
			try {
				xpath = failures.getJSONObject(i).get("ReferencedItem").toString();
				System.out.println("xpath = " + xpath);
			} catch (Exception e) {
				allHaveXPath = false;
			}
			if (null == xpath || xpath.isEmpty() || xpath.length() < 5) {
				allHaveXPath = false;
			}
		}
		return allHaveXPath;
	}
	
	
	private JSONObject getFailureForRule(JSONArray failures, String ruleID) {
		for (int i = 0; i < failures.length(); i++) {
			JSONObject curentFail = failures.getJSONObject(i);
			if (curentFail.getString("ID").equalsIgnoreCase(ruleID)) {
				return curentFail;
			}
		}
		return null;
	}
	
	
}