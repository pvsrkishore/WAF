package common.helpers;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.testng.ITestResult;
import org.testng.Reporter;

import webdriver.helper.WebPage;

import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import common.configurations.TestContext;
import common.configurations.ContextManager;

public class Log {

	private static boolean logFlag = false;

	static {
		if (!ContextManager.getGlobalContext().isEmpty()
				&& ContextManager.getGlobalContext().getValueAsBoolean(
						TestContext.ENABLE_LOGS))
			logFlag = true;
	}

	public static Map<ITestResult, Integer> actionLogginCountMapper = new ConcurrentHashMap<ITestResult, Integer>();

	public static void actionLog(String message) {

		if (logFlag) {
			int currentCount = 0;
			ITestResult ctr = Reporter.getCurrentTestResult();
			if (ctr != null) {
				currentCount = getCurrentCount(ctr);
			}
			if (currentCount == 0)
				info(message);
			else
				Reporter.log(currentCount + ". " + message);
		} else {
			System.out.println(message);
		}
	}

	public static void info(String message) {

		if (logFlag) {
			Reporter.log(massageInfoMessage(message), true);
		} else {
			System.out.println(message);
		}
	}

	public static void warn(String message) {

		if (logFlag) {
			Reporter.log(message, true);
		} else {
			System.out.println(message);
		}
	}

	public static void error(String message, Throwable t) {
		if (logFlag) {
			Reporter.log(message + " \n"
					+ (t != null ? stringifyStackTrace(t) : ""), true);
		} else {
			System.out.println(message);
		}
	}

	public static void actionMessagewithLinktoScreenShot(String message,
			String pathToScreenShot) {

		actionLog(message + "\n <a href=" + pathToScreenShot
				+ ">Click here</a> to view the screenshot.");
	}

	public static void actionMessagewithLinktoScreenShot(String message) {

		if (logFlag)
			actionMessagewithLinktoScreenShot(message, WebPage.takeScreenShot());
		else
			actionMessagewithLinktoScreenShot(message, "no screenshot taken");
	}

	public static void infoMessagewithLinktoScreenShot(String message,
			String pathToScreenShot) {

		info(message + " <a href=" + pathToScreenShot
				+ ">Click here</a> to view the screenshot.");
	}

	public static void infoMessagewithLinktoScreenShot(String message) {
		if (logFlag)
			infoMessagewithLinktoScreenShot(message, WebPage.takeScreenShot());
		else
			infoMessagewithLinktoScreenShot(message, "no ScreenShot taken");
	}

	public static void apiError(RestServiceException e) {

		if (logFlag) {
			String errorMessage = null;
			if (e.getResponse() != null) {

				errorMessage = "Api call failed with follwing status code "
						+ e.getResponse().getStatus()
						+ ". error message sent in the body - "
						+ e.getResponse().getEntity(String.class);
			}

			Log.error(errorMessage, e);
		}
	}

	public static String stringifyStackTrace(Throwable t) {
		String st = ExceptionUtils.getStackTrace(t);
		System.out.println("error - " + st);
		return st;
	}

	private static int getCurrentCount(ITestResult tr) {

		try {
			Integer count;
			if ((count = actionLogginCountMapper.get(tr)) != null) {
				actionLogginCountMapper.put(tr, count + 1);
				return count + 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String massageInfoMessage(String message) {
		return "<p id =\"infoMessage\"> " + message + " </p>";
	}

	public static void apiSuccessFulLog(ClientRequest request,
			ClientResponse response) {

		if (logFlag) {
			String path = null;
			try {
				path = generateRestHTMLLog(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
			info("rest api call returned success wtih response code - "
					+ response.getStatus()
					+ ". "
					+ (path != null ? "<a href=" + path
							+ ">Click here</a> for Rest call details" : ""));
		}
	}

	public static void apiUnsuccessFulLog(ClientRequest request,
			ClientResponse response) {

		if (logFlag) {
			String path = null;
			try {
				path = generateRestHTMLLog(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
			info("rest api call failed wtih response code - "
					+ response.getStatus()
					+ ". "
					+ (path != null ? "<a href=" + path
							+ ">Click here</a> for Rest call details" : ""));
		}
	}

	public static String generateRestHTMLLog(ClientRequest request,
			ClientResponse response) {

		String path = null;
		try {
			File random = new File(ContextManager.getGlobalContext()
					.getValueAsString(TestContext.HTML_FILE_DIR)
					+ "/" + UUID.randomUUID().toString() + ".html");
			HtmlHelper.generateVelocityTemplate(request, response, random);
			path = random.getParentFile().getName() + "/" + random.getName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

}
