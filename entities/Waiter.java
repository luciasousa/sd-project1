package entities;
import sharedRegions.*;

public class Waiter {

    //identify the waiter
    //the state the waiter is in

    private int waiterState;
    private Bar bar;

    public Waiter(Bar bar){
        //initial state
        waiterState = WaiterStates.APPST;
        this.bar = bar;
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
            
            case 'o':
                bar.getThePad();
                bar.handTheNoteToChef();
                bar.returnToBar();
            
            case 'p':
                while(!bar.haveAllClientsBeenServed()) {
                    bar.collectPortion();
                    bar.deliverPortion();
                }
                bar.returnToBar();
        }

    }
}
