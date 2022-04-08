package sharedRegions;
import libraries.*;
import main.Constants;

import java.util.*;
import entities.*;
import java.lang.*;

/*
TABLE

*/

public class Table {

    //lista com o ID dos estudantes pela ordem de chegada à mesa
    Queue<Integer> studentsInTableQueue;

    //número de porções que já foram entregues
    int numberOfPortionsDelivered;

    //número de porções que já foram comidas
    int numberOfPortionsEaten;

    //lista com pedidos dos estudantes
    Queue<Request> studentsRequestsQueue;
    int numberOfStudentsRequests;

    //array de estudantes
    Student[] student;

    //flags
    //flag para indicar que o waiter regressou ao bar depois de ter entregue o menu
    boolean waiterHasReturnedToTheBar = false;
    //flags para indicar se todos os estudantes informaram o primeiro
    //boolean[] studentHasBeenInformed = new boolean[Constants.N];
    //flag para indicar que conta foi paga
    boolean billPaid = false;

    public Table(){
        //inicializar threads dos estudantes
        student = new Student[Constants.N];
        for(int i = 0; i < Constants.N; i++){
            student[i] = null;
        }

        //inicializar queue dos estudantes
        studentsInTableQueue = new LinkedList<>();

        numberOfPortionsDelivered = 0;
        numberOfPortionsEaten = 0;
        studentsRequestsQueue = new LinkedList<>();
        numberOfStudentsRequests = 0;


    }

    public synchronized void enter() {

        //entra estudante um a um
        //adicionar à lista o estudante que se senta à mesa
        //passar para o estado TKSTT (1)
        Student student = ((Student) Thread.currentThread());
        //int studentID = student.getStudentID();
        //definir o novo estado do estudante, TAKE A SEAT AT THE TABLE
        student.setStudentState(StudentStates.TKSTT);

        
        //wait() method causes the current thread to wait indefinitely until another thread either invokes 
        //notify() for this object or notifyAll().

        //waiter wait() -> student enters -> notify() -> waiter is awake

        //acordar o waiter para ele entregar o menu
        
        //adormecer o estudante até waiter voltar para o bar
        
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void saluteTheClient() {

        //estudante bloqueado
        
        notify();

        Waiter waiter =(Waiter)Thread.currentThread();
        waiter.setWaiterState(WaiterStates.PRSMN);
        //adormecer o estudante até waiter voltar para o bar

        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void readMenu() {

        //estudante vai ler menu e escolher o pedido
        //transita para o estado SELCS (2)
        notify();
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.SELCS);
        
        //seleciona course
        
    }

    public synchronized void prepareTheOrder() {

        //primeiro estudante vai organizar o pedido
        //passa para o estado OGODR
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.OGODR);
        //int studentID = student.getStudentID();
        //studentHasBeenInformed[studentID] = true;
        //numberOfStudentsRequests++;

        //esperar que seja acordado pelos outros estudantes
        //só a thread do primeiro estudante faz esta função
        //por isso faz wait até notify dos outros estudantes
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //fica nesta estado até ser acordado, 
        //ou seja, até add up ones choice / has everybody chosen / call the waiter / describe the order
    }

    public synchronized void informCompanion() {
        //se não é o primeiro estudante então vai informar o seu pedido ao primeiro estudante
        //vão 
        //todos os estudantes menos o primeiro transitam para o estado CHTWC
        Student student = ((Student) Thread.currentThread());
        //int studentID = student.getStudentID();
        student.setStudentState(StudentStates.CHTWC);
        //studentHasBeenInformed[studentID] = true;

        //desbloquear o 1º estudante -> só uma thread
        notify();

        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void joinTheTalk() {
        //pedidos feitos, todos escolheram, chamar o empregado
        
        //primeiro estudante passa para o estado CHTWC
        Student student = (Student) Thread.currentThread();
        student.setStudentState(StudentStates.CHTWC);
        

        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        

    }

    public synchronized void addUpOnesChoice() {
        //primeiro estudante adiciona pedidos dos restantes
        //primeiro estudante mantem-se no estado OGODR

        //espera por ser desbloqueado pelo informCompanion

        numberOfStudentsRequests++;

        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }

    public synchronized void callWaiter() {
        //acordar o waiter para lhe darem o pedido
        //notify();        
        //adormecer o estudante até waiter voltar para o bar
        
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }

    public synchronized void getThePad() {
        notify();
        
    }

    public synchronized void describeTheOrder() {

    }

    public synchronized void startEating() {
        //estudante passa para o estado EJYML
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.EJYML);

        try {
            wait((long) (1 + 500 * Math.random ()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public synchronized void endEating() {
        //aumenta o número de porções comidas
        numberOfPortionsEaten++;
        //estudante passa para o estado CHTWC
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.CHTWC);

    }

    public synchronized void signalTheWaiter() {
        //acordar o waiter para lhe darem o pedido
        notifyAll();
        //adormecer o estudante até waiter voltar para o bar

        //TODO: adicionar flag no bar

        while(!waiterHasReturnedToTheBar){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void shouldHaveArrivedEarlier() {
        //ultimo estudante passa para o estado de PYTBL
        Student student = ((Student) Thread.currentThread());
        int studentID = student.getStudentID();
        if(student.getStudentLast()){
            student.setStudentState(StudentStates.PYTBL);
            billPaid = true;
        }
    }

    public synchronized void honourTheBill() {

        //mantém no estado PYTBL até conta estar paga
        //se paga passa para estado GGHOM
        if(billPaid){
            Student student = ((Student) Thread.currentThread());
            student.setStudentState(StudentStates.GGHOM);
        }

    }

    public synchronized void exit() {
        //passam para o estado GGHOM
        //exit quando todos terminaram de comer e último ter pago a conta
        //vai sair um a um, empregado vai deizer adeus
        //for(int i=0; i<Constants.N; i++){
            Student student = (Student)Thread.currentThread();

            //int studentID = student[studentsOrder[i]].getStudentID();
            student.setStudentState(StudentStates.GGHOM);
            
            //acordar o waiter para ele entregar o menu
            notifyAll();
            //adormecer o estudante até waiter voltar para o bar

            //TODO: adicionar flag no bar

            while(!waiterHasReturnedToTheBar){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //estudantes vão saindo e empregado vai dizendo adeus
        //}
    }

    public synchronized boolean hasEverybodyChosen() {
        //retorna true quando o número de pedidos é N
        
        if (numberOfStudentsRequests == (Constants.N - 1)){
            return true;
        }
        return false;
    }

    public synchronized boolean hasEverybodyFinished() {
        //retorna true quando o número de porções comidas é N
        if(numberOfPortionsEaten == Constants.N){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }





    public synchronized void presentTheBill() {

        //TODO: alterar estado do waiter

        //acordar o waiter para ir à mesa
        notifyAll();

        Waiter waiter =(Waiter)Thread.currentThread();
        waiter.setWaiterState(WaiterStates.RECPM);
        //adormecer o estudante até waiter voltar para o bar

        //TODO: adicionar flag no bar

        while(!waiterHasReturnedToTheBar){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public synchronized void deliverPortion() {

        numberOfPortionsDelivered++;

        notify();
        
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    public boolean haveAllClientsBeenServed() {
        if(numberOfPortionsDelivered == Constants.N) {
            return true; 
        }
        else return false;
    }
    
}

