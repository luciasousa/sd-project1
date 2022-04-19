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
    private boolean isPortionDelivered = false;
    private boolean preparationStarted;

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
        System.out.println("chef starts preparation");
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.PRPCS);
        int state = chef.getChefState();
        repos.setChefState(state);
        numberOfCoursesToDeliver--;
        
        //Acorda waiter que está à espera em handTheNoteToChef
        preparationStarted = true;
        notifyAll();
    }

    public synchronized void proceedToPresentation() 
    {
        System.out.println("chef proceeds to presentation");
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.DSHPT);
        int state = chef.getChefState();
        repos.setChefState(state);
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
        System.out.println("chef have next portion ready");
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.DSHPT);
        int state = chef.getChefState();
        repos.setChefState(state);
        numberOfPortionsToDeliver--;
    }

    public synchronized void continuePreparation() 
    {
        System.out.println("chef continues preparation");
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.PRPCS);
        int state = chef.getChefState();
        repos.setChefState(state);
        numberOfCoursesToDeliver--;
        numberOfPortionsToDeliver = Constants.N;
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
