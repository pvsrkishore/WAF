package common.configurations;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

public class WebDriverConfig {

	public static DesiredCapabilities getFireFoxConfig() {

		String downloadDir = getDownloadDirectory();

		File fireFoxprofile = null;
		try {
			fireFoxprofile = new File(WebDriverConfig.class.getClassLoader()
					.getResource("FireFoxCustomProfile").toURI());
		} catch (Exception e) {
			e.printStackTrace();
		}

		FirefoxProfile profile = null;

		if (fireFoxprofile == null)
			profile = new FirefoxProfile();
		else
			profile = new FirefoxProfile(fireFoxprofile);

		profile.setPreference("browser.download.dir", downloadDir);
		profile.setPreference("browser.download.folderList", 2);
		profile.setPreference("geo.enabled", false);
		profile.setPreference("browser.helperApps.alwaysAsk.force", false);
		profile.setPreference("browser.download.manager.showWheneStarting",
				false);
		profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
				"application/csv,application/vnd.ms-excel");
		if (!profile.areNativeEventsEnabled())
			profile.setEnableNativeEvents(true);
		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		capabilities.setJavascriptEnabled(true);
		capabilities.setCapability(FirefoxDriver.PROFILE, profile);
		return capabilities;
	}

	public static DesiredCapabilities getChromeConfig() {

		String downloadDir = getDownloadDirectory();

		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("disable-popup-blocking", true);
		chromePrefs.put("ignore-certifiate-errors", true);
		chromePrefs.put("download.default_directory", downloadDir);
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.setExperimentalOption("prefs", chromePrefs);
		chromeOptions.addArguments("--start-maximized");
		chromeOptions.addArguments("--test-type");
		capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
		capabilities.setJavascriptEnabled(true);
		return capabilities;
	}

	public static DesiredCapabilities getAndroidConfig() {

		String path = ContextManager.getGlobalContext().getValueAsString(
				TestContext.APP_LOCATION);
		String apk = ContextManager.getTestLevelContext().getValueAsString(
				TestContext.APK_NAME);
		String app_package = ContextManager.getTestLevelContext()
				.getValueAsString(TestContext.APP_PACKAGE);
		String app_activity = ContextManager.getTestLevelContext()
				.getValueAsString(TestContext.APP_LAUNCH_ACTIVITY);
		String app_wait_activity = ContextManager.getTestLevelContext()
				.getValueAsString(TestContext.APP_WAIT_ACTIVITY);

		String app = null;

		DesiredCapabilities capabilities = new DesiredCapabilities();

		try {
			if (apk != null && path != null) {

				app = path + "/" + apk;
				capabilities.setCapability("app", app);
			}

			if (app_package != null)
				capabilities.setCapability("appPackage", app_package);
			if (app_activity != null)
				capabilities.setCapability("appActivity", app_activity);
			if (app_wait_activity != null)
				capabilities
						.setCapability("appWaitActivity", app_wait_activity);

			capabilities.setCapability("platformName", "Android");
			capabilities.setCapability("automationName", "Appium");
			capabilities.setCapability("deviceName", "Android Emulator");

		} catch (NullPointerException e) {
			throw new WebDriverCreationException(e);
		}
		return capabilities;

	}

	public static String getTempDownloadLocation() throws IOException {

		String downloadPath = "/target/resource/temp";
		File file = new File(".");
		return file.getCanonicalPath() + downloadPath;
	}

	private static String getDownloadDirectory() {

		String downloadDir = ContextManager.getTestLevelContext()
				.getValueAsString(TestContext.DOWNLOAD_DIR);

		if (downloadDir == null) {
			try {
				downloadDir = getTempDownloadLocation();
				ContextManager.getTestLevelContext().setValue(
						TestContext.DOWNLOAD_DIR, downloadDir);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return downloadDir;
	}
}