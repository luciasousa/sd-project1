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
    Queue<Request> coursesRequestsQueue;
    int numberOfCoursesRequests;

    //array de estudantes
    Student[] student;

    //flags
    //flag para indicar que o waiter regressou ao bar depois de ter entregue o menu
    boolean waiterHasReturnedToTheBar = false;
    //flags para indicar se todos os estudantes informaram o primeiro
    boolean[] studentHasBeenInformed = new boolean[Constants.N];
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
        coursesRequestsQueue = new LinkedList<>();
        numberOfCoursesRequests = 0;


    }


    public synchronized void walkABit() {

        //estudantes vão chegando ao restaurante aleatoriamente
        //estudantes estão no primeiro estado bloqueados
        //estudante fica sleep durante período random
        try{ 
            Thread.sleep ((long) (1 + 40 * Math.random ()));
        }
        catch (InterruptedException e) {}

    }

    public synchronized void enter() {

        //entra estudante um a um
        //adicionar à lista o estudante que se senta à mesa
        //passar para o estado TKSTT (1)
        Student student = ((Student) Thread.currentThread());
        //int studentID = student.getStudentID();
        //definir o novo estado do estudante, TAKE A SEAT AT THE TABLE
        student.setStudentState(StudentStates.TKSTT);

        
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

            //estudantes vão chegando e empregado vai entregando menus e regressando ao bar
        


    }

    public synchronized void readMenu() {

        //estudante vai ler menu e escolher o pedido
        //transita para o estado SELCS (2)
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.SELCS);
        
        //TODO:seleciona course
        
    }

    public synchronized void prepareTheOrder() {

        //primeiro estudante vai organizar o pedido
        //passa para o estado OGODR
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.OGODR);
        int studentID = student.getStudentID();
        studentHasBeenInformed[studentID] = true;
        numberOfCoursesRequests++;

        //esperar que seja acordado pelos outros estudantes
        while(){

        }

    }

    public synchronized void informCompanion() {
        //se não é o primeiro estudante então vai informar o seu pedido ao primeiro estudante
        //todos os estudantes menos o primeiro transitam para o estado CHTWC
        Student student = ((Student) Thread.currentThread());
        int studentID = student.getStudentID();
        student.setStudentState(StudentStates.CHTWC);
        studentHasBeenInformed[studentID] = true;

        //desbloquear o 1º
    }

    public synchronized void joinTheTalk() {
        //pedidos feitos, todos escolheram, chamar o empregado
        

        //bloqueiam estudantes
        //chamam empregado
        //callWaiter();

        //dizem-lhe o pedido
        //describeTheOrder();

        //primeiro estudante passa para o estado CHTWC
        Student student = (Student) Thread.currentThread();
        if(student.getStudentFirst()){
            student.setStudentState(StudentStates.CHTWC);
        }
        

    }

    public synchronized void addUpOnesChoice() {
        //primeiro estudante adiciona pedidos dos restantes
        //primeiro estudante mantem-se no estado OGODR

        //espera por ser desbloqueado pelo informCompanion

        for(int i = 0; i < Constants.N-1; i++){
            //se foi informado
            if(studentHasBeenInformed[i]){
                numberOfCoursesRequests++;
            }
        }
        
    }

    public synchronized void callWaiter() {

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

    public synchronized void describeTheOrder() {
        //adicionar pedidos à queue
        Student student = (Student)Thread.currentThread();
        for (int i=0; i < numberOfCoursesRequests; i++){
            Request r = new Request(student.getStudentID(), 'o');
            coursesRequestsQueue.add(r);
        }
    }

    public synchronized void startEating() {
        //estudante passa para o estado EJYML
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.EJYML);

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
        for(boolean i: studentHasBeenInformed){
            if (!i){
                return false;
            }
        }
        return true;
    }

    public synchronized boolean hasEverybodyFinished() {
        //retorna true quando o número de porções comidas é N
        if(numberOfPortionsEaten == Constants.N){
            return true;
        }
        return false;
    }


    public synchronized void saluteTheClient() {

        

        //estudante bloqueado
        //waiter vai cumprimentar estudante e regressa ao bar
        //acordar o waiter para ir à mesa
        notifyAll();
        //TODO: alterar estado do waiter
        Waiter waiter =(Waiter)Thread.currentThread();
        waiter.setWaiterState(WaiterStates.PRSMN);
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

        //TODO: alterar estado do waiter
        //acordar o waiter para ir à mesa
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

        //incrementar número de porções entregues
        numberOfPortionsDelivered++;

    }


    public boolean haveAllClientsBeenServed() {
        return false;
    }
    
}

