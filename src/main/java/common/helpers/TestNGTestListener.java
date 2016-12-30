package common.helpers;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import common.configurations.ContextManager;

/**
 * @author sabarinath.s
 * Date: 06-May-2015	
 * Time: 2:58:26 pm 
 */

public class TestNGTestListener implements ITestListener {


	public static volatile ConcurrentHashMap<ITestNGMethod,  AtomicInteger> resultmap = new ConcurrentHashMap<ITestNGMethod,  AtomicInteger> ();  

	public static Set <ITestResult> failedTests = new HashSet<ITestResult>();

	@Override
	public void onTestStart(ITestResult result) {
		
		Log.info("Test execution starts");
		Log.actionLogginCountMapper.put(result, 0);
		if(resultmap.get(result.getMethod())==null)
			resultmap.put(result.getMethod(), new AtomicInteger(1));
	}

	@Override
	public void onTestSuccess(ITestResult result) {

		RetryAnalyzer retryAnalyzer	= (RetryAnalyzer)result.getMethod().getRetryAnalyzer();
		if(retryAnalyzer !=null)
			retryAnalyzer.resetCountOnSuccess(result);
	}

	@Override
	public void onTestFailure(ITestResult result) {
		
		RetryAnalyzer retryAnalyzer	= (RetryAnalyzer)result.getMethod().getRetryAnalyzer();

		if(retryAnalyzer!=null && retryAnalyzer.isRetryPossible(result)){
			failedTests.add(result);
		}

	}


	@Override
	public void onTestSkipped(ITestResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStart(ITestContext context) {

		if(ContextManager.getGlobalContext().isRetryEnabled()){
			for(ITestNGMethod tm : context.getAllTestMethods()){
				tm.setRetryAnalyzer(new RetryAnalyzer());
			}
		}

	}

	@Override
	public void onFinish(ITestContext context) {

		for(ITestResult tr : failedTests){
			context.getFailedTests().removeResult(tr);
		}
	}


}
