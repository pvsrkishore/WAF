package common.configurations;

public class WebDriverCreationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public WebDriverCreationException(Throwable cause){
		super("unable to create webdriver instance. Please check the configuration", cause);
	}
	
	public WebDriverCreationException(){
		this("unable to create webdriver instance. Please check the configuration");
	}
	
	public WebDriverCreationException(String messsage){
		super(messsage);
	}
}
