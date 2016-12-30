package webdriver.helper;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.pagefactory.AndroidFindBy;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.apache.commons.lang.reflect.FieldUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.collect.Lists;
import common.configurations.ContextManager;
import common.helpers.Log;
import common.utility.FindByAnnotationConverter;

@SuppressWarnings("unchecked")
public class MobilePage extends WebPage {

	public MobilePage() {

	}

	@Override
	public void initElements(WebPage page) {

		// Get fields list from page class and its super class
		ArrayList<Field> fieldsList = Lists.newArrayList(page.getClass()
				.getFields());
		fieldsList.addAll(Lists.newArrayList(page.getClass()
				.getDeclaredFields()));

		for (Field f : fieldsList) {

			By by = null;
			if (PageElement.class.isAssignableFrom(f.getType())
					&& f.isAnnotationPresent(AndroidFindBy.class)) {
				AndroidFindBy annotation = f.getAnnotation(AndroidFindBy.class);
				by = FindByAnnotationConverter.getByfromFindBy(annotation);

				if (by != null) {
					f.setAccessible(true);
					try {
						FieldUtils.writeField(page, f.getName(),
								new MobileElement(f.getName(), by), true);

					} catch (IllegalAccessException e) {
						Log.warn("unable to initialize elements");
						e.printStackTrace();
					}
				}
			}

		}

	}

	public void waitForActivity(final String activityName) {
		WebDriverWait wait = new WebDriverWait(driver, ContextManager
				.getGlobalContext().getWebDriverTimeOutInSeconds());
		wait.until(new ExpectedCondition<Boolean>() {

			public Boolean apply(WebDriver driver) {
				if (activityName
						.equalsIgnoreCase(((AndroidDriver<AndroidElement>) driver)
								.currentActivity()))
					return true;
				else
					return false;
			}

		});
	}

	public String getCurrentActivity() {
		return ((AndroidDriver<AndroidElement>) driver).currentActivity();
	}

	public void hideKeyBoard() {
		try {

			((AndroidDriver<AndroidElement>) driver).hideKeyboard();
			Thread.sleep(2000);
		} catch (Exception e) {
			Log.warn("unable to hide keyboard");
		}
	}

	public void scrollTo(String text) {
		((AndroidDriver<AndroidElement>) driver).scrollTo(text);
	}

}
