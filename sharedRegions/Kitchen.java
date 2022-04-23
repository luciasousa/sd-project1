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
    private boolean isNoteAvailable = false;
    private boolean preparationStarted;
    private boolean portionCollected;

    public Kitchen(GeneralRepository repos)
    {
        firstCourse = true;
        this.repos = repos;
    }

    /**
     *    Operation set first course
     *
     *    Called by the chef to set first course
     *    @param b boolean value setting the first course
     *    
     */
    public void setFirstCourse(boolean b) { firstCourse = b; }

    /**
     *    Operation get first course
     *
     *    Called by the chef to get first course
     *    @return boolean value for the first course
     *    
     */
    public boolean getFirstCourse() { return firstCourse; }

    /**
     *    Operation watch the news
     *
     *    Called by the chef to watch the news
     *    waits until note is available
     *    
     */
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
    
    /**
     *    Operation hand note to the chef
     *
     *    Called by the waiter to hand note to the chef
     *    signals chef that note is available
     *    waiter waits until chef starts preparation
     *    
     */
    public synchronized void handTheNoteToChef() 
    {
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.PCODR);
        int state = waiter.getWaiterState();
        repos.setWaiterState(state);
        System.out.println("waiter hands the note to chef");
        this.numberOfCoursesToDeliver = Constants.M;
        this.numberOfPortionsToDeliver = Constants.N;
        isNoteAvailable = true;
        notifyAll();
        while(!preparationStarted)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *    Operation chef wait for collection
     *
     *    Called by the waiter for chef to wait for collection of the portion
     *    
     */
    public synchronized void chefWaitForCollection() 
    {
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.DLVPT);
        int state = chef.getChefState();
        repos.setChefState(state);
        System.out.println("chef waits for waiter to collect portion");
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

    /**
     *    Operation portion has been collected
     *
     *    Called by the waiter to inform that portion was collected
     *    signals chef that portion was collected
     *    
     */
    public synchronized void portionHasBeenCollected()
    {
        portionCollected = true;
        notifyAll();
    }

    /**
     *    Operation start preparation
     *
     *    Called by the chef to start preparation
     *    signals waiter that preperation was started
     *    
     */    
    public synchronized void startPreparation() 
    {
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.PRPCS);
        int state = chef.getChefState();
        repos.setChefState(state);
        numberOfCoursesToDeliver--;
        System.out.printf("chef starts preparation\n");
        preparationStarted = true;
        notifyAll();
    }

    /**
     *    Operation proceedToPresentation
     *
     *    Called by the chef to proceed to presentation
     *    
     */
    public synchronized void proceedToPresentation() 
    {
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.DSHPT);
        int state = chef.getChefState();
        repos.setChefState(state);
        numberOfPortionsToDeliver--;
        System.out.printf("chef proceeds to presentation, course %d, portion %d\n",numberOfCoursesToDeliver,numberOfPortionsToDeliver);
    }

    /**
     *    Operation have all portions been delivered
     *
     *    Called by the chef to check if all portions were delivered
     *    
     */
    public synchronized boolean haveAllPortionsBeenDelivered() 
    {
        if(numberOfPortionsToDeliver == 0) return true; else return false;
    }

    /**
     *    Operation has the order been completed
     *
     *    Called by the chef to check if the order was completed
     *    
     */
    public synchronized boolean hasTheOrderBeenCompleted() 
    {
        if(numberOfCoursesToDeliver == 0) return true; else return false;
    }

    /**
     *    Operation have next portion ready
     *
     *    Called by the chef to have next portion ready
     *    
     */
    public synchronized void haveNextPortionReady() 
    {
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.DSHPT);
        int state = chef.getChefState();
        repos.setChefState(state);
        numberOfPortionsToDeliver--;
        System.out.printf("chef have next portion ready course %d, portion %d\n",numberOfCoursesToDeliver,numberOfPortionsToDeliver);
    }

    /**
     *    Operation continue preparation
     *
     *    Called by the chef to continue preparation
     *    
     */
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

    /**
     *    Operation clean up
     *
     *    Called by the chef to clean up
     *    
     */
    public synchronized void cleanUp() 
    {
        System.out.println("chef cleans up");
        Chef chef = (Chef) Thread.currentThread();
        chef.setChefState(ChefStates.CLSSV);
        int state = chef.getChefState();
        repos.setChefState(state);
    }
}
