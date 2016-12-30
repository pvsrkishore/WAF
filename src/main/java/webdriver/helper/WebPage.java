package webdriver.helper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import common.configurations.TestContext;
import common.configurations.ContextManager;
import common.configurations.WebDriverManager;
import common.helpers.Log;
import common.utility.DownloadFilenameFilterFactory;
import common.utility.FindByAnnotationConverter;

/**
 * @author sabarinath.s Date: 12-Apr-2016 Time: 6:24:00 pm
 */

public class WebPage {

	public RemoteWebDriver driver = null;
	public Actions actionBuilder;
	FluentWait<WebDriver> wait;

	public WebPage() {
		driver = (RemoteWebDriver) WebDriverManager.getWebDriver(true);
		actionBuilder = new Actions(driver);
		wait = new WebDriverWait(driver, ContextManager.getGlobalContext()
				.getWebDriverTimeOut());
		initElements(this);
		waitForPageToload();
	}

	public WebPage(String url) {
		this();
		driver.get(url);
		waitForPageToload();
	}

	public void waitForPageToload() {

		wait.until((ExpectedCondition<Boolean>) webDriver -> {

			String documentState = (String) ((JavascriptExecutor) webDriver)
					.executeScript("return document.readyState;");
			if (documentState.equalsIgnoreCase("complete"))
				return true;
			else
				return false;
		});
	}

	public void initElements(WebPage page) {

		// Get fields list from page class and its super class
		ArrayList<Field> fieldsList = Lists.newArrayList(page.getClass()
				.getFields());
		fieldsList.addAll(Lists.newArrayList(page.getClass()
				.getDeclaredFields()));

		for (Field f : fieldsList) {

			By by = null;

			if (PageElement.class.isAssignableFrom(f.getType())
					&& f.isAnnotationPresent(FindBy.class)) {
				FindBy annotation = f.getAnnotation(FindBy.class);
				by = FindByAnnotationConverter.getByfromFindBy(annotation);
				if (by != null) {
					f.setAccessible(true);
					try {
						FieldUtils.writeField(page, f.getName(),
								new PageElement(f.getName(), by), true);

					} catch (IllegalAccessException e) {
						Log.warn("unable to initialize elements");
						e.printStackTrace();
					}
				}
			}

		}

	}

	public void waitforElementPresent(PageElement ele) {
		ele.waitforElementPresent();
	}

	public void waitForTextPresent(String textTobePresent) {
		WebDriverWait wait = new WebDriverWait(driver, ContextManager
				.getGlobalContext().getWebDriverTimeOutInSeconds());
		wait.until(ExpectedConditions.textToBePresentInElementLocated(
				By.xpath("."), textTobePresent));
	}

	public void waitForTextPresentInAnElement(String textTobePresent, By by) {
		WebDriverWait wait = new WebDriverWait(driver, ContextManager
				.getGlobalContext().getWebDriverTimeOutInSeconds());
		wait.until(ExpectedConditions.textToBePresentInElementLocated(by,
				textTobePresent));
	}


	public static String takeScreenShot() {

		return saveScreenShot(((TakesScreenshot) WebDriverManager
				.getWebDriver()).getScreenshotAs(OutputType.FILE));
	}

	
	private static String saveScreenShot(File file) {

		File screenshot = new File(ContextManager.getGlobalContext()
				.getValueAsString(TestContext.SCREENSHOTS_DIR)
				+ "/"
				+ UUID.randomUUID() + ".png");
		try {
			FileUtils.copyFile(file, screenshot);
		} catch (IOException e) {
			e.printStackTrace();
			Log.warn("unable to save screenshot");
		}
		return screenshot.getParentFile().getName() + "/"
				+ screenshot.getName();
	}

	public String getTitle() {
		return driver.getTitle();
	}

	public boolean isElementPresent(PageElement ele) {

		try {
			driver.findElement(ele.getBy());
			return true;
		} catch (NoSuchElementException nse) {
			return false;
		}
	}

	public void waitForDownloadtoComplete() {

		waitForDownloadtoComplete(null);
	}

	public void waitForDownloadtoComplete(String fileNameWithExtension) {

		String tempDownloadLocation = ContextManager.getTestLevelContext()
				.getValueAsString(TestContext.DOWNLOAD_DIR);

		File f = new File(tempDownloadLocation);

		FluentWait<File> wait = new FluentWait<File>(f)
				.withMessage("waiting for download to complete")
				.withTimeout(
						ContextManager.getGlobalContext().getWebDriverTimeOut(),
						TimeUnit.MILLISECONDS)
				.pollingEvery(5000, TimeUnit.MILLISECONDS);

		try {
			wait.until((Function<File, Boolean>) f1 -> {
				File[] listFiles = f1.listFiles(DownloadFilenameFilterFactory
						.getFileNameFilter(ContextManager.getTestLevelContext()
								.getBrowser(), fileNameWithExtension));
				return listFiles.length == 0;
			});
		} catch (Exception e) {
			Log.error("Failed to download fail within timeout time", e);
		}

	}

	public void pageUp() {
		Log.actionLog("scrolling up to previous page");
		WebDriver.Window window = ((RemoteWebDriver) driver).manage().window();
		RemoteWebDriver rDriver = (RemoteWebDriver) driver;
		rDriver.executeScript(" window.scrollBy(0, -"
				+ window.getSize().getHeight() + ")");
	}

	public void pageDown() {
		Log.actionLog("scrolling down to next page");
		WebDriver.Window window = ((RemoteWebDriver) driver).manage().window();
		RemoteWebDriver rDriver = (RemoteWebDriver) driver;
		rDriver.executeScript(" window.scrollBy(0, "
				+ window.getSize().getHeight() + ")");
	}

	public void scrollToTop() {
		Log.actionLog("scrolling to beginning of the page");
		RemoteWebDriver rDriver = (RemoteWebDriver) driver;
		rDriver.executeScript(" window.scrollTo(0, 0)");
	}

	public void scrollToBottom() {
		Log.actionLog("scrolling to the bottom of the page");
		RemoteWebDriver rDriver = (RemoteWebDriver) driver;
		rDriver.executeScript("var height = document.body.scrollHeight; window.scrollTo(0, height)");
	}

	public void scrollToElement(PageElement pageElement) {

		Log.actionLog("scrolling to " + pageElement.getName() + "on the page");
		new Actions(driver).moveToElement(pageElement.getWebElement())
				.perform();
	}

	// Upload works with xpath locator only
	public void uploadDocument(PageElement ele, String absolutePathOfFile) {
		if (ele.getWebElement().getCssValue("display").equalsIgnoreCase("none")) {
			String xpath = ele.getBy().toString().replace("By.xpath: ", "");
			((JavascriptExecutor) driver)
					.executeScript("document.evaluate(\""
							+ xpath
							+ "\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.style.display=\"block\"");
		}
	
		ele.type(absolutePathOfFile);
	}

	public void navigateToURL(String url) {
		Log.info("Navigating to the URL - " + url);
		this.driver.navigate().to(url);
	}

	public void waitForAJAXCallsToComplete() {
		(new WebDriverWait(driver, 30))
				.until((ExpectedCondition<Boolean>) d -> {
					JavascriptExecutor js = (JavascriptExecutor) d;
					return (Boolean) js
							.executeScript("return !!window.jQuery && window.jQuery.active == 0");
				});
	}

	public String getCurrentUrl() {
		return this.driver.getCurrentUrl();
	}

	public void clearCookies() {
		this.driver.manage().deleteAllCookies();
	}

	public String getCookie(String cookieName) {
		return this.driver.manage().getCookieNamed(cookieName).getValue();
	}
}
