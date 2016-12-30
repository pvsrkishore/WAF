package common.configurations;

import java.util.HashMap;
import java.util.Map;

import org.testng.ITestContext;
import org.testng.xml.XmlTest;

public class ContextManager {

	private static TestContext globalContext = new TestContext();
	private static ThreadLocal<TestContext> threadLevelContext = new ThreadLocal<TestContext>();
	private static Map<XmlTest, TestContext> testLevelContext = new HashMap<XmlTest, TestContext>();

	public static void initGlobalContext(ITestContext context) {

		globalContext.buildTestContext(context);
	}

	public static void initThreadLevelContext(ITestContext context) {
		if (threadLevelContext.get() == null) {
			threadLevelContext.set(new TestContext());
			threadLevelContext.get().buildTestContext(context);
		}
	}

	public static void initTestLevelContext(ITestContext context) {
		testLevelContext.put(context.getCurrentXmlTest(),
				new TestContext());
		testLevelContext.get(context.getCurrentXmlTest()).buildTestLevelContext(
				context);
	}

	public static TestContext getGlobalContext() {
		return globalContext;
	}

	public static TestContext getThreadLevelContext() {
		return threadLevelContext.get();
	}

	public static TestContext getTestLevelContext() {
		return testLevelContext.get(((ITestContext) getThreadLevelContext()
				.getParameter(TestContext.TESTNG_CONTEXT))
				.getCurrentXmlTest());
	}

	public static TestContext getTestLevelContext(ITestContext context) {
		return testLevelContext.get(context.getCurrentXmlTest());
	}
}
