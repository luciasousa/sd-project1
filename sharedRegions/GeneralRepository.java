package sharedRegions;

import commInfra.MemException;
import commInfra.MemFIFO;
import entities.ChefStates;
import entities.StudentStates;
import entities.WaiterStates;
import main.Constants;
import genclass.GenericIO;
import genclass.TextFile;

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
    private int[] studentState = new int[Constants.N];

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
            case 0: lineStatus += " WAFOR ";
                                                break;
            case 1: lineStatus += " PRPCS ";
                                                break;
            case 2: lineStatus += " DSHPT ";
                                                break;
            case 3: lineStatus += " DLVPT ";
                                                break;
            case 4: lineStatus += " CLSSV ";
                                                break;
        }
   
        switch (waiterState){
            case 0: lineStatus += " APPST ";
                                                break;
            case 1: lineStatus += " PRSMN ";
                                                break;
            case 2: lineStatus += " TKODR ";
                                                break;
            case 3: lineStatus += " PCODR ";
                                                break;      
            case 4: lineStatus += " WTFPT ";
                                                break;
            case 5: lineStatus += " PRCBL ";
                                                break;
            case 6: lineStatus += " RECPM ";
                                                break;                              
        }
   
        for (int i = 0; i < Constants.N; i++)
            switch (studentState[i]){
                case 0:  lineStatus += " GGTRT ";
                                                break;
                case 1:  lineStatus += " TKSTT ";
                                                break;
                case 2:  lineStatus += " SELCS ";
                                                break;
                case 3:  lineStatus += " OGODR ";
                                                break;
                case 4:  lineStatus += " CHTWC ";
                                                break;
                case 5:  lineStatus += " EJYML ";
                                                break;
                case 6:  lineStatus += " PYTBL ";
                                                break;
                case 7:  lineStatus += " GGHOM ";
                                                break;
            }
       
        lineStatus += " "+String.format("%7d", numberOfCourse)+"  "+String.format("%7d", numberOfPortion);

        /*for(int i =0; i<Constants.N; i++){
            try {
                studentsInTableQueue.write(i);
            } catch (MemException e) {
                e.printStackTrace();
            }
        }

        for(int i =0; i<Constants.N; i++){
            try {
                lineStatus += " "+ String.format("%2d",studentsInTableQueue.read());
            } catch (MemException e) {
                e.printStackTrace();
            }
        }*/
        log.writelnString (lineStatus);
        if (!log.close ()){ 
            GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
            System.exit (1);
        }
    }

    public void printSumUp() {
        TextFile log = new TextFile ();  

        if (!log.openForAppending (".", logFileName))
            { GenericIO.writelnString ("The operation of opening for appending the file " + logFileName + " failed!");
                System.exit (1);
            }
        
        if (!log.close ())
        { 
            GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
            System.exit (1);
        }
    }

    
}
