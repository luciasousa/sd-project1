package entities;
import sharedRegions.*;

public class Waiter {

    //identify the waiter
    //the state the waiter is in

    private int waiterState;
    private Bar bar;
    private Kitchen kitchen;

    public Waiter(Bar bar, Kitchen kitchen){
        //initial state
        waiterState = WaiterStates.APPST;
        this.bar = bar;
        this.kitchen = kitchen;
    }

    public void setWaiterState(int state){
        waiterState = state;
    }

    public int getWaiterState(){
        return waiterState;
    }

    //fucntion run - thread 
    public void run() {

        char c = bar.lookAround();

        switch(c) {

            case 'c':

                bar.saluteTheClient();

                bar.returnToBar();

                bar.lookAround();
            
            case 'o':

                bar.getThePad();

                kitchen.handTheNoteToChef(3,7);

                bar.returnToBar();

                bar.lookAround();
            
            case 'p':

                while(!bar.haveAllClientsBeenServed()) {

                    kitchen.collectPortion();

                    bar.deliverPortion();

                }

                bar.returnToBar();

                bar.lookAround();

            case 'b':

                bar.prepareTheBill();

                bar.presentTheBill();

                bar.returnToBar();

                bar.lookAround();
                
            case 'g':

                bar.sayGoodbye();
                
        }

    }
}
