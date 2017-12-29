package pages.utils;


import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import pages.utils.DriverManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomTestListener implements ITestListener{
	
	private static String executionReportsHome = "target/_executionReports";
	private static String executionDir = null;
	private static final String FILE_NAME = "ExtentReport.html";
	
	private static ExtentReports extent;
	private static ExtentHtmlReporter htmlReporter;
	private static ExtentTest currentTest;
	
	
	
	public void onStart(ITestContext iTestContext) {
		
		//	making sure paths are in place
		executionDir = ExecutionReportsUtils.dateToStringFormat(Calendar.getInstance().getTime(), "MMM_dd-hh_mm");
		ExecutionReportsUtils.createFolderIfNotExists(executionReportsHome);
		ExecutionReportsUtils.createFolderIfNotExists(executionReportsHome+"/"+executionDir);
		
		//	configuring the extent reporter
		
		htmlReporter = new ExtentHtmlReporter(executionReportsHome+"/"+executionDir +"/"+FILE_NAME);
		htmlReporter.setStartTime(ExecutionReportsUtils.getCurrentTime());
		
		htmlReporter.config().setDocumentTitle("ExtentReports - Created by TestNG Listener");
		htmlReporter.config().setReportName("ExtentReports - Created by TestNG Listener");
		htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
		htmlReporter.config().setChartVisibilityOnOpen(true);
		htmlReporter.config().setTheme(Theme.DARK);
		
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		extent.setReportUsesManualConfiguration(true);
	}
	
	private void assignCategories(ITestResult iTestResult){
		
		ArrayList<String> labels = new ArrayList<String>();
		String className = iTestResult.getTestClass().getRealClass().getName();
		String methodName = iTestResult.getMethod().getMethodName();
		
		labels.addAll(Arrays.asList(className.split("\\.")));
		labels.addAll(Arrays.asList(methodName.split("(?<=[a-z])(?=[A-Z])")));
		
		for (String label : labels) {
			currentTest.assignCategory(label.toLowerCase());
		}
	}
	
	//	replaces all references to jira issues with the configured key with links to the desired issues
	private String processDescription(String rawDescription){
		
		if ((null == rawDescription) || rawDescription.isEmpty() || (rawDescription.length() < 5)){
			return rawDescription;
		}
		
		try {
			List<String> matches = new ArrayList<String>();
			
			String jiraProjectKey = "UNIONVMS";
			String jiraIssueLinkTemplate = "<a target=\"_blank\" href='https://webgate.ec.europa.eu/CITnet/jira/browse/%s'>%s</a>\n";
			
			
			Matcher matcher = Pattern.compile(jiraProjectKey+"-\\d+").matcher(rawDescription);
			
			while (matcher.find()){
				System.out.println("matcher.group(0) = " + matcher.group(0));
				matches.add(matcher.group(0));
			}
			
			for (String match : matches) {
				rawDescription = rawDescription.replace(match, String.format(jiraIssueLinkTemplate, match, match));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rawDescription;
	}
	
	
	public void onTestStart(ITestResult iTestResult) {
		
		try {
			String testName = iTestResult.getMethod().getMethodName();
			String processedDescription = processDescription(iTestResult.getMethod().getDescription());
			currentTest = extent.createTest(testName, processedDescription);
			
			currentTest.getModel().setStartTime(ExecutionReportsUtils.getTime(iTestResult.getStartMillis()));
			
			assignCategories(iTestResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void onTestSuccess(ITestResult iTestResult) {
		currentTest.pass("<strong>TEST PASSED!!!!!!!</strong>");
		currentTest.getModel().setEndTime(ExecutionReportsUtils.getTime(iTestResult.getEndMillis()));
	}
	
	public void onTestFailure(ITestResult iTestResult) {
		
		try {
			String testName = iTestResult.getMethod().getMethodName();
			String screenshotName = testName+".jpg";
			
			File scrFile = ((TakesScreenshot)DriverManager.getDriver()).getScreenshotAs(OutputType.FILE);
			
			FileUtils.copyFile(scrFile, new File(executionReportsHome+"/"+executionDir+"/"+screenshotName));
			if(null != iTestResult.getThrowable()){
				currentTest.fail( iTestResult.getThrowable().getMessage() );
			}else {
				currentTest.fail("Could not get cause!!!");
			}
			currentTest.addScreenCaptureFromPath(screenshotName);
			currentTest.getModel().setEndTime(ExecutionReportsUtils.getTime(iTestResult.getEndMillis()));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	public void onTestSkipped(ITestResult iTestResult) {
		try {
			if(null == iTestResult.getThrowable()){
				currentTest.skip("<strong>TEST SKIPPED!!!!!!!</strong>");
			}else {
				currentTest.skip(iTestResult.getThrowable().getMessage());
			}
			currentTest.getModel().setEndTime(ExecutionReportsUtils.getTime(iTestResult.getEndMillis()));
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
			DriverManager.quitDriver();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}