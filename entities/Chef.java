package entities;

import sharedRegions.*;

public class Chef extends Thread {
    
    //identify the chef
    //the state the chef is in

    private int chefState;
    private Kitchen kitchen;
    private boolean firstCourse = true;

    public Chef(Kitchen kitchen){
        //initial state
        chefState = ChefStates.WAFOR;
        this.kitchen = kitchen;
    }

    public void setChefState(int state){
        chefState = state;
    }

    public int getChefState(){
        return chefState;
    }

    //fucntion run - thread 
    public void run() {
        kitchen.watchTheNews();

        kitchen.startPreparation();

        do {
            
            if(!firstCourse) kitchen.continuePreparation(); else firstCourse = false;

            kitchen.proceedToPresentation();

            kitchen.alertTheWaiter();

            while(!kitchen.haveAllPortionsBeenDelivered()) {
                
                kitchen.haveNextPortionReady();

                kitchen.alertTheWaiter();
            }

        } while(!kitchen.hasTheOrderBeenCompleted());

        kitchen.cleanUp();
    }
}
