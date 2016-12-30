package common.configurations;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import common.configurations.datamodels.Browser;

public class WebDriverManager {

	// private static WebDriver webDriver = null;

	public static void quitWebDriver() {
		getWebDriverFromContext().quit();
		AddWebDriverToContext(null);
	}

	public static WebDriver getWebDriverFromContext() {
		return (WebDriver) (ContextManager.getThreadLevelContext()
				.getParameter(TestContext.WEBDRIVER));
	}

	public static void AddWebDriverToContext(WebDriver driver) {
		ContextManager.getThreadLevelContext().setValue(
				TestContext.WEBDRIVER, driver);
	}

	private static WebDriver getWebDriver(Browser browser) {
		if (getWebDriverFromContext() != null)
			return getWebDriverFromContext();
		else
			return instantiateDriver(browser);
	}

	public static WebDriver getWebDriver() {
		return getWebDriverFromContext();
	}

	public static WebDriver getWebDriver(boolean create) {
		if (!create)
			return getWebDriverFromContext();
		else
			return getWebDriver(ContextManager.getTestLevelContext()
					.getBrowser());
	}

	private static WebDriver instantiateDriver(Browser browser) {

		System.out.println("creating new webdriver instance");
		WebDriver webDriver = null;
		switch (browser) {
		case FIREFOX: {
			webDriver = new FirefoxDriver(WebDriverConfig.getFireFoxConfig());
			break;
		}

		case CHROME: {
			webDriver = new RemoteWebDriver(ContextManager
					.getTestLevelContext().getChromeDriverService().getUrl(),
					WebDriverConfig.getChromeConfig());
			break;
		}

		case ANDROID: {
			webDriver = new AndroidDriver<AndroidElement>(ContextManager
					.getTestLevelContext().getAppiumServer().getUrl(),
					WebDriverConfig.getAndroidConfig());
			break;
		}

		default:
			return null;
		}

		if (ContextManager.getTestLevelContext().getPlatform() != null
				&& !ContextManager.getTestLevelContext().getPlatform()
						.equals("MOBILE"))
			webDriver.manage().window().maximize();
		webDriver
				.manage()
				.timeouts()
				.implicitlyWait(
						Long.valueOf(ContextManager.getGlobalContext()
								.getValueAsString(
										TestContext.WEBDRIVER_TIMEOUT)),
						TimeUnit.MILLISECONDS);

		AddWebDriverToContext(webDriver);
		return webDriver;
	}

}
