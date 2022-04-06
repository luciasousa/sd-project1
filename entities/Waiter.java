package entities;
import sharedRegions.*;

public class Waiter extends Thread {

    //identify the waiter
    //the state the waiter is in

    private int waiterState;
    private Bar bar;
    private Kitchen kitchen;
    private Table table;

    public Waiter(Bar bar, Kitchen kitchen, Table table){
        //initial state
        waiterState = WaiterStates.APPST;
        this.bar = bar;
        this.kitchen = kitchen;
        this.table = table;
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

                table.saluteTheClient();

                bar.returnToBar();
            
            case 'o':

                bar.getThePad();

                kitchen.handTheNoteToChef(3,7);

                bar.returnToBar();
            
            case 'p':

                while(!bar.haveAllClientsBeenServed()) {

                    kitchen.collectPortion();

                    table.deliverPortion();

                }

                bar.returnToBar();


            case 'b':

                bar.prepareTheBill();

                table.presentTheBill();

                bar.returnToBar();
                
            case 'g':

                bar.sayGoodbye();
                
        }

    }
}
