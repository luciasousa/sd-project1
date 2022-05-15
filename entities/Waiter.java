package entities;
import commInfra.Request;
import sharedRegions.*;

/**
 *   Waiter thread.
 *
 *   Used to simulate the Waiter life cycle.
 *   Static solution.
 */
public class Waiter extends Thread 
{
    /**
     *  Waiter State.
     */
    private int waiterState;

    /**
     *  Reference to the bar.
     */
    private Bar bar;

    /**
     *  Reference to the kitchen.
     */
    private Kitchen kitchen;

    /**
     *  Reference to the table.
     */
    private Table table;

    /**
     *   Instantiation of a Waiter thread.
     *
     *     @param waiterState waiter state
     *     @param bar reference to the bar
     *     @param kitchen reference to the kicthen
     *     @param table reference to the table
     */
    public Waiter(int waiterState, Bar bar, Kitchen kitchen, Table table)
    {
        this.waiterState = waiterState;
        this.bar = bar;
        this.kitchen = kitchen;
        this.table = table;
    }

    /**
     *   Set waiter state.
     *
     *     @param state waiter state
     */
    public void setWaiterState(int state)
    {
        waiterState = state;
    }

    /**
     *   Get waiter state.
     *
     *     @return waiter state
     */
    public int getWaiterState()
    {
        return waiterState;
    }

    /**
     *   Life cycle of the waiter.
     *   
     *   Starts at the state appraising situation (waiting for the arrival of the students)
     *   Ends when all the N students exit the restuarant
     */
    public void run() 
    {
        //System.out.println("waiter thread");
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
