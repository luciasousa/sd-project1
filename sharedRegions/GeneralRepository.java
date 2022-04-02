package sharedRegions;

import entities.*;
import main.*;

/*
General Repository.
Responsible to keep the visible internal state of the problem.
Prints in the logging file.
Implemented as an implicit monitor.
All public methods are executed in mutual exclusion.
There are no internal synchronization points.
*/

public class GeneralRepository {

    //name of the logging file
    private final String logFileName;

    //state of the chef
    private int chefState;

    //state of the waiter
    private int waiterState;

    //states of the students
    private int[] studentState;

    //variables needed

    public GeneralRepository(String logFileName){
        this.logFileName = logFileName;
        chefState = ChefStates.WAFOR;
        waiterState = WaiterStates.APPST;
        for(int i = 0; i < Constants.N; i++){
            studentState[i] = StudentStates.GGTRT;
        }
    }
}
