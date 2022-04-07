package entities;

import sharedRegions.Table;

public class Student extends Thread {
    
    //identify the student
    //id + the state the student is in
    private int studentID;
    private int studentState;
    private boolean firstStudent;
    private boolean lastStudent;
    private final Table table;


    public Student(int studentID, Table table, boolean firstStudent, boolean lastStudent){
        //initial state
        this.studentID = studentID;
        studentState = StudentStates.GGTRT;
        this.table = table;
        this.firstStudent = firstStudent;
        this.lastStudent = lastStudent;
    }

    public void setStudentID(int id){
        studentID = id;
    }

    public int getStudentID(){
        return studentID;
    }

    public void setStudentState(int state){
        studentState = state;
    }

    public int getStudentState(){
        return studentState;
    }

    public void setStudentFirst(boolean first){
        firstStudent = first;
    }

    public boolean getStudentFirst(){
        return firstStudent;
    }

    public void setStudentLast(boolean last){
        lastStudent = last;
    }

    public boolean getStudentLast(){
        return lastStudent;
    }

    //function run - thread
    public void run() {

        table.walkABit();

        table.enter();
        //bar.enter();

        table.readMenu();

        if (!firstStudent) table.informCompanion();

        else {

            table.prepareTheOrder();

            while(!table.hasEverybodyChosen()) table.addUpOnesChoice();

            table.callWaiter();

            table.describeTheOrder();

            table.joinTheTalk();

        }

        table.startEating();

        table.endEating();

        while(table.hasEverybodyFinished());

        table.signalTheWaiter();

        if(lastStudent) {

            table.shouldHaveArrivedEarlier();

            table.honourTheBill();

        }

        table.exit();
        
    }
}
