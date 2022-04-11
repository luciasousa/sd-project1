package sharedRegions;
import libraries.*;
import main.Constants;

import java.util.*;
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
    //lista com o ID dos estudantes pela ordem de chegada à mesa
    //private Queue<Integer> studentsInTableQueue;
    //número de porções que já foram entregues
    private int numberOfPortionsDelivered;
    //número de porções que já foram comidas
    private int numberOfPortionsEaten;
    private boolean dishReady = false;
    //lista com pedidos dos estudantes
    //private Queue<Request> studentsRequestsQueue;
    private int numberOfStudentsRequests;

    //array de estudantes
    //private Student[] student;
    private Bar bar;

    private final GeneralRepository repos;
    //flags
    //flag para indicar que conta foi paga
    private boolean billPaid = false;
    //flag para indicar que estudante leu o menu
    private boolean menuRead = false;
    //flag para indicar que estudante informou
    private boolean hasInformed = false;
    //flag para indicar se waiter voltou ao bar
    private boolean waiterHasReturnedToTheBar = false;
    //flag para verificar se o waiter está pronto para receber o pedido
    private boolean waiterHasThePad = false;

    public Table(GeneralRepository repos, Bar bar)
    {
        //inicializar threads dos estudantes
        /*student = new Student[Constants.N];
        for(int i = 0; i < Constants.N; i++){
            student[i] = null;
        }*/

        //inicializar variáveis da table
        //studentsInTableQueue = new LinkedList<>();
        numberOfPortionsDelivered = 0;
        numberOfPortionsEaten = 0;
        //studentsRequestsQueue = new LinkedList<>();
        numberOfStudentsRequests = 0;
        this.repos = repos;
        this.bar = bar;
    }

    public synchronized void takeASeat(int studentID) 
    {
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.TKSTT);
        menuRead = false;
        while(student.getStudentID() != studentID) 
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void saluteTheClient() 
    {
        //desbloqueia estudante preso em takeASeat
        notifyAll();

        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.PRSMN);

        //adormecer o waiter até o estudante ler o menu
        while(!menuRead)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void readMenu() 
    {
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.SELCS);
        //desbloqueia waiter preso em saluteTheClient
        menuRead = true;
        notifyAll();
    }

    public synchronized void prepareTheOrder() 
    {
        //primeiro estudante vai organizar o pedido
        //passa para o estado OGODR
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.OGODR);

        //esperar que seja acordado pelos outros estudantes
        //só a thread do primeiro estudante faz esta função
        //por isso faz wait até notify dos outros estudantes
        while (!hasInformed) 
        {    
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void informCompanion() 
    {
        //se não é o primeiro estudante então vai informar o seu pedido ao primeiro estudante
        //todos os estudantes menos o primeiro transitam para o estado CHTWC
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.CHTWC);
        //desbloquear o 1º estudante
        hasInformed = true;
        notifyAll();

        while(!dishReady) 
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void addUpOnesChoice() 
    {
        //primeiro estudante adiciona pedidos dos restantes
        //primeiro estudante mantém-se no estado OGODR
        numberOfStudentsRequests += 1;
        hasInformed = false;
        //espera por ser desbloqueado pelo informCompanion
        while(!hasInformed)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } 
        }
    }
    
    public synchronized void waitForPad() 
    {     
        //adormecer o estudante até o waiter ter o bloco
        while(!waiterHasThePad) 
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void setIfWaiterHasPad(boolean b) { waiterHasThePad = b; }

    public void describeTheOrder() 
    {
        synchronized(this) 
        {
            //acordar o waiter para sincronizar
            notifyAll();
        }
        bar.setOrderDescribed(true);
    }

    public synchronized void joinTheTalk() 
    {
        //primeiro estudante passa para o estado CHTWC
        Student student = (Student) Thread.currentThread();
        student.setStudentState(StudentStates.CHTWC);
        while(!dishReady)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void startEating() 
    {
        //estudante passa para o estado EJYML
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.EJYML);

        try {
            wait((long) (1 + 500 * Math.random ()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public synchronized void endEating() 
    {
        //aumenta o número de porções comidas
        numberOfPortionsEaten++;
        //estudante passa para o estado CHTWC
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.CHTWC);

    }

    public synchronized void signalTheWaiter() 
    {
        //acordar o waiter para lhe darem o pedido
        notifyAll();
        //adormecer o estudante até waiter voltar para o bar

        //TODO: adicionar flag no bar

        while(!waiterHasReturnedToTheBar)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void shouldHaveArrivedEarlier() 
    {
        //ultimo estudante passa para o estado de PYTBL
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.PYTBL);
        billPaid = true;
    }

    public synchronized void honourTheBill() 
    {

        //mantém no estado PYTBL até conta estar paga
        //se paga passa para estado GGHOM
        if(billPaid)
        {
            Student student = ((Student) Thread.currentThread());
            student.setStudentState(StudentStates.GGHOM);
        }

    }

    public synchronized void exit() 
    {
        //passam para o estado GGHOM
        //exit quando todos terminaram de comer e último ter pago a conta
        //vai sair um a um, empregado vai dizer adeus
        Student student = (Student)Thread.currentThread();
        //int studentID = student[studentsOrder[i]].getStudentID();
        student.setStudentState(StudentStates.GGHOM);
        //acordar o waiter para ele entregar o menu
        notifyAll();
        //adormecer o estudante até waiter voltar para o bar
        //TODO: adicionar flag no bar

        while(!waiterHasReturnedToTheBar)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized boolean hasEverybodyChosen() 
    {
        //retorna true quando o número de pedidos é N
        if (numberOfStudentsRequests == Constants.N) return true; else return false;
    }

    public synchronized boolean hasEverybodyFinished() 
    {
        //retorna true quando o número de porções comidas é N
        if(numberOfPortionsEaten == Constants.N) return true; else return false;
    }

    public synchronized void presentTheBill() 
    {
        //TODO: alterar estado do waiter

        //acordar o waiter para ir à mesa
        notifyAll();

        Waiter waiter =(Waiter)Thread.currentThread();
        waiter.setWaiterState(WaiterStates.RECPM);
        //adormecer o estudante até waiter voltar para o bar

        //TODO: adicionar flag no bar

        while(!waiterHasReturnedToTheBar)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void deliverPortion() 
    {
        numberOfPortionsDelivered++;
        notifyAll();
        dishReady = true;
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean haveAllClientsBeenServed() 
    {
        if(numberOfPortionsDelivered == Constants.N) return true; else return false;
    }
}

