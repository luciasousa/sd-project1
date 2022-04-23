package entities;
import libraries.Request;
import sharedRegions.*;

public class Waiter extends Thread 
{
    private int waiterState;
    private Bar bar;
    private Kitchen kitchen;
    private Table table;

    public Waiter(int waiterState, Bar bar, Kitchen kitchen, Table table)
    {
        this.waiterState = waiterState;
        this.bar = bar;
        this.kitchen = kitchen;
        this.table = table;
    }

    public void setWaiterState(int state)
    {
        waiterState = state;
    }

    public int getWaiterState()
    {
        return waiterState;
    }

    //function run - thread 
    public void run() 
    {
        System.out.println("waiter thread");
        while(true)
        {
            Request r = bar.lookAround();
            switch(r.getRequestType()) 
            {
                case 'c': //client arriving
                    table.saluteTheClient(r.getRequestID());
                    bar.returnToBar();
                    break;
                
                case 'o': //order ready to be collected
                    table.getThePad();
                    kitchen.handTheNoteToChef();
                    bar.returnToBar();
                    break;
                
                case 'p': //portion ready to be collected
                    if(!table.haveAllClientsBeenServed())
                    {
                        bar.collectPortion();
                        table.deliverPortion();
                        bar.returnToBar();
                    }
                    break;

                case 'b': //bill presentation
                    bar.prepareTheBill();
                    table.presentTheBill();
                    bar.returnToBar();
                    break;
                    
                case 'g': //say goodbye to students
                    int numberOfStudentsInRestaurant = bar.sayGoodbye(r.getRequestID());
                    if(numberOfStudentsInRestaurant == 0) return;
            }
        }
    }
}
