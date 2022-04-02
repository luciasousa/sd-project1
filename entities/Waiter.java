package entities;
import sharedRegions.*;

public class Waiter {

    //identify the waiter
    //the state the waiter is in

    public int waiterState;

    public Waiter(){
        //initial state
        waiterState = WaiterStates.APPST;
    }

    public void setWaiterState(int state){
        waiterState = state;
    }

    public int getWaiterState(){
        return waiterState;
    }

    //fucntion run - thread 
}
