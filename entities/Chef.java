package entities;

import sharedRegions.*;

public class Chef extends Thread 
{
    
    //identify the chef
    //the state the chef is in

    private int chefState;
    private Kitchen kitchen;
    private Bar bar;
    private boolean firstCourse = true;

    public Chef(Kitchen kitchen, Bar bar)
    {
        //initial state
        chefState = ChefStates.WAFOR;
        this.kitchen = kitchen;
        this.bar = bar;
    }

    public void setChefState(int state)
    {
        chefState = state;
    }

    public int getChefState()
    {
        return chefState;
    }

    //function run - thread 
    public void run() 
    {
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
