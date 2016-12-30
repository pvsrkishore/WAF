package common.utility;

import java.util.HashMap;
import java.util.Map;

import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

import com.google.common.base.Function;

/**
 * @author sabarinath.s
 * Date: 29-Apr-2016	
 * Time: 2:08:23 pm 
 */

public class FindByAnnotationConverter {
	
	private static Map<Function<FindBy, String>, Function<String, By>> findByMap = new HashMap<>();
	private static Map<Function<AndroidFindBy, String>, Function<String, By>> androidFindByMap = new HashMap<>();


	static{
		findByMap.put(FindBy::id, By::id);
		findByMap.put(FindBy::css, By::cssSelector);
		findByMap.put(FindBy::name, By::name);
		findByMap.put(FindBy::tagName, By::tagName);
		findByMap.put(FindBy::xpath, By::xpath);
		findByMap.put(FindBy::linkText, By::linkText);
		findByMap.put(FindBy::partialLinkText, By::partialLinkText);
		findByMap.put(FindBy::className, By::className);

		androidFindByMap.put(AndroidFindBy::id, By::id);
		androidFindByMap.put(AndroidFindBy::tagName, MobileBy::tagName);
		androidFindByMap.put(AndroidFindBy::xpath, By::xpath);
		androidFindByMap.put(AndroidFindBy::uiAutomator, MobileBy::AndroidUIAutomator);
		androidFindByMap.put(AndroidFindBy::accessibility, MobileBy::AccessibilityId);
		androidFindByMap.put(AndroidFindBy::className, By::className);
	}
	
	public static By getByfromFindBy(FindBy findBy){

		for (Function<FindBy, String> func : findByMap.keySet()) {
					String value = func.apply(findBy);
					if (StringUtils.isNotEmpty(value)) {
						return findByMap.get(func).apply(value);
					}
				}
		return null;
	}

	public static By getByfromFindBy(AndroidFindBy findBy){

			for (Function<AndroidFindBy, String> func : androidFindByMap.keySet()) {
						String value = func.apply(findBy);
						if (StringUtils.isNotEmpty(value)) {
							return androidFindByMap.get(func).apply(value);
						}
					}
			return null;
		}


}
