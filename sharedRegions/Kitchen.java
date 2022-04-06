package sharedRegions;

import java.util.Queue;

import entities.*;

/*
KITCHEN

*/

public class Kitchen {
    
    private boolean isNoteAvailable;
    private Queue<Integer> portionsQueue;
    private int numberOfCoursesToDeliver;
    private int numberOfPortionsToDeliver;
    private int numberOfStudentsInRestaurant;

    public synchronized void watchTheNews() {
        Chef chef = (Chef) Thread.currentThread();
        if(chef.getChefState() != ChefStates.WAFOR) {
            chef.setChefState(ChefStates.WAFOR);
        }

        while(isNoteAvailable) {
            try{
                wait();
            } catch(Exception e) {
                System.out.println("Thread was interrupted");
            }
        }
    }

    
    public synchronized void handTheNoteToChef(int numberOfCoursesToDeliver, int numberOfStudents) {
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.PCODR);
        isNoteAvailable = true;
        this.numberOfCoursesToDeliver = numberOfCoursesToDeliver;
        this.numberOfPortionsToDeliver = numberOfStudents;
        this.numberOfStudentsInRestaurant = numberOfStudents;
    }

    public synchronized void startPreparation() {
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.PRPCS);
        numberOfCoursesToDeliver--;
    }

    public synchronized void proceedToPresentation() {
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.DSHPT);
        portionsQueue.add(0);
        numberOfPortionsToDeliver--;
    }

    public synchronized boolean haveAllPortionsBeenDelivered() {
        if(numberOfPortionsToDeliver == 0) return true; else return false;
    }

    public synchronized boolean hasTheOrderBeenCompleted() {
        if(numberOfCoursesToDeliver == 0) return true; else return false;
    }

    public synchronized void haveNextPortionReady() {
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.DSHPT);
        portionsQueue.add(0);
        numberOfPortionsToDeliver--;
    }

    public synchronized void collectPortion() {
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.WTFPT);
    }

    public synchronized void continuePreparation() {
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.PRPCS);
        numberOfCoursesToDeliver--;
        numberOfPortionsToDeliver = numberOfStudentsInRestaurant;
    }

    public synchronized void cleanUp() {
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.CLSSV);
    }

}
