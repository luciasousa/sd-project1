package sharedRegions;
import main.Constants;
import entities.*;

/**
 *    TABLE
 *
 *    It is responsible for the the synchronization of the Students and Waiter
 *    is implemented as an implicit monitor.
 *    
 *    There is one internal synchronization points: 
 *    a single blocking point for the Student, where he waits for the Waiter to signal
 */

public class Table 
{
    /**
     *   Counter of the number of portions delivered.
     */
    private int numberOfPortionsDelivered;

    /**
     *   Counter of the number of courses delivered.
     */
    private int numberOfCoursesDelivered;

    /**
     *   Counter of the number of portions eaten.
     */
    private int numberOfPortionsEaten;

    /**
     *   Counter of the number of students requests.
     */
    private int numberOfStudentsRequests = 1;

    /**
     *   Reference to the general repository.
     */
    private final GeneralRepository repos;

    /**
     *   Array of booleans that indicates if student read the menu.
     */
    private boolean[] menuRead;

    /**
     *   Boolean flag that indicates if was informed.
     */
    private boolean wasInformed = false;

    /**
     *   Boolean flag that indicates if waiter has the pad.
     */
    private boolean waiterHasThePad = false;

    /**
     *   Boolean flag that indicates if student has paid.
     */
    private boolean studentHasPaid = false;

    /**
     *   Array of booleans that indicates if clients were saluted.
     */
    private boolean[] clientsSaluted;

    /**
     *   Boolean flag that indicates if ordr was described.
     */
    private boolean orderDescribed;

    /**
     *   Array of booleans that indicates if course is ready.
     */
    private boolean []courseReady;

    /**
     *   Boolean flag that indicates if bill is ready.
     */
    private boolean billReady = false;

    /**
     *   Boolean flag that indicates if student has end eating.
     */
    private boolean hasEndedEating;

    /**
     *   Boolean flag that indicates if courses are complete.
     */
    private boolean coursesCompleted;

    /**
     *  Table instantiation.
     *
     *    @param repos reference to the General Information Repository
     */
    public Table(GeneralRepository repos)
    {
        this.repos = repos;
        clientsSaluted = new boolean[Constants.N];
        courseReady = new boolean[Constants.M];
        menuRead = new boolean[Constants.N];
    }

    /**
     *    Operation take a seat
     *
     *    Called by the student to take a seat at the table
     */
    public synchronized void takeASeat() 
    {
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.TKSTT);
        int studentID = student.getStudentID();
        int state = student.getStudentState();
        repos.setStudentState(studentID, state);
        repos.setSeatOrder(studentID);
        //System.out.printf("student %d take a seat, state: %d\n", student.getStudentID(),student.getStudentState());

        while(!clientsSaluted[student.getStudentID()]) 
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *    Operation salute the client
     *
     *    Called by the waiter to salute the client
     * 
     *    @param studentID the ID of the student to be saluted by the waiter
     */
    public synchronized void saluteTheClient(int studentID) 
    {
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.PRSMN);
        int state = waiter.getWaiterState();
        repos.setWaiterState(state);
        //System.out.printf("waiter salute the client %d, state: %d\n", studentID, waiter.getWaiterState());
        clientsSaluted[studentID] = true;
        notifyAll();
        while(!menuRead[studentID])
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *    Operation read menu
     *
     *    Called by the student to read the menu, wakes up waiter to signal that he has read the menu
     * 
     */
    public synchronized void readMenu() 
    {
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.SELCS);
        int studentID = student.getStudentID();
        int state = student.getStudentState();
        repos.setStudentState(studentID, state);
        //System.out.printf("student %d read menu, state: %d\n", studentID, student.getStudentState());
        menuRead[studentID] = true;
        //signal waiter that menu was read
        notifyAll();
    }

    /**
     *    Operation prepare the order
     *
     *    Called by the first student to arrive to prepare the order
     *    waits until gets the choices from companions
     * 
     */
    public synchronized void prepareTheOrder() 
    {
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.OGODR);
        int studentID = student.getStudentID();
        int state = student.getStudentState();
        repos.setStudentState(studentID, state);
        //System.out.printf("student %d prepare the order, state: %d\n", student.getStudentID(), student.getStudentState());
        while (!wasInformed) 
        {    
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *    Operation inform Companion
     *
     *    Called by the student (that was not the first to arrive) to inform companion
     *    Signal first student that it has been informed and waits for the course to be ready
     * 
     */
    public synchronized void informCompanion() 
    {
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.CHTWC);
        int studentID = student.getStudentID();
        int state = student.getStudentState();
        repos.setStudentState(studentID, state);
        //System.out.printf("student %d inform companion, state: %d\n", student.getStudentID(), student.getStudentState());
        wasInformed = true;
        numberOfStudentsRequests += 1;
        notifyAll();
        while(!courseReady[numberOfCoursesDelivered]) 
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *    Operation add up ones choice
     *
     *    Called by the first student to arrive to add up the companion's choice to the order
     *    waits until gets the choices from companions
     * 
     */
    public synchronized void addUpOnesChoice() 
    {
        wasInformed = false;
        Student student = (Student) Thread.currentThread();
        int studentID = student.getStudentID();
        int state = student.getStudentState();
        repos.setStudentState(studentID, state);
        //System.out.printf("student %d has been informed\n", student.getStudentID());
        while(!wasInformed)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } 
        }
    }
    
    /**
     *    Operation wait for pad
     *
     *    Called by the student when students calls the waiter
     *    waits until waiter has the pad
     * 
     */
    public synchronized void waitForPad() 
    {     
        while(!waiterHasThePad) 
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //System.out.println("waiter has the pad");
    }

    /**
     *    Operation get the pad
     *
     *    Called by the waiter to get the pad
     *    signal student that he got the pad and waits until student describes the order
     * 
     */
    public synchronized void getThePad() 
    { 
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.TKODR);
        int state = waiter.getWaiterState();
        repos.setWaiterState(state);
        //System.out.printf("waiter get the pad, state: %d\n", waiter.getWaiterState());
        waiterHasThePad = true;
        notifyAll(); 
        while(!orderDescribed)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *    Operation describe the order
     *
     *    Called by the first student to arrive to describe the order
     *    signals waiter that the order was described
     * 
     */
    public synchronized void describeTheOrder() 
    {
        //System.out.println("order is described");
        orderDescribed = true;
        notifyAll();
    }

    /**
     *    Operation has everybody chosen
     *
     *    Called by the first student to arrive to check if every companion has chosen
     *    @return boolean
     * 
     */
    public synchronized boolean hasEverybodyChosen() 
    {
        //System.out.printf("pedidos dos estudantes = %d\n", numberOfStudentsRequests);
        if (numberOfStudentsRequests == Constants.N) return true; else return false;
    }

    /**
     *    Operation join the talk
     *
     *    Called by the first student to arrive to join the talk
     *    waits until course is ready
     * 
     */
    public synchronized void joinTheTalk() 
    {
        Student student = (Student) Thread.currentThread();
        student.setStudentState(StudentStates.CHTWC);
        int studentID = student.getStudentID();
        int state = student.getStudentState();
        repos.setStudentState(studentID, state);
        //System.out.println("first student has joined the talk");
        while(!courseReady[numberOfCoursesDelivered])
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *    Operation deliver portion
     *
     *    Called by the waiter to deliver portion
     *    signals students that portion was delivered
     *    waits for all students to eat the course
     * 
     */
    public synchronized void deliverPortion() 
    {
        numberOfPortionsEaten = 0;
        numberOfPortionsDelivered += 1;
        repos.setNumberOfPortions(numberOfPortionsDelivered);
        hasEndedEating = false;
        //System.out.printf("waiter is delivering the portion %d\n", numberOfPortionsDelivered);
        if(numberOfPortionsDelivered == Constants.N)
        {
            //System.out.printf("course nº %d finished\n", numberOfCoursesDelivered);
            courseReady[numberOfCoursesDelivered] = true;
            notifyAll();
            if(numberOfCoursesDelivered == Constants.M - 1) coursesCompleted = true;
            while(!hasEndedEating)
            {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            numberOfPortionsDelivered = 0;
            if(numberOfCoursesDelivered < Constants.M - 1) numberOfCoursesDelivered += 1;
            if(coursesCompleted) repos.setNumberOfPortionsAndCourses(numberOfPortionsDelivered, numberOfCoursesDelivered + 1);
            else repos.setNumberOfPortionsAndCourses(numberOfPortionsDelivered, numberOfCoursesDelivered);
        }
    }

    /**
     *    Operation have all clients been served
     *
     *    Called by the waiter to check if all clients have been served
     *    @return boolean
     */
    public boolean haveAllClientsBeenServed() 
    {
        if(numberOfPortionsDelivered == Constants.N) return true; else return false;
    }

    /**
     *    Operation start eating
     *
     *    Called by the student to start eating
     *    waits a random time
     * 
     */
    public synchronized void startEating() 
    {
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.EJYML);
        int studentID = student.getStudentID();
        int state = student.getStudentState();
        repos.setStudentState(studentID, state);
        ////System.out.printf("student %d has started eating, course: %d\n", student.getStudentID(), numberOfCoursesDelivered);
        try {
            wait((long) (1 + 500 * Math.random ()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     *    Operation end eating
     *
     *    Called by the student to end eating
     * 
     */
    public synchronized void endEating() 
    {
        numberOfPortionsEaten += 1;
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.CHTWC);
        int studentID = student.getStudentID();
        int state = student.getStudentState();
        repos.setStudentState(studentID, state);
        ////System.out.printf("student %d has finished eating\n", student.getStudentID());
    }

    /**
     *    Operation has everybody finished
     *
     *    Called by the student to check if everybody has finished
     *    signals waiter that everybody finished eating
     *    waits for next course to be ready
     * 
     *    @return boolean
     */
    public synchronized boolean hasEverybodyFinished() 
    {
        if(numberOfPortionsEaten == Constants.N) 
        {
            hasEndedEating = true;
            notifyAll();
            courseReady[numberOfCoursesDelivered] = false;
            //System.out.printf("Courses Completed: %b\n",coursesCompleted);
            while(!(courseReady[numberOfCoursesDelivered] || coursesCompleted))
            {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return true; 
        } else return false;
    }

    /**
     *    Operation wait for payment
     *
     *    Called by the students(except for the last one) to wait for payment
     *    students wait for last student to pay the bill
     * 
     */
    public synchronized void waitForPayment()
    {
        Student student = (Student) Thread.currentThread();
        int studentID = student.getStudentID();
        //System.out.printf("Student %d is waiting for payment\n", studentID);
        while(!studentHasPaid)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *    Operation present the bill
     *
     *    Called by the waiter to present the bill
     *    signals last student that bill is ready
     *    waits for student to pay
     * 
     */
    public synchronized void presentTheBill() 
    {
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.RECPM);
        int state = waiter.getWaiterState();
        repos.setWaiterState(state);
        //System.out.println("presenting the bill");
        billReady = true;
        notifyAll();
        while(!studentHasPaid)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *    Operation should have arrived earlier
     *
     *    Called by the last student to arrive to pay the bill
     *    student waits for the bill to be ready
     * 
     */
    public synchronized void shouldHaveArrivedEarlier() 
    {
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.PYTBL);
        int studentID = student.getStudentID();
        int state = student.getStudentState();
        repos.setStudentState(studentID, state);
        //System.out.printf("student %d should have arrived earlier, pay the bill\n",studentID);
        while(!billReady)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *    Operation honour the bill
     *
     *    Called by the last student to arrive to honour the bill
     *    signals the rest of the students that he/she has paid the bill
     * 
     */
    public synchronized void honourTheBill() 
    {
        //System.out.println("bill has been honoured");
        studentHasPaid = true;
        notifyAll();
    }
}

