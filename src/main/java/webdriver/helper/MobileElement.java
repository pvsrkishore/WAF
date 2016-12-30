package webdriver.helper;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import common.configurations.ContextManager;
import common.configurations.WebDriverManager;
import common.helpers.Log;

/**
 * @author sabarinath.s
 *
 */

public class MobileElement extends PageElement {

	public MobileElement(String name, By by) {
		super(name, by);
	}

	public MobileElement(String name, By by, MobileElement parent) {
		super(name, by, parent);
	}

	public void singleTap() {
		if (webElement == null)
			init();
		Log.actionMessagewithLinktoScreenShot("Single taping on "
				+ this.getName());
		AndroidDriver<AndroidElement> driver = ((AndroidDriver<AndroidElement>) WebDriverManager
				.getWebDriver());
		driver.tap(1, webElement, 1);
	}

	public void type(String textToType) {
		if (webElement == null)
			init();
		Log.actionMessagewithLinktoScreenShot("Typing text - " + textToType
				+ " on " + this.getName());
		// ele.clear();
		webElement.sendKeys(textToType);
	}

	public boolean isSelected() {

		if (webElement == null)
			init();
		try {
			return webElement.isSelected();
		} catch (Exception e) {
			Log.infoMessagewithLinktoScreenShot("unable to check the state of the element -"
					+ this.getName());
			return false;

		}
	}

	public boolean isDisplayed() {

		try {
			if (webElement == null)
				init();
		} catch (NoSuchElementException exe) {
			return false;
		}
		return webElement.isDisplayed();
	}

	public void waitforElementPresent() {
		try {
			if (webElement == null)
				init();
		} catch (Exception e) {
			Log.warn("element not present in layout. will wait for layout to be populated");
		}
		WebDriverWait wait = new WebDriverWait(WebDriverManager.getWebDriver(),
				ContextManager.getGlobalContext()
						.getWebDriverTimeOutInSeconds());
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
	}

	public void waitForAttributeValuetoChange(String attributeName,
			String expectedValue) {
		if (webElement == null)
			init();
		WebDriverWait wait = new WebDriverWait(
				((AndroidElement) webElement).getWrappedDriver(),
				ContextManager.getGlobalContext()
						.getWebDriverTimeOutInSeconds());
		wait.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				if (webElement.getAttribute(attributeName)
						.equals(expectedValue))
					return true;
				return false;
			}
		});
	}

	public String getText() {
		if (webElement == null)
			init();
		String text = webElement.getText();
		Log.info("text retrieved from the element - " + this.getName() + text);
		return text;
	}

	public AndroidElement getAndroidElement() {
		if (webElement == null)
			init();
		return (AndroidElement) this.webElement;
	}

	public By getBy() {
		return by;
	}

	public void setBy(By by) {
		this.by = by;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
