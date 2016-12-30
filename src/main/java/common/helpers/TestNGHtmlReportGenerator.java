package common.helpers;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.DisplayTool;
import org.apache.velocity.tools.generic.MathTool;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import common.configurations.TestContext;
import common.configurations.ContextManager;

/**
 * @author sabarinath.s Date: 20-May-2015 Time: 2:32:43 pm
 */

public class TestNGHtmlReportGenerator implements IReporter {

	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites,
			String outputDirectory) {

		Map<String, Set<ITestResult>> failedTestResultsMap = new TreeMap<String, Set<ITestResult>>();
		Map<String, Set<ITestResult>> passedTestResultsMap = new TreeMap<String, Set<ITestResult>>();
		Map<String, Set<ITestResult>> skippedTestResultsMap = new TreeMap<String, Set<ITestResult>>();
		Map<ITestResult, List<String>> resultLogMapping = new HashMap<ITestResult, List<String>>();

		ISuite suite = suites.get(0);
		XmlSuite xmlSuite = xmlSuites.get(0);
		Map<String, ISuiteResult> results = suite.getResults();

		for (XmlTest test : xmlSuite.getTests()) {

			ISuiteResult iSuiteResult = results.get(test.getName());
			ITestContext testContext = iSuiteResult.getTestContext();

			failedTestResultsMap.put(test.getName(), testContext
					.getFailedTests().getAllResults());
			passedTestResultsMap.put(test.getName(), testContext
					.getPassedTests().getAllResults());
			skippedTestResultsMap.put(test.getName(), testContext
					.getSkippedTests().getAllResults());

			for (ITestResult tr : failedTestResultsMap.get(test.getName())) {
				resultLogMapping.put(tr, Reporter.getOutput(tr));
			}

			for (ITestResult tr : passedTestResultsMap.get(test.getName())) {
				resultLogMapping.put(tr, Reporter.getOutput(tr));
			}

		}
		System.out.println(resultLogMapping.toString());
		// move css file to output forlder
		try {
			InputStream resourceAsStream = getClass().getClassLoader()
					.getResourceAsStream("reporting.css");
			File cssFile_local = new File(ContextManager.getGlobalContext()
					.getValueAsString(TestContext.TESTNG_REPORT_DIR)
					+ "/reporting.css");
			FileUtils.copyInputStreamToFile(resourceAsStream, cssFile_local);
			resourceAsStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			generateVelocityTemplate(xmlSuite.getTests(), failedTestResultsMap,
					passedTestResultsMap, skippedTestResultsMap,
					resultLogMapping);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void generateVelocityTemplate(List<XmlTest> xmlTestList,
			Map<String, Set<ITestResult>> failedTestMethodsMap,
			Map<String, Set<ITestResult>> passedTestMethodsMap,
			Map<String, Set<ITestResult>> skippedTestMethodsMap,
			Map<ITestResult, List<String>> methodLogMapping) throws Exception {

		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class",
				ClasspathResourceLoader.class.getName());
		ve.init();

		VelocityContext context = new VelocityContext();
		context.put("xmlTestList", xmlTestList);
		context.put("passedTestMethodsMap", passedTestMethodsMap);
		context.put("failedTestMethodsMap", failedTestMethodsMap);
		context.put("skippedTestMethodsMap", skippedTestMethodsMap);
		context.put("methodLogMapping", methodLogMapping);
		context.put("dateTool", new DateTool());
		context.put("mathTool", new MathTool());
		context.put("displayTool", new DisplayTool());
		context.put("ExceptionUtils", new ExceptionUtils());

		File html = new File(ContextManager.getGlobalContext()
				.getValueAsString(TestContext.TESTNG_REPORT_DIR)
				+ "/report.html");
		FileWriter fw = new FileWriter(html);

		Template template = ve.getTemplate("sample.vm");
		template.merge(context, fw);
		System.out.println("Test report generated - " + html.getAbsolutePath());
		fw.flush();
		fw.close();
	}

}