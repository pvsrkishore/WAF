package webdriver.helper;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.AndroidServerFlag;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sabarinath.s Date: 19-Jan-2016 Time: 6:35:02 pm
 */

public class AppiumServerLauncher {

	public AppiumServerLauncher() {
		builder = new AppiumServiceBuilder();
	}

	private AppiumServiceBuilder builder;
	private AppiumDriverLocalService appiumService;

	protected AppiumServiceBuilder buildWithArguments(String platFormName) {

		Map<String, String> map = new HashMap<String, String>();
		map.put("ANDROID_HOME", System.getenv("ANDROID_HOME"));
		builder.usingAnyFreePort()
				.withArgument(GeneralServerFlag.LOG_LEVEL.AUTOMATION_NAME,
						"appium")
				.withArgument(GeneralServerFlag.PLATFORM_NAME, platFormName)
				.withEnvironment(map);
		return builder;
	}

	protected AppiumServiceBuilder buildWithArguments(String platFormName,
			String deviceId) {

		buildWithArguments(platFormName).withArgument(GeneralServerFlag.UIID,
				deviceId);
		return builder;
	}

	public AppiumDriverLocalService startDefaultService(String platForm)
			throws Exception {
		return startService(buildWithArguments(platForm));
	}

	public AppiumDriverLocalService startService(String platFormName,
			String deviceId) throws Exception {
		return startService(buildWithArguments(platFormName, deviceId));
	}

	public AppiumDriverLocalService startService(String platFormName,
			String deviceId, String bootStrapPort) throws Exception {
		return startService(deviceId != null ? (buildWithArguments(
				platFormName, deviceId)) : (buildWithArguments(platFormName))
				.withArgument(AndroidServerFlag.BOOTSTRAP_PORT_NUMBER,
						bootStrapPort));
	}

	public AppiumDriverLocalService startService(
			AppiumServiceBuilder serviceBuilder) throws Exception {
		appiumService = AppiumDriverLocalService.buildService(serviceBuilder);
		appiumService.start();
		if (!appiumService.isRunning())
			throw new Exception(
					"Appium Service not started. Please check the settings");
		return appiumService;

	}

	public void stopService() {
		if (appiumService != null && appiumService.isRunning())
			appiumService.stop();
	}

}
