package commInfra;

/**
 *   Request class.
 *
 *   Used to represnt a request that is made to the waiter.
 * 
 */
public class Request {
    /**
     *  Request id.
     */
    private int requestID;

    /**
     *  Request type.
     */
    private char requestType;

    /**
     *   Instantiation of a Request.
     *
     *     @param id request id
     *     @param type request type
     */
    public Request(int id,char type){
        this.requestID = id;
        this.requestType = type;
    }

    /**
     *   Get request ID.
     *
     *     @return request id
     */
    public int getRequestID(){

        return requestID;
    }

    /**
     *   Get request type.
     *
     *     @return request type
     */
    public char getRequestType(){

        return requestType;
    }
}
