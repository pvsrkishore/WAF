package sptafTests;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.chrome.ChromeDriverService;
import org.testng.annotations.Test;

import common.configurations.BaseTest;
import common.configurations.WebDriverManager;

/**
 * @author sabarinath.s
 * Date: 20-Apr-2016	
 * Time: 11:53:31 am 
 */

public class WebDriverTests extends BaseTest{

	
	@Test(groups={"test"})
	public void testChromeDriver(){
		
		WebDriverManager.getWebDriver(true);
	}
	
	public static void main(String[] a){
		
		ChromeDriverService service = new ChromeDriverService.Builder()
        .usingDriverExecutable(new File("/Users/sabarinath.s/gitRepositories/automation/sptaf/src/main/resources/chromedriver"))
        .usingAnyFreePort()
        .build();
		try {
			service.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
