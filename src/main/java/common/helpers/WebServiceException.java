package common.helpers;

import com.ning.http.client.Response;

public class WebServiceException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Response response;

    public WebServiceException(Response response) {
        super();
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }


}
