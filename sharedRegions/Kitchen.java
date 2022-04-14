package sharedRegions;

import java.util.Queue;

import entities.*;
import libraries.Request;
import main.Constants;

/**
 *    KITCHEN
 *
 *    It is responsible for the the synchronization of the Chef and Waiter
 *    is implemented as an implicit monitor.
 *    
 *    There is one internal synchronization points: 
 *    a single blocking point for the Chef, where he waits for the Waiter to signal
 */

public class Kitchen 
{
    
    private Queue<Integer> portionsQueue;
    private int numberOfCoursesToDeliver;
    private int numberOfPortionsToDeliver;
    private int numberOfStudentsInRestaurant;
    private final GeneralRepository repos;
    private boolean firstCourse;

    //flags
    private boolean isNoteAvailable = false;
    private boolean isPortionDelivered = false;

    public Kitchen((GeneralRepository repos)
    {
        firstCourse = true;
        this.repos = repos;
    }

    public setFirstCourse(boolean b) { firstCourse = b; }

    public getFirstCourse() { return firstCourse; }

    public synchronized void watchTheNews() 
    {
        Chef chef = (Chef) Thread.currentThread();

        if(chef.getChefState() != ChefStates.WAFOR) 
        {
            chef.setChefState(ChefStates.WAFOR);
        }

        while(!isNoteAvailable)
        {
            try{
                wait();
            } catch(Exception e) {
                System.out.println("Thread was interrupted");
            }
        }
    }
    
    public synchronized void handTheNoteToChef() 
    {
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.PCODR);
        
        this.numberOfCoursesToDeliver = Constants.M;
        this.numberOfPortionsToDeliver = Constants.N;
        this.numberOfStudentsInRestaurant = Constants.N;
        isNoteAvailable = true;

        //acorda chefe que está em watchTheNews
        notifyAll();

        //waiter espera pelo chef
        while(!preparationStarted)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //função chamada pelo Bar em alertTheWaiter
    public synchronized void chefWaitForCollection() 
    {
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.DLVPT);

        //chef espera pela entrega do waiter
        while(!isPortionDelivered)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void startPreparation() 
    {
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.PRPCS);
        numberOfCoursesToDeliver--;
        
        //Acorda waiter que está à espera em handTheNoteToChef
        preparationStarted = true;
        notifyAll();
    }

    public synchronized void proceedToPresentation() 
    {
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.DSHPT);
        numberOfPortionsToDeliver--;
    }

    public synchronized boolean haveAllPortionsBeenDelivered() 
    {
        if(numberOfPortionsToDeliver == 0) return true; else return false;
    }

    public synchronized boolean hasTheOrderBeenCompleted() 
    {
        if(numberOfCoursesToDeliver == 0) return true; else return false;
    }

    //função chamada pelo Bar em collectPortion
    public synchronized void prepareNextPortion() 
    {
        isPortionDelivered = true;
        notifyAll();
    }

    public synchronized void haveNextPortionReady() 
    {
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.DSHPT);
        numberOfPortionsToDeliver--;
    }

    public synchronized void continuePreparation() 
    {
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.PRPCS);
        numberOfCoursesToDeliver--;
        numberOfPortionsToDeliver = Constants.N;
    }

    public synchronized void cleanUp() 
    {
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.CLSSV);
    }
}
