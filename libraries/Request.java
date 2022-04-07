package libraries;

public class Request {
    private int requestID;
    private char requestType;

    public Request(int id,char type){
        this.requestID = id;
        this.requestType = type;
    }

    public int getRequestID(){

        return requestID;
    }

    public char getRequestType(){

        return requestType;
    }
}
