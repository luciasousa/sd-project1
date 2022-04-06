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
    //número de estudantes no restaurante
    private int numberOfStudentsInRestaurant;

    public synchronized void saluteTheClient() {
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.PRSMN);
        numberOfStudentsInRestaurant++;
    }

    public synchronized void returnToBar() {
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.APPST);
    }

    public synchronized void getThePad() {
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.TKODR);
        
    }
    
    public synchronized String lookAround() {
        Waiter waiter = (Waiter) Thread.currentThread();
        if(waiter.getWaiterState() != WaiterStates.APPST) {
            waiter.setWaiterState(WaiterStates.APPST);
        }

        while(numberOfPendingServiceRequests == 0) {
            try {
                wait();
            } catch (Exception e) {
                System.out.println("Thread interrupted");
            }
        }
        Request request = pendingServiceRequests.poll();
        return request.getRequestType();
    }
    
    public synchronized void deliverPortion() {}

    //possivelmente esta função será na Table
    public synchronized boolean haveAllClientsBeenServed() {
        return false;
    }

    public synchronized void prepareTheBill() {
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.PRCBL);
    }

    public synchronized void presentTheBill() {
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.RECPM);
    }

    public synchronized void sayGoodbye() {}

    public synchronized void alertTheWaiter() {
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.DLVPT);
    }
    
}


