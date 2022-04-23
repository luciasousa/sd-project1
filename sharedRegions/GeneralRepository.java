package sharedRegions;

import commInfra.MemException;
import commInfra.MemFIFO;
import entities.ChefStates;
import entities.StudentStates;
import entities.WaiterStates;
import main.Constants;
import genclass.GenericIO;
import genclass.TextFile;

/**
 *
 * General Repository
 * Responsible to keep the visible internal state of the problem.
 * Prints in the logging file.
 * Implemented as an implicit monitor.
 * All public methods are executed in mutual exclusion.
 * There are no internal synchronization points.
 * 
 */

public class GeneralRepository {

    /**
     *  Name of the logging file.
     */
    private final String logFileName;

    /**
     *  State of the chef.
     */
    private int chefState;

    /**
     *  State of the waiter.
     */
    private int waiterState;

    /**
     *  States of the students.
     */
    private int[] studentState = new int[Constants.N];

    /**
     *  FIFO with students at the table.
     */
    private MemFIFO<Integer> studentsInTableQueue;

    /**
     *  Counter with the number of course.
     */
    private int numberOfCourse;

    /**
     *  Counter with the number of portion.
     */
    private int numberOfPortion;

    private int k = 0;

    /**
     *   Instantiation of a general repository object.
     *
     *     @param logFileName name of the logging file
     */
    public GeneralRepository(String logFileName)
    {
        this.logFileName = logFileName;
        chefState = ChefStates.WAFOR;
        waiterState = WaiterStates.APPST;
        for(int i = 0; i < Constants.N; i++)
        {
            studentState[i] = StudentStates.GGTRT;
        }

        try {
            studentsInTableQueue = new  MemFIFO(new Integer[Constants.N]);
        } catch (MemException e) {
            e.printStackTrace();
        }
        numberOfCourse = 0;
        numberOfPortion = 0;

        reportInitialStatus();
    }

    /**
     *  Write the header and the initial states to the logging file.
     *
     *  
     */
    private void reportInitialStatus() 
    {
        TextFile log = new TextFile();
        if (!log.openForWriting (".", logFileName))
        {
            GenericIO.writelnString ("The operation of creating the file " + logFileName + " failed!");
            System.exit(1);
        }
        log.writelnString ("\t\t\t\t\t\t\t\t\t\t\t\tThe Restaurant - Description of the internal state\n");
        log.writelnString (" Chef   Waiter  Stu0  Stu1    Stu2   Stu3   Stu4   Stu5  Stu6    NCourse   NPortion                     Table       ");
        log.writelnString ("State   State  State  State  State  State  State  State  State                        Seat0 Seat1 Seat2 Seat3 Seat4 Seat5 Seat6");
        if (!log.close())
        {
            GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
            System.exit (1);
        }
        reportStatus();
    }

    /**
    *   Update FIFO updateStudentsInTableQueue
    *
    *     @param studentID integer
    */
    public synchronized void updateStudentsInTableQueue(int studentID)
    {
        try {
            studentsInTableQueue.write(studentID);
        } catch (MemException e) {
            e.printStackTrace();
        }
    }

    /**
    *   Set chef state.
    *
    *     @param state chef state
    */
    public synchronized void setChefState (int state)
    {
	  
        switch(state) 
        {
            case 0: //WAFOR
                break;
            case 1: //PRPCS
                numberOfCourse += 1;
                numberOfPortion = 0;
                break;
            case 2: //DSHPT
                break;
            case 3: //DLVPT
                break;
            case 4: //CLSSV
                break;
        }
        chefState = state;
        reportStatus();
   }

   /**
    *   Set waiter state.
    *
    *     @param state waiter state
    */
    public synchronized void setWaiterState (int state)
    {
        waiterState = state;
        reportStatus();
   }

   /**
    *   Set student state.
    *
    *     @param state student state
    */
   public synchronized void setStudentState (int studentID, int state)
    {
	    switch(state) 
        {
            case 0: //GGTRT
                break;
            case 1: //TKSTT
                updateStudentsInTableQueue(studentID);
                break;
            case 2: //SELCS
                break;
            case 3: //OGODR
                break;
            case 4: //CHTWC
                break;
            case 5: //EJYML
                break;
            case 6: //PYTBL
                break;
            case 7: //GGHOM
                break;
	    }
        studentState[studentID] = state;
        reportStatus();
   }

   /**
    *   Update Counter numberOfPortion
    *
    *     @param nPortions integer
    */
    public synchronized void setNumberOfPortions(int nPortions)
    {
        numberOfPortion = nPortions;
        reportStatus();
    }

    /**
    *   Update Counter numberOfCourse
    *
    *     @param nCourses integer
    */
    public synchronized void setNumberOfCourses(int nCourses)
    {
        numberOfCourse = nCourses;
        reportStatus();
    }

    /**
     *  Write a state line at the end of the logging file.
     *
     *  The current state of entities is organized in a line to be printed.
     * 
     */
    private void reportStatus() 
    {
        TextFile log = new TextFile ();                      // instantiation of a text file handler

        String lineStatus = "";                              // state line to be printed

        if (!log.openForAppending (".", logFileName))
        { 
            GenericIO.writelnString ("The operation of opening for appending the file " + logFileName + " failed!");
            System.exit (1);
        }

        switch (chefState)
        {
            case 0: lineStatus += "WAFOR  ";
                                                break;
            case 1: lineStatus += "PRPCS  ";
                                                break;
            case 2: lineStatus += "DSHPT  ";
                                                break;
            case 3: lineStatus += "DLVPT  ";
                                                break;
            case 4: lineStatus += "CLSSV  ";
                                                break;
        }
   
        switch (waiterState)
        {
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
            switch (studentState[i])
            {
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

        try {
            if(!studentsInTableQueue.empty)
            {
                int id = studentsInTableQueue.read();
                for (int i = 0; i < k; i++)
                {
                    lineStatus += String.format("\t");
                }
                lineStatus += String.format("\t %5d ",id);
                k++;
            }
        } catch (MemException e) {
            e.printStackTrace();
        }
    
        log.writelnString (lineStatus);
        if (!log.close ())
        { 
            GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
            System.exit (1);
        }
    }

    /**
    *   Write to the logging file if operation of opening for appending the file or operation of closing the file failed.
    *
    */
    public void printSumUp() 
    {
        TextFile log = new TextFile ();  

        if (!log.openForAppending (".", logFileName))
        { 
            GenericIO.writelnString ("The operation of opening for appending the file " + logFileName + " failed!");
            System.exit(1);
        }
        
        log.writelnString("\n\n\nLegend: \n" +
        "Chef State - state of the chef: WAFOR PRPCS DSHPT DLVPT CLSSV\n" +
        "Waiter State - state of the waiter: APPST PRSMN TKODR PCODR WTFPT PRCBL RECPM\n" +
        "Stu# State - state of the student #: GGTRT TKSTT SELCS OGODR CHTWC EJYML PYTBL GGHOM\n" +
        "NCourse - number of the course: 0 upto M\n" +
        "NPortion - number of the portion of a course: 0 upto N\n" +
        "Table Seat# - id of the student sat at that chair\n");
        
        if (!log.close ())
        { 
            GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
            System.exit (1);
        }
        
    }
}
