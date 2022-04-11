package sharedRegions;

import commInfra.MemException;
import commInfra.MemFIFO;
import entities.*;
import main.*;
//import genclass.*;

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
    //private int numberOfStudentsInRestaurant;
    //with the IDs of the students in order by arrival
    private MemFIFO<Integer> studentsInTableQueue;
    private int numberOfCourse;
    private int numberOfPortion;
    private int numberOfStudentsInRestaurant;

    public GeneralRepository(String logFileName){
        this.logFileName = logFileName;
        chefState = ChefStates.WAFOR;
        waiterState = WaiterStates.APPST;
        for(int i = 0; i < Constants.N; i++){
            studentState[i] = StudentStates.GGTRT;
        }

        MemFIFO<Integer> studentsInTableQueue = null;
        try {
            studentsInTableQueue = new  MemFIFO(new Integer[Constants.N]);
        } catch (MemException e) {
            e.printStackTrace();
        }
        numberOfStudentsInRestaurant = 0;
        numberOfCourse=0;
        numberOfPortion=0;

        reportInitialStatus();
    }

    private void reportInitialStatus() {
        TextFile log = new TextFile();
        if (!log.openForWriting (".", logFileName)){
            GenericIO.writelnString ("The operation of creating the file " + logFileName + " failed!");
            System.exit (1);
        }
        log.writelnString ("The Restaurant - Description of the internal state");
        log.writelnString (" Chef   Waiter  Stu0  Stu1    Stu2   Stu3   Stu4   Stu5  Stu6    NCourse   NPortion                     Table       ");
        log.writelnString ("State   State  State  State  State  State  State  State  State                        Seat0 Seat1 Seat2 Seat3 Seat4 Seat5 Seat6");
        if (!log.close ()){
            GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
            System.exit (1);
        }
        reportStatus ();
    }

    private void reportStatus() {
        TextFile log = new TextFile ();                      // instantiation of a text file handler

        String lineStatus = "";                              // state line to be printed

        if (!log.openForAppending (".", logFileName)){ 
            GenericIO.writelnString ("The operation of opening for appending the file " + logFileName + " failed!");
            System.exit (1);
        }

        switch (chefState){
            case ChefStates.WAFOR: lineStatus += "WAFOR ";
                                                break;
            case ChefStates.PRPCS: lineStatus += "PRPCS ";
                                                break;
            case ChefStates.DSHPT: lineStatus += "DSHPT ";
                                                break;
            case ChefStates.DLVPT: lineStatus += "DLVPT ";
                                                break;
            case ChefStates.CLSSV: lineStatus += "CLSSV ";
                                                break;
        }
   
        switch (waiterState){
            case WaiterStates.APPST: lineStatus += "APPST ";
                                                break;
            case WaiterStates.PRSMN: lineStatus += "PRSMN ";
                                                break;
            case WaiterStates.TKODR: lineStatus += "TKODR ";
                                                break;
            case WaiterStates.PCODR: lineStatus += "PCODR ";
                                                break;      
            case WaiterStates.WTFPT: lineStatus += "WTFPT ";
                                                break;
            case WaiterStates.PRCBL: lineStatus += "PRCBL ";
                                                break;
            case WaiterStates.RECPM: lineStatus += "RECPM ";
                                                break;                              
        }
   
        for (int i = 0; i < Constants.N; i++)
            switch (studentState[i]){
                case StudentStates.GGTRT:  lineStatus += "GGTRT ";
                                                break;
                case StudentStates.TKSTT:  lineStatus += "TKSTT ";
                                                break;
                case StudentStates.SELCS:  lineStatus += "SELCS ";
                                                break;
                case StudentStates.OGODR:  lineStatus += "OGODR ";
                                                break;
                case StudentStates.CHTWC:  lineStatus += "CHTWC ";
                                                break;
                case StudentStates.EJYML:  lineStatus += "EJYML ";
                                                break;
                case StudentStates.PYTBL:  lineStatus += "PYTBL ";
                                                break;
                case StudentStates.GGHOM:  lineStatus += "GGHOM ";
                                                break;
            }
       
        lineStatus += " "+String.format("%2d", numberOfCourse)+"  "+String.format("%2d", numberOfPortion);
        for(int i =0; i<Constants.N; i++){
            lineStatus += " "+ String.format("%2d",StudentsInTableQueue.read());
        }
        log.writelnString (lineStatus);
        if (!log.close ()){ 
            GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
            System.exit (1);
        }
    }

    
}
