package sharedRegions;
import libraries.*;
import main.Constants;
import commInfra.*;
import entities.*;

/**
 *    BAR
 *
 *    It is responsible for the the synchronization of the Students and Waiter
 *    is implemented as an implicit monitor.
 *    
 *    There are internal synchronization points: 
 *    blocking point for the Waiter, where he waits for the Student to signal
 *    blocking point for the Waiter, where he waits for the Chef to signal
 */

public class Bar {
    //número de serviços pendentes
    private int numberOfPendingServiceRequests;
    //fila com os serviços pendentes
    private MemFIFO<Request> pendingServiceRequests;
    //número de estudantes no restaurante
    private int numberOfStudentsInRestaurant;
    private boolean orderDescribed;
    private Table table;
    private final GeneralRepository repos;

    public Bar(GeneralRepository repos, Table table){
        //initialize queue requests
        try {
            pendingServiceRequests = new MemFIFO(new Request[Constants.N]);
        } catch (MemException e) {
            pendingServiceRequests = null;
            e.printStackTrace();
        }
        //initialize other variables
        numberOfPendingServiceRequests = 0;
        numberOfStudentsInRestaurant = 0;
        orderDescribed = false;
        this.repos = repos;
        this.table = table;
    }

    public int enter() {
        synchronized(this){
            numberOfStudentsInRestaurant++;
            Student student = (Student) Thread.currentThread();
            Request r = new Request(student.getStudentID(), 'c');
            try {
                pendingServiceRequests.write(r);
            } catch (MemException e) {
                e.printStackTrace();
            }
        }
        notify();
        table.takeASeat();
        return numberOfStudentsInRestaurant;
    }

    public synchronized void saluteTheClient() {
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.PRSMN);
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
        Student student = (Student) Thread.currentThread();
        Request r = new Request(student.getStudentID(),'o');
        try {
            pendingServiceRequests.write(r);
        } catch (MemException e) {
            e.printStackTrace();
        }
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
        Request request = null;
        try {
            request = pendingServiceRequests.read();
        } catch (MemException e) {
            e.printStackTrace();
        }
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

    
    
}


