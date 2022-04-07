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

        //acordar 1º estudante
        notifyAll();

        
        while(!orderDescribed){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
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
        numberOfPendingServiceRequests++;
        Student student = (Student)Thread.currentThread();
        int studentID = student.getStudentID();
        Request r = new Request(studentID, 'o');
        pendingServiceRequests.add(r);
        
    }

    public synchronized void prepareTheBill() {
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.PRCBL);
    }

    public synchronized void sayGoodbye() {}

    public synchronized void alertTheWaiter() {
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.DLVPT);
    }
    
}


