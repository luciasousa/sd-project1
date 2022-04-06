package sharedRegions;
import libraries.*;
import java.util.*;
import entities.*;

/*
BAR

*/

public class Bar {
    //número de serviços pendentes
    private int numberOfPendingServiceRequests;
    //fila com os serviços pendentes
    private Queue<Request> pendingServiceRequests;
    //número de estudantes no restaurant    e
    private int numberOfStudentsInRestaurant;

    public void saluteTheClient() {}

    public void returnToBar() {}

    public void getThePad() {}
    
    public char lookAround() {
        return '0';
    }
    
    public void deliverPortion() {}

    public boolean haveAllClientsBeenServed() {
        return false;
    }

    public void prepareTheBill() {}

    public void presentTheBill() {}

    public void sayGoodbye() {}

    public void alertTheWaiter() {}
    
}


