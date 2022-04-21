package sharedRegions;

import entities.*;
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
    private int numberOfCoursesToDeliver;
    private int numberOfPortionsToDeliver;
    private final GeneralRepository repos;
    private boolean firstCourse;

    //flags
    private boolean isNoteAvailable = false;
    private boolean preparationStarted;
    private boolean portionCollected;

    public Kitchen(GeneralRepository repos)
    {
        firstCourse = true;
        this.repos = repos;
    }

    public void setFirstCourse(boolean b) { firstCourse = b; }

    public boolean getFirstCourse() { return firstCourse; }

    public synchronized void watchTheNews() 
    {
        Chef chef = (Chef) Thread.currentThread();

        if(chef.getChefState() != ChefStates.WAFOR) 
        {
            chef.setChefState(ChefStates.WAFOR);
            int state = chef.getChefState();
            repos.setChefState(state);
        }
        System.out.println("chef watches the news");
        while(!isNoteAvailable)
        {
            try{
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("chef has note");
    }
    
    public synchronized void handTheNoteToChef() 
    {
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.PCODR);
        int state = waiter.getWaiterState();
        repos.setWaiterState(state);
        System.out.println("waiter hands the note to chef");
        this.numberOfCoursesToDeliver = Constants.M;
        this.numberOfPortionsToDeliver = Constants.N;
        
        //acorda chefe que está em watchTheNews
        isNoteAvailable = true;
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
        int state = chef.getChefState();
        repos.setChefState(state);
        System.out.println("chef waits for waiter to collect portion");
        
        //chef espera pela entrega do waiter
        while(!portionCollected)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        portionCollected = false;
    }

    //função chamada pelo Bar em collectPortion
    public synchronized void portionHasBeenCollected()
    {
        portionCollected = true;
        notifyAll();
    }

    public synchronized void startPreparation() 
    {
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.PRPCS);
        int state = chef.getChefState();
        repos.setChefState(state);
        numberOfCoursesToDeliver--;
        System.out.printf("chef starts preparation\n");
        
        //Acorda waiter que está à espera em handTheNoteToChef
        preparationStarted = true;
        notifyAll();
    }

    public synchronized void proceedToPresentation() 
    {
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.DSHPT);
        int state = chef.getChefState();
        repos.setChefState(state);
        numberOfPortionsToDeliver--;
        System.out.printf("chef proceeds to presentation, course %d, portion %d\n",numberOfCoursesToDeliver,numberOfPortionsToDeliver);
    }

    public synchronized boolean haveAllPortionsBeenDelivered() 
    {
        if(numberOfPortionsToDeliver == 0) return true; else return false;
    }

    public synchronized boolean hasTheOrderBeenCompleted() 
    {
        if(numberOfCoursesToDeliver == 0) return true; else return false;
    }

    public synchronized void haveNextPortionReady() 
    {
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.DSHPT);
        int state = chef.getChefState();
        repos.setChefState(state);
        numberOfPortionsToDeliver--;
        System.out.printf("chef have next portion ready course %d, portion %d\n",numberOfCoursesToDeliver,numberOfPortionsToDeliver);
    }

    public synchronized void continuePreparation() 
    {
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.PRPCS);
        int state = chef.getChefState();
        repos.setChefState(state);
        numberOfCoursesToDeliver--;
        numberOfPortionsToDeliver = Constants.N;
        System.out.printf("chef continues preparation course %d, portion %d\n",numberOfCoursesToDeliver,numberOfPortionsToDeliver);
    }

    public synchronized void cleanUp() 
    {
        System.out.println("chef cleans up");
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.CLSSV);
        int state = chef.getChefState();
        repos.setChefState(state);
    }
}
