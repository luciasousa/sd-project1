package entities;

import main.Constants;
import sharedRegions.*;

public class Student extends Thread {
    
    //identify the student
    //id + the state the student is in
    private int studentID;
    private int studentState;
    private boolean firstStudent;
    private boolean lastStudent;
    private final Table table;
    private final Bar bar;


    public Student(int studentID, Table table, boolean firstStudent, boolean lastStudent, Bar bar){
        //initial state
        this.studentID = studentID;
        studentState = StudentStates.GGTRT;
        this.table = table;
        this.bar=bar;
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

        walkABit();

        table.enter();
        bar.enter();

        table.readMenu();

        if (!firstStudent) table.informCompanion();

        else {

            table.prepareTheOrder();

            while(!table.hasEverybodyChosen()) table.addUpOnesChoice();

            table.callWaiter();
            bar.callWaiter();
            
            table.describeTheOrder();
            bar.describeTheOrder();

            table.joinTheTalk();

        }

        for(int i=0; i< Constants.M; i++){
            table.startEating();

            table.endEating();
    
            while(table.hasEverybodyFinished());

            bar.hasEverybodyFinished();
        }
       

        table.signalTheWaiter();

        if(lastStudent) {

            table.shouldHaveArrivedEarlier();

            table.honourTheBill();

        }

        table.exit();
        
    }

    /**
   *  Living normal life.
   *
   *  Internal operation.
   */

    private void walkABit() {
        //estudantes vão chegando ao restaurante aleatoriamente
        //estudantes estão no primeiro estado bloqueados
        //estudante fica sleep durante período random
        try{ 
            Thread.sleep ((long) (1 + 40 * Math.random ()));
        }
        catch (InterruptedException e) {}
    }
}
