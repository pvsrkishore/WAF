package common.configurations;

import io.appium.java_client.service.local.AppiumDriverLocalService;

import java.io.File;

import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.os.CommandLine;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import common.configurations.datamodels.Browser;
import webdriver.helper.AppiumServerLauncher;

/**
 * All classes with annotated test methods should extend this class. All
 * configuration methods required for context generation, reporting and
 * webdriver servers are initialized here. In case, you need to write your own
 * configuration methods, override this method in the testplan and call these
 * method from within your custom configuration method.
 */

public class BaseTest {

	/**
	 * Before suite method. initialize global context before any of the test
	 * methods are executed.
	 * 
	 * @param context
	 */
	@BeforeSuite(alwaysRun = true)
	public void initTestRun(ITestContext context) {
		ContextManager.initGlobalContext(context);

		// Directories initialization
		File testng_report_root_location = new File(ContextManager
				.getGlobalContext().getValueAsString(
						TestContext.TESTNG_REPORT_DIR));
		testng_report_root_location.mkdir();
		File htmlFileLocation = new File(
				testng_report_root_location.getAbsolutePath() + "/htmlFiles");
		htmlFileLocation.mkdir();
		File screenShotLocation = new File(
				testng_report_root_location.getAbsolutePath() + "/screenshots");
		screenShotLocation.mkdir();
		ContextManager.getGlobalContext().setValue(
				TestContext.HTML_FILE_DIR,
				htmlFileLocation.getAbsolutePath());
		ContextManager.getGlobalContext().setValue(
				TestContext.SCREENSHOTS_DIR,
				screenShotLocation.getAbsolutePath());

	}

	/**
	 * TestNG test level context is initialized here. Also, Appium and chrome
	 * servers are
	 * 
	 * @param context
	 * @throws Exception
	 */

	@BeforeTest(alwaysRun = true)
	public void beforeTest(ITestContext context) throws Exception {
		ContextManager.initTestLevelContext(context);

		if (ContextManager.getTestLevelContext(context).getBrowser()
				.equals(Browser.ANDROID)) {

			// starting appium servers
			String platFormName = ContextManager.getTestLevelContext(context)
					.getBrowser().toString();
			String deviceId = ContextManager.getTestLevelContext(context)
					.getValueAsString(TestContext.ANDROID_DEVICE_ID);
			AppiumDriverLocalService server = new AppiumServerLauncher()
					.startService(platFormName, deviceId,
							String.valueOf(getAndroidBootstrapPort()));
			ContextManager.getTestLevelContext(context).setValue(
					TestContext.APPIUM_SERVER, server);

		}

		if (ContextManager.getTestLevelContext(context).getBrowser()
				.equals(Browser.CHROME)) {

			String pathToChromDriver = ContextManager.getGlobalContext()
					.getValueAsString(TestContext.CHROME_DRIVER_PATH);

			if (pathToChromDriver != null)
				System.setProperty("webdriver.chrome.driver", pathToChromDriver);
			System.setProperty("webdriver.chrome.logfile",
					new File(".").getCanonicalPath() + "/chromedriver.log");

			ChromeDriverService defaultService = new ChromeDriverService.Builder()
					.usingAnyFreePort()
					.withLogFile(
							new File(new File(".").getCanonicalPath()
									+ "/chromedriver.log"))
					.withVerbose(true)
					.usingDriverExecutable(
							new File(CommandLine.find("chromedriver"))).build();
			defaultService.start();
			ContextManager.getTestLevelContext(context).setValue(
					TestContext.CHROME_DRIVER_SERVICE, defaultService);
		}
	}

	@BeforeMethod(alwaysRun = true)
	public void beforeMethod(ITestContext context) {
		ContextManager.initThreadLevelContext(context);
		System.out.println(ContextManager.getThreadLevelContext().hashCode());
	}

	@AfterMethod(alwaysRun = true)
	public void afterMethod(ITestContext context) {
		if (WebDriverManager.getWebDriver() != null) {
			WebDriverManager.quitWebDriver();
		}
	}

	@AfterTest(alwaysRun = true)
	public void afterSuite(ITestContext context) {
		ChromeDriverService service = ContextManager.getTestLevelContext(
				context).getChromeDriverService();
		if (service != null)
			service.stop();

		AppiumDriverLocalService appiumServer = ContextManager
				.getTestLevelContext(context).getAppiumServer();
		if (appiumServer != null)
			appiumServer.stop();
	}

	private static int getAndroidBootstrapPort() {

		return (int) ((Math.random() * 9999) + 5000);

	}

}