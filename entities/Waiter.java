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
        while(true)
        {
            char s = bar.lookAround();

            switch(s) 
            {
                case 'c': //client arriving
                    table.saluteTheClient();
                    bar.returnToBar();
                
                case 'o': //order ready to be collected
                    bar.getThePad();
                    kitchen.handTheNoteToChef(3,7);
                    bar.returnToBar();
                
                case 'p': //portion ready to be collected
                    while(!table.haveAllClientsBeenServed()) {
                        bar.collectPortion();
                        kitchen.collectPortion();
                        table.deliverPortion();
                    }
                    bar.returnToBar();

                case 'b': //bill presentation
                    bar.prepareTheBill();
                    table.presentTheBill();
                    bar.returnToBar();
                    
                case 'g': //say goodbye to students
                    bar.sayGoodbye();
                    System.exit(0);
            }
        }
    }
}
