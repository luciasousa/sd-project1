package entities;
import sharedRegions.*;

public class Waiter extends Thread{

    //identify the waiter
    //the state the waiter is in

    private int waiterState;
    private Bar bar;
    private Kitchen kitchen;
    private int numberOfStudentsInRestaurant;
    private int numberOfCoursesToDeliver;

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

        String s = bar.lookAround();

        switch(s) {

            case "c": //client arriving

                bar.saluteTheClient();

                bar.returnToBar();

                bar.lookAround();
            
            case "o": //order ready to be collected

                bar.getThePad();

                kitchen.handTheNoteToChef(numberOfCoursesToDeliver, numberOfStudentsInRestaurant);

                bar.returnToBar();

                bar.lookAround();
            
            case "p": //portion ready to be collected

                while(!bar.haveAllClientsBeenServed()) {

                    kitchen.collectPortion();

                    bar.deliverPortion();

                }

                bar.returnToBar();

                bar.lookAround();

            case "b": //bill presentation

                bar.prepareTheBill();

                bar.presentTheBill();

                bar.returnToBar();

                bar.lookAround();
                
            case "g": //say goodbye to students

                bar.sayGoodbye();
                
        }

    }
}
