package common.helpers;

import java.util.concurrent.atomic.AtomicInteger;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import common.configurations.TestContext;
import common.configurations.ContextManager;

/**
 * @author sabarinath.s Date: 20-May-2015 Time: 2:24:16 pm
 */

public class RetryAnalyzer implements IRetryAnalyzer {

	@Override
	public boolean retry(ITestResult result) {

		AtomicInteger count = null;

		if ((count = TestNGTestListener.resultmap.get(result.getMethod())) == null) {
			count = new AtomicInteger(1);
			TestNGTestListener.resultmap.put(result.getMethod(), count);
		}

		if (ContextManager.getTestLevelContext().getValueAsInteger(
				TestContext.RETRY_COUNT) >= TestNGTestListener.resultmap
				.get(result.getMethod()).getAndIncrement())
			return true;
		else
			resetCountOnSuccess(result);

		return false;
	}


	public boolean isRetryPossible(ITestResult result) {

		AtomicInteger count = null;

		System.out.println("Current run Count " + result.getName() + " - "
				+ TestNGTestListener.resultmap.get(result.getMethod()));

		if ((count = TestNGTestListener.resultmap.get(result.getMethod())) == null) {
			count = new AtomicInteger(1);
			TestNGTestListener.resultmap.put(result.getMethod(), count);
		}

		if (ContextManager.getTestLevelContext().getValueAsInteger(
				TestContext.RETRY_COUNT) >= count.get())
			return true;
		return false;
	}

	public void resetCountOnSuccess(ITestResult result) {
		if (TestNGTestListener.resultmap.get(result.getMethod()) != null)
			TestNGTestListener.resultmap.get(result.getMethod()).set(1);
	}

}
