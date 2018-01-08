package rules;

import generic.ExecutionReportsUtils;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

public class RulesTestListener implements ITestListener {
	
	private static final String FILE_NAME = "ExtentReport-%s.html";
	
	private static ExtentReports extent;
	private static ExtentHtmlReporter htmlReporter;
	private static ExtentTest currentTest;
	
	
	private String prettyXML(String xml) {
		
		if(null == xml){
			return "";
		}
		
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = db.parse(new InputSource(new StringReader(xml)));
			
			OutputFormat format = new OutputFormat(doc);
			format.setIndenting(true);
			format.setIndent(2);
			format.setOmitXMLDeclaration(true);
			format.setLineWidth(Integer.MAX_VALUE);
			Writer outxml = new StringWriter();
			XMLSerializer serializer = new XMLSerializer(outxml, format);
			serializer.serialize(doc);
			
			
			return outxml.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xml;
	}
	
	private String getTestResultDetails(CustomRuleTest test, String message) {
		String template = "<table>" +
				"<tr><td><strong> %s </strong></td></tr>" +
//				"<tr><td>%s</td></tr>" +
				"<tr><td><xmp>%s</xmp></td></tr>" +
				"<tr><td><xmp>%s</xmp></td></tr>" +
				"</table>";
		
		return String.format(template, message, test.getJmsMessageBody(), prettyXML(test.getRulesReponse()));
	}
	
	public void onStart(ITestContext iTestContext) {
		
		//	configuring the extent reporter
		
		String filename = String.format(FILE_NAME, ExecutionReportsUtils.currentDateToStringFormat("yyyy-MM-dd_HH-mm"));
		try {
			File f = new File(System.getProperty("user.dir"), filename);
			
			f.createNewFile();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		htmlReporter = new ExtentHtmlReporter(filename);
		htmlReporter.setStartTime(ExecutionReportsUtils.getCurrentTime());
		
		htmlReporter.config().setDocumentTitle("Rules Tests");
		htmlReporter.config().setReportName("Run report");
		htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
		htmlReporter.config().setChartVisibilityOnOpen(true);
		htmlReporter.config().setTheme(Theme.DARK);
		
		htmlReporter.config().setChartVisibilityOnOpen(false);
		
		
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		extent.setReportUsesManualConfiguration(true);
	}
	
	public void onTestStart(ITestResult iTestResult) {
		
		try {
			String testName = iTestResult.getMethod().getMethodName();
			
			currentTest = extent.createTest(testName);
			currentTest.getModel().setStartTime(ExecutionReportsUtils.getTime(iTestResult.getStartMillis()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	public void onTestSuccess(ITestResult iTestResult) {
		
		CustomRuleTest test = (CustomRuleTest) iTestResult.getParameters()[0];
		
		currentTest.pass(getTestResultDetails(test, "PASSED!!!"));
		currentTest.getModel().setEndTime(ExecutionReportsUtils.getTime(iTestResult.getEndMillis()));
		currentTest.getModel().setName(String.format("%s - %s", test.getTestID(), test.getRuleID()));
		currentTest.getModel().setDescription(getTestDescription(test));
		
	}
	
	public void onTestFailure(ITestResult iTestResult) {
		
		CustomRuleTest test = (CustomRuleTest) iTestResult.getParameters()[0];
		
		try {
			if (null != iTestResult.getThrowable()) {
				currentTest.fail(getTestResultDetails(test, "FAILED:  " + iTestResult.getThrowable().getMessage()));
				
			} else {
				currentTest.fail(getTestResultDetails(test, "FAILED: (Cause UNKONOWN)"));
			}
			
			currentTest.getModel().setEndTime(ExecutionReportsUtils.getTime(iTestResult.getEndMillis()));
			currentTest.getModel().setName(String.format("%s - %s", test.getTestID(), test.getRuleID()));
			currentTest.getModel().setDescription(getTestDescription(test));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void onTestSkipped(ITestResult iTestResult) {
		
		CustomRuleTest test = (CustomRuleTest) iTestResult.getParameters()[0];
		
		try {
			if (null == iTestResult.getThrowable()) {
				currentTest.skip(getTestResultDetails(test, "SKIPPED: (Cause UNKNOWN)"));
			} else {
				currentTest.skip(getTestResultDetails(test, "SKIPPED: " + iTestResult.getThrowable().getMessage()));
			}
			currentTest.getModel().setEndTime(ExecutionReportsUtils.getTime(iTestResult.getEndMillis()));
			currentTest.getModel().setName(String.format("%s - %s", test.getTestID(), test.getRuleID()));
			currentTest.getModel().setDescription(getTestDescription(test));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
		try {
			onTestFailure(iTestResult);
			currentTest.getModel().setEndTime(ExecutionReportsUtils.getTime(iTestResult.getEndMillis()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onFinish(ITestContext iTestContext) {
		try {
			htmlReporter.setEndTime(ExecutionReportsUtils.getCurrentTime());
			htmlReporter.flush();
			htmlReporter.stop();
			extent.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private String getTestDescription(CustomRuleTest test) {
		StringBuilder description = new StringBuilder("<table border = 2px>");
		String tableRowTemplate = "<tr><td>%s</td><td>%s</td></tr>";
		
		description.append(String.format(tableRowTemplate, "TestID", test.getTestID()));
		description.append(String.format(tableRowTemplate, "RuleID", test.getRuleID()));
		description.append(String.format(tableRowTemplate, "ErrLevel", test.geteORw()));
		description.append(String.format(tableRowTemplate, "Level", test.getRuleLevelCode()));
		description.append(String.format(tableRowTemplate, "ShouldPass", test.shouldPass()));
		
		description.append("</table>");
		return description.toString();
	}
	
	
}
