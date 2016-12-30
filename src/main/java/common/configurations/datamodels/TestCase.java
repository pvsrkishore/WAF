package common.configurations.datamodels;

/**
 * @author sabarinath.s
 * Date: 05-May-2015	
 * Time: 2:39:58 pm 
 */
public class TestCase {

	private String name;
	private TEST_PRIORITY priority;
	private TEST_TYPE type;


	public static enum TEST_PRIORITY{
		P0,P1,P2,P3;
	}

	public static enum TEST_TYPE{
		FUNCTIONAL, SMOKE, REGRESSION;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TEST_PRIORITY getPriority() {
		return priority;
	}

	public void setPriority(TEST_PRIORITY priority) {
		this.priority = priority;
	}

	public TEST_TYPE getType() {
		return type;
	}

	public void setType(TEST_TYPE type) {
		this.type = type;
	}
	
}
