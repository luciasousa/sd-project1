package entities;

import main.Constants;
import sharedRegions.*;

public class Student extends Thread {
    
    //identify the student
    //id + the state the student is in
    private int studentID;
    private int studentState;
    private final Table table;
    private final Bar bar;


    public Student(int studentID, Table table, Bar bar){
        //initial state
        this.studentID = studentID;
        studentState = StudentStates.GGTRT;
        this.table = table;
        this.bar=bar;
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

    //function run - thread
    public void run() {
        walkABit();
        int orderOfArrival = bar.enter();
        table.readMenu();

        if (orderOfArrival == 1) 
        {
            table.informCompanion();
        } else
        {
            table.prepareTheOrder();
            while(!table.hasEverybodyChosen()) table.addUpOnesChoice();
            table.callWaiter();
            bar.callWaiter();
            table.describeTheOrder();
            bar.describeTheOrder();
            table.joinTheTalk();
        }

        for(int i=0; i< Constants.M; i++)
        {
            table.startEating();
            table.endEating();
            while(table.hasEverybodyFinished());
        }

        table.signalTheWaiter();

        if(orderOfArrival == Constants.N) {
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
