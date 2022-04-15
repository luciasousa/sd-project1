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
    //número de estudantes no restaurante
    private int numberOfStudentsInRestaurant=0;
    private Table table;
    private Kitchen kitchen;
    private final GeneralRepository repos;

    private int studentsArrival[] = new int [Constants.N];
    private boolean orderDescribed = false;
    private boolean haveAllPortionsBeenDelivered = false;
    private boolean firstStudent=false;

    public Bar(GeneralRepository repos, Table table, Kitchen kitchen)
    {
        //initialize queue requests
        try {
            pendingServiceRequests = new MemFIFO(new Request[Constants.N]);
        } catch (MemException e) {
            pendingServiceRequests = null;
            e.printStackTrace();
        }
        //initialize other variables
        
        this.repos = repos;
        this.table = table;
    }

    public synchronized char lookAround() 
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
        numberOfPendingServiceRequests--;
        return request.getRequestType();
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
            studentsArrival[numberOfStudentsInRestaurant] = studentID;
            numberOfStudentsInRestaurant++;
            System.out.println("student enters");
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
        table.takeASeat(studentID);
        //retorna a posição de chegada de cada estudante
        return studentsArrival;
    }

    public synchronized void returnToBar() 
    {
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

    public void getThePad() 
    {
        synchronized(this) 
        {
            Waiter waiter = (Waiter) Thread.currentThread();
            waiter.setWaiterState(WaiterStates.TKODR);
            System.out.printf("waiter get the pad, state: %d",waiter.getWaiterState());
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

    //public synchronized void setOrderDescribed(boolean b) { orderDescribed = b; }

    public void alertTheWaiter() 
    {
        synchronized(this) 
        {
            //chef's ID is Number of students + 1
            Request r = new Request(Constants.N + 1, 'p');
            numberOfPendingServiceRequests += 1;
            try {
                pendingServiceRequests.write(r);
            } catch (MemException e) {
                e.printStackTrace();
            }

            //wake up the waiter stuck in lookAround 
            notifyAll();
        }
        kitchen.chefWaitForCollection();
    }

    public void collectPortion() 
    {
        synchronized(this) 
        {
            Waiter waiter = (Waiter) Thread.currentThread();
            waiter.setWaiterState(WaiterStates.WTFPT);
            
            /*while(!haveAllPortionsBeenDelivered) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
        }
        kitchen.prepareNextPortion();
    }

    public synchronized void hasEverybodyFinished() 
    {
        //retorna true quando o número de porções comidas é N
    }

    //signal the waiter quando os estudantes acabaram de comer
    public void signalTheWaiter() 
    {
        //estudante adiciona pedido à requests queue e acorda o waiter
        synchronized(this) 
        {
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


    public synchronized void sayGoodbye() {
        numberOfStudentsInRestaurant--;
        // transition occurs when the last student has left the restaurant
        while(numberOfStudentsInRestaurant!=0){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


