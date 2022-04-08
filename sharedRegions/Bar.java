package sharedRegions;
import libraries.*;
import main.Constants;

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
    private boolean orderDescribed;

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

        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }

    public synchronized void describeTheOrder() {
        //Request r = Request(Constants.N-1,'o');
        notify();
    }

    
    public synchronized char lookAround() {
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
        numberOfPendingServiceRequests--;
        return request.getRequestType();
    }

    //função da table, estudante acorda o waiter- waiter adiciona pedido à requests queue
    public synchronized void callWaiter() {
        /*numberOfPendingServiceRequests++;
        Student student = (Student)Thread.currentThread();
        int studentID = student.getStudentID();
        Request r = new Request(studentID, 'o');
        pendingServiceRequests.add(r);*/
        notify();
        
    }

    public synchronized void prepareTheBill() {
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.PRCBL);
    }

    public synchronized void sayGoodbye() {}

    public synchronized void alertTheWaiter() {
    
        notify();
    }

    public synchronized void collectPortion() {
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.WTFPT);

    }

    public synchronized void hasEverybodyFinished() {
        //retorna true quando o número de porções comidas é N
        

    }

    public void enter() {
        /*Waiter waiter = (Waiter) Thread.currentThread();
        Request r = new Request(Constants.N-1, 'c');
        pendingServiceRequests.add(r);*/
        notify();
    }
    
}


