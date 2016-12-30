package common.configurations;

import io.appium.java_client.service.local.AppiumDriverLocalService;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.openqa.selenium.chrome.ChromeDriverService;
import org.testng.ITestContext;

import common.configurations.datamodels.Browser;
import common.configurations.datamodels.Environment;
import common.utility.Utility;

public class TestContext {

	public static final String ENVIRONMENT = "environment";
	public static final String RETRY_COUNT = "retry_count";
	public static final String BROWSER = "browser";
	public static final String TESTNG_CONTEXT = "testng_context";
	public static final String DOWNLOAD_DIR = "download_dir";
	public static final String FIREFOX_PROFILE = "ff_profile";
	public static final String WEBDRIVER_TIMEOUT = "webdriver_timeout";
	public static final String TESTNG_REPORT_DIR = "testng_report_dir";
	public static final String HTML_FILE_DIR = "html_file_dir";
	public static final String SCREENSHOTS_DIR = "screenshots_dir";
	public static final String RETRY_ENABLED = "retry_enabled";
	public static final String ENABLE_LOGS = "enable_logs";
	public static final String ENABLE_REPORTING = "enable_reporting";
	public static final String API_VERSION = "api_version";
	public static final String PLATFORM = "platform";
	public static final String WEBDRIVER = "webdriver";
	public static final String CHROME_DRIVER_SERVICE = "chrome_driver_service";
	public static final String CHROME_DRIVER_PATH = "chrome_driver_path";

	// Android configurations
	public static final String APP_LOCATION = "path_to_app";
	public static final String APK_NAME = "apk_name";
	public static final String ANDROID_VERSION = "android_version";
	public static final String APPIUM_SERVER = "appium_server";
	public static final String DEVICE_NAME = "deiveName";
	public static final String ANDROID_DEVICE_ID = "deviceId";
	public static final String APP_PACKAGE = "app_package";
	public static final String APP_LAUNCH_ACTIVITY = "app_launch_activity";
	public static final String APP_WAIT_ACTIVITY = "app_wait_activity";

	public Boolean isEmpty() {
		return (context.size() > 0 ? false : true);
	}

	private Map<String, Object> context = new HashMap<String, Object>();

	protected void loadProperties(Properties properties) {
		for (Entry<Object, Object> e : properties.entrySet()) {
			context.put(e.getKey().toString(), e.getValue());
		}
	}

	public Environment getEnvironment() {
		return Environment.valueOf((String) getParameter(ENVIRONMENT));
	}

	protected void buildTestContext(ITestContext context) {

		setValue(TESTNG_CONTEXT, context);
		setAttribute(context, ENVIRONMENT, "QA");
		try {
			switch (Environment.valueOf(getValueAsString(ENVIRONMENT))) {
			case QA: {
				loadProperties(Utility.readFromPropertiesFile("qa.properties"));
				break;
			}
			case STAGE: {
				loadProperties(Utility
						.readFromPropertiesFile("stage.properties"));
				break;
			}
			case PROD: {
				loadProperties(Utility
						.readFromPropertiesFile("prod.properties"));
				break;
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		setAttribute(context, RETRY_COUNT, "2");
		setAttribute(context, BROWSER, Browser.FIREFOX);
		setAttribute(context, PLATFORM, "DESKTOP");

		// setting web timeout in ms
		setAttribute(context, WEBDRIVER_TIMEOUT, "60000");

		setAttribute(context, ENABLE_LOGS, true);
		setAttribute(context, ENABLE_REPORTING, true);

		// setting default report directory
		setAttribute(context, TESTNG_REPORT_DIR, System.getProperty("user.dir")
				+ "/test-output/");

		setAttribute(context, RETRY_ENABLED, true);
		setAttribute(context, DEVICE_NAME, "Android Emulator");

		Map<String, String> params = context.getSuite().getXmlSuite()
				.getAllParameters();
		if (params != null) {
			for (Entry<String, String> e : params.entrySet()) {
				setAttribute(e.getKey(), e.getValue());
			}
		}
	}

	protected void buildTestLevelContext(ITestContext context) {

		buildTestContext(context);
		Map<String, String> parameters = context.getCurrentXmlTest()
				.getParameters();
		if (parameters != null) {
			for (Entry<String, String> e : parameters.entrySet()) {
				setValue(e.getKey(), e.getValue());
			}
		}
	}

	private void setAttribute(ITestContext ctx, String key, Object defaultValue) {
		setAttribute(
				key,
				getValueFromContext(ctx, key) != null ? getValueFromContext(
						ctx, key) : defaultValue);
	}

	private String getValueFromContext(ITestContext context, String key) {
		try {
			return context.getSuite().getXmlSuite().getParameter(key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void setAttribute(String key, Object value) {
		setValue(key, System.getenv(key) != null ? System.getenv(key) : value);
	}

	public void setValue(String key, Object value) {
		context.put(key, value);
	}

	public Object getParameter(String key) {
		return context.get(key);
	}

	public String getValueAsString(String key) {
		Object val = getParameter(key);
		return val != null ? val.toString() : null;
	}

	public Long getValueAsLong(String key) {
		String val = getValueAsString(key);
		return val != null ? Long.valueOf(getValueAsString(key)) : null;
	}

	public Integer getValueAsInteger(String key) {
		String val = getValueAsString(key);
		return val != null ? Integer.valueOf(getValueAsString(key)) : null;
	}

	// reutrns false if there is no value in the context
	public Boolean getValueAsBoolean(String key) {
		String val = getValueAsString(key);
		return val != null ? Boolean.valueOf(getValueAsString(key)) : false;
	}

	public long getWebDriverTimeOutInSeconds() {
		return getValueAsLong(TestContext.WEBDRIVER_TIMEOUT) / 1000;
	}

	public long getWebDriverTimeOut() {
		return getValueAsLong(TestContext.WEBDRIVER_TIMEOUT);
	}

	public Browser getBrowser() {
		Browser browser = null;
		try {
			browser = Browser.valueOf(this
					.getValueAsString(TestContext.BROWSER));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return browser;
	}

	public AppiumDriverLocalService getAppiumServer() {
		return (AppiumDriverLocalService) getParameter(TestContext.APPIUM_SERVER);
	}

	public String getPlatform() {
		return getValueAsString(TestContext.PLATFORM);
	}

	public String getApiVersion() {
		return getValueAsString(API_VERSION);
	}

	public void setApiVersion(String version) {
		setValue(API_VERSION, version);
	}

	public boolean isRetryEnabled() {
		return getValueAsBoolean(RETRY_ENABLED);
	}

	public ChromeDriverService getChromeDriverService() {
		return (ChromeDriverService) getParameter(CHROME_DRIVER_SERVICE);
	}

	@Override
	public String toString() {
		return context.toString();
	}

}
