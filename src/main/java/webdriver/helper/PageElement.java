package webdriver.helper;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import common.configurations.ContextManager;
import common.configurations.WebDriverManager;
import common.helpers.Log;

/**
 * @author sabarinath.s Date: 13-Apr-2016 Time: 3:52:59 pm
 */

public class PageElement {

	protected By by;
	protected WebElement webElement;
	protected PageElement parent;
	protected String name;

	public PageElement() {

	}

	public PageElement(String name, By by) {
		this.setName(name);
		this.setBy(by);
	}

	public PageElement(String name, By by, PageElement parent) {
		this.setName(name);
		this.setBy(by);
	}

	protected void init() {
		try {
			if (parent != null)
				webElement = parent.getWebElement().findElement(by);
			else
				webElement = WebDriverManager.getWebDriver().findElement(
						getBy());
		} catch (Exception e) {
			Log.error("unable to locate the element - " + getName(), e);
			throw e;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public By getBy() {
		return by;
	}

	public void setBy(By by) {
		this.by = by;
	}

	public WebElement getWebElement() {
		if (webElement == null)
			init();
		return webElement;
	}

	public void setWebElement(RemoteWebElement ele) {
		this.webElement = ele;
	}

	public PageElement getParent() {
		return parent;
	}

	public void setParent(PageElement parent) {
		this.parent = parent;
	}

	public void type(String textToType) {
		if (webElement == null)
			init();
		Log.actionMessagewithLinktoScreenShot("Typing text - " + textToType
				+ " on " + this.getName());
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

	public void selectByIndex(int index) {
		Log.actionMessagewithLinktoScreenShot("Selecting value at position"
				+ index + " from the dropdown" + getName());
		Select select = new Select(getWebElement());
		select.selectByIndex(index);
	}

	public void selectByValue(String value) {
		Log.actionMessagewithLinktoScreenShot("Selecting value - '" + value
				+ "' from the dropdown" + getName());
		Select select = new Select(getWebElement());
		select.selectByValue(value);
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
		WebDriverWait wait = new WebDriverWait(WebDriverManager.getWebDriver(),
				ContextManager.getGlobalContext()
						.getWebDriverTimeOutInSeconds());
		wait.until(ExpectedConditions.presenceOfElementLocated(by));
	}

	public void waitForAttributeValuetoChange(String attributeName,
			String expectedValue) {
		if (webElement == null)
			init();
		WebDriverWait wait = new WebDriverWait(
				((RemoteWebElement) webElement).getWrappedDriver(),
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

	public void click() {

		if (webElement == null)
			init();
		Log.actionMessagewithLinktoScreenShot("Clicking on " + this.getName());
		webElement.click();
	}

	public void hover() {

		if (webElement == null)
			init();
		Actions a = new Actions(WebDriverManager.getWebDriverFromContext());
		a.moveToElement(webElement).release().perform();
	}

}
