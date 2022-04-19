package entities;

import main.Constants;
import sharedRegions.*;

public class Student extends Thread 
{
    
    //identify the student
    //id + the state the student is in
    private int studentID;
    private int studentState;
    private final Table table;
    private final Bar bar;
    private boolean bill=false;


    public Student(int studentID,int studentState, Table table, Bar bar)
    {
        //initial state
        this.studentID = studentID;
        this.studentState = studentState;
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
    public void run() 
    {
        System.out.println("student thread");
        walkABit();
        int[] orderOfArrival = bar.enter();
        table.readMenu();
        if (orderOfArrival[0] != studentID) table.informCompanion();
        else
        {
            table.prepareTheOrder();
            while(!table.hasEverybodyChosen()) table.addUpOnesChoice();
            System.out.println("student is going to call the waiter");
            bar.callWaiter();
            table.describeTheOrder();
            table.joinTheTalk();
        }

        for(int i=0; i< Constants.M; i++)
        {
            table.startEating();
            table.endEating();
            while(table.hasEverybodyFinished());
        }

        if(orderOfArrival[Constants.N-1] == studentID) 
        {
            bar.signalTheWaiter();
            table.shouldHaveArrivedEarlier();
            table.honourTheBill();
            bill=true;
        }
        if(bill) bar.exit();
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
        try { 
            Thread.sleep ((long) (1 + 40 * Math.random ()));
        }
        catch (InterruptedException e) {}
    }
}
