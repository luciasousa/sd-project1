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
    private Table table;
    private final GeneralRepository repos;

    //flags
    private boolean orderDescribed = false;

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

    public int enter() {
        int studentID;
        synchronized(this)
        {
            numberOfStudentsInRestaurant += 1;
            Student student = (Student) Thread.currentThread();
            studentID = student.getStudentID();
            Request r = new Request(studentID, 'c');
            numberOfPendingServiceRequests += 1;
            try {
                pendingServiceRequests.write(r);
            } catch (MemException e) {
                e.printStackTrace();
            }
            //acorda waiter preso em lookAround
            notifyAll();
        }
        
        table.takeASeat(studentID);
        return numberOfStudentsInRestaurant;
    }

    public synchronized void returnToBar() {
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.APPST);
    }

    public void callWaiter() {
        //estudante adiciona pedido à requests queue e acorda o waiter
        synchronized(this) 
        {
            Student student = (Student)Thread.currentThread();
            int studentID = student.getStudentID();
            Request r = new Request(studentID, 'o');
            numberOfPendingServiceRequests++;
            try {
                pendingServiceRequests.write(r);
            } catch (MemException e) {
                e.printStackTrace();
            }
            //acordar o waiter preso em lookAround
            notifyAll();
        }
        table.waitForPad();
    }

    public void getThePad() {
        synchronized(this) 
        {
            Waiter waiter = (Waiter) Thread.currentThread();
            waiter.setWaiterState(WaiterStates.TKODR);
            //acorda o estudante
            notifyAll();
            //espera que ele descreva o pedido
            while(!orderDescribed)
            {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        table.setIfWaiterHasPad(true);
    }

    public synchronized void setOrderDescribed(boolean b) { orderDescribed = b; }

    public synchronized void prepareTheBill() {
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.PRCBL);
    }

    public synchronized void sayGoodbye() {}

    public synchronized void alertTheWaiter() {
        notifyAll();
    }

    public synchronized void collectPortion() {
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.WTFPT);
    }

    public synchronized void hasEverybodyFinished() {
        //retorna true quando o número de porções comidas é N
    }
}


