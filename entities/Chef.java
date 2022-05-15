package entities;
import sharedRegions.*;


/**
 *   Chef thread.
 *
 *   Used to simulate the Chef life cycle.
 *   Static solution.
 */
public class Chef extends Thread 
{
    /**
     *  Chef state.
     */
    private int chefState;

    /**
     *  Reference to the kitchen.
     */
    private Kitchen kitchen;

    /**
     *  Reference to the bar.
     */
    private Bar bar;

     /**
     *   Instantiation of a Chef thread.
     *
     *     @param chefState state of the chef
     *     @param kitchen reference to the kitchen
     *     @param bar reference to the bar
     */
    public Chef(int chefState, Kitchen kitchen, Bar bar)
    {
        this.chefState = chefState;
        this.kitchen = kitchen;
        this.bar = bar;
    }

    /**
     *   Set chef state.
     *
     *     @param state chef state
     */
    public void setChefState(int state)
    {
        chefState = state;
    }

    /**
     *   Get chef state.
     *
     *   @return chef state
     */
    public int getChefState()
    {
        return chefState;
    }

    /**
     *   Life cycle of the chef.
     *   
     *   Starts at the state whatch the news (waiting for the note)
     *   Ends when the order is complete.
     */
    public void run() 
    {
        //System.out.println("chef thread");
        kitchen.watchTheNews();
        kitchen.startPreparation();
        do 
        {
            if(!kitchen.getFirstCourse()) kitchen.continuePreparation(); else kitchen.setFirstCourse(false);
            kitchen.proceedToPresentation();
            bar.alertTheWaiter();

            while(!kitchen.haveAllPortionsBeenDelivered()) {
                kitchen.haveNextPortionReady();
                bar.alertTheWaiter();
            }

        } while(!kitchen.hasTheOrderBeenCompleted());

        kitchen.cleanUp();
    }
}
