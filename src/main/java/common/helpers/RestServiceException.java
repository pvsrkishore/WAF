package common.helpers;

import com.sun.jersey.api.client.ClientResponse;


public class RestServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ClientResponse response = null;
	
	public RestServiceException(){
		super("unexpected error occured while invoking rest service");
	}
	
	public RestServiceException(String message ){
		super(message);
	}
	
	public RestServiceException(String message, Throwable e){
		super(message, e);
	} 
	public RestServiceException( Throwable e){
		super(e);
	} 
	
	
	public RestServiceException(String message, Throwable e, ClientResponse resp){
		super(message, e);
		this.setResponse(resp);
	} 
	
	public RestServiceException(String message,  ClientResponse resp){
		super(message);
		this.setResponse(resp);
	}

	public ClientResponse getResponse() {
		return response;
	}

	public void setResponse(ClientResponse response) {
		this.response = response;
	}
}
