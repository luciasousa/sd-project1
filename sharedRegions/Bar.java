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

public class Bar 
{
    //número de serviços pendentes
    private int numberOfPendingServiceRequests=0;
    //fila com os serviços pendentes
    private MemFIFO<Request> pendingServiceRequests;
    private MemFIFO<Integer> arrivalQueue;

    //número de estudantes no restaurante
    private int numberOfStudentsInRestaurant;
    private Table table;
    private Kitchen kitchen;
    private final GeneralRepository repos;

    private int studentsArrival[] = new int [Constants.N];
    private int portionsCollected;
    private boolean firstStudent;

    public Bar(GeneralRepository repos, Table table, Kitchen kitchen)
    {
        //initialize queue requests
        try {
            pendingServiceRequests = new MemFIFO(new Request[Constants.N+1]);
        } catch (MemException e) {
            pendingServiceRequests = null;
            e.printStackTrace();
        }
        //initialize other variables
        try {
            arrivalQueue = new MemFIFO(new Integer[Constants.N]);
        } catch (MemException e) {
            arrivalQueue = null;
            e.printStackTrace();
        }
        this.repos = repos;
        this.table = table;
        this.kitchen = kitchen;
    }

    public synchronized Request lookAround() 
    {
        Waiter waiter = (Waiter) Thread.currentThread();
        if(waiter.getWaiterState() != WaiterStates.APPST) {
            waiter.setWaiterState(WaiterStates.APPST);
        }
        System.out.println("waiter looking");

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
        System.out.print(request.getRequestID());
        System.out.println(" - " + request.getRequestType());
        numberOfPendingServiceRequests--;
        return request;
    }

    public int[] enter() 
    {
        int studentID;
        synchronized(this)
        {
            //não podemos usar variável do numero de estudantes para operar sobre o primeiro
            
            //if(numberOfStudentsInRestaurant==0) firstStudent=true; else firstStudent=false;
            Student student = (Student) Thread.currentThread();
            studentID = student.getStudentID();
            System.out.printf("student %d enters\n", studentID);
            //add student in order to the queue
            try {
                arrivalQueue.write(studentID);
            } catch (MemException e1) {
                e1.printStackTrace();
            }
            studentsArrival[numberOfStudentsInRestaurant] = studentID;
            numberOfStudentsInRestaurant++;
            Request r = new Request(studentID, 'c');
            numberOfPendingServiceRequests++;
            try {
                pendingServiceRequests.write(r);
            } catch (MemException e) {
                e.printStackTrace();
            }
            //acorda waiter preso em lookAround
            notifyAll();
        }
        table.takeASeat();
        //retorna a posição de chegada de cada estudante
        return studentsArrival;
    }

    public synchronized MemFIFO<Integer> getArrivalQueue(){
        return arrivalQueue;
    }

    public synchronized void returnToBar() 
    {
        System.out.println("waiter is returning to bar");
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.APPST);
    }

    public void callWaiter() 
    {
        //estudante adiciona pedido à requests queue e acorda o waiter
        System.out.println("waiter was called");
        synchronized(this) 
        {
            Student student = (Student)Thread.currentThread();
            int studentID = student.getStudentID();
            Request r = new Request(studentID, 'o');
            numberOfPendingServiceRequests += 1;
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

    public void alertTheWaiter() 
    {
        synchronized(this) 
        {
            //chef's ID is equal to the number of students
            Request r = new Request(Constants.N, 'p');
            numberOfPendingServiceRequests += 1;
            try {
                pendingServiceRequests.write(r);
            } catch (MemException e) {
                e.printStackTrace();
            }
            
            //wake up the waiter stuck in lookAround 
            notifyAll();
            System.out.println("chef alerts the waiter");
        }
        kitchen.chefWaitForCollection();
    }

    public void collectPortion() 
    {
        System.out.println("waiter is collecting portion");
        kitchen.prepareNextPortion();
        synchronized(this) 
        {
            Waiter waiter = (Waiter) Thread.currentThread();
            waiter.setWaiterState(WaiterStates.WTFPT);
        }
    }

    //signal the waiter quando os estudantes acabaram de comer
    public void signalTheWaiter() 
    {
        //estudante adiciona pedido à requests queue e acorda o waiter
        synchronized(this) 
        {
            System.out.println("waiter has been signaled");
            Student student = (Student)Thread.currentThread();
            int studentID = student.getStudentID();
            //bill presentation
            Request r = new Request(studentID, 'b');
            numberOfPendingServiceRequests += 1;
            try {
                pendingServiceRequests.write(r);
            } catch (MemException e) {
                e.printStackTrace();
            }
            //acordar o waiter preso em lookAround
            notifyAll();
        }
        table.allFinished();
    }

    public synchronized void prepareTheBill() 
    {
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.PRCBL);
    }

    public int exit() 
    {
        int studentID;
        synchronized(this)
        {
            Student student = (Student) Thread.currentThread();
            studentID = student.getStudentID();
            //say goodbye
            Request r = new Request(studentID, 'g');
            numberOfPendingServiceRequests += 1;
            try {
                pendingServiceRequests.write(r);
            } catch (MemException e) {
                e.printStackTrace();
            }
            //acorda waiter preso em lookAround
            notifyAll();
        }
        table.goingHome(studentID);
        //retorna a posição de chegada de cada estudante
        return numberOfStudentsInRestaurant;
    }


    public synchronized void sayGoodbye() 
    {
        numberOfStudentsInRestaurant -= 1;
        // transition occurs when the last student has left the restaurant
        while(numberOfStudentsInRestaurant != 0)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


