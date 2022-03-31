package libraries;

public class Request {
    private int requestID;
    private String requestType;

    public Request(int id,String type){
        this.requestID = id;
        this.requestType = type;
    }

    public int getRequestID(){

        return requestID;
    }

    public String getRequestType(){

        return requestType;
    }
}
