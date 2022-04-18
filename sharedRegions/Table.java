package sharedRegions;
import main.Constants;
import commInfra.MemException;
import commInfra.MemFIFO;
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
    private int numberOfPortionsDelivered=0;
    //número de porções que já foram comidas
    private int numberOfPortionsEaten=0;
    private boolean dishReady = false;
    //lista com pedidos dos estudantes
    //private Queue<Request> studentsRequestsQueue;
    private int numberOfStudentsRequests=1;

    //array de estudantes
    //private Student[] student;
    private Bar bar;

    private final GeneralRepository repos;
    //flags
    //flag para indicar que conta foi paga
    private boolean billPaid = false;
    //flag para indicar quais estudantes leram o menu
    private boolean[] menuRead;
    //flag para indicar que estudante informou
    private boolean wasInformed = false;
    //flag para indicar se waiter voltou ao bar
    private boolean waiterHasReturnedToTheBar = false;
    //flag para verificar se o waiter está pronto para receber o pedido
    private boolean waiterHasThePad = false;
    private boolean isBillDelivered = false;
    private boolean isBillPrepared = false;
    private boolean studentHasPaid=false;
    private int lastStudentToTakeASeat;
    private boolean[] clientsSaluted;
    private boolean orderDescribed;

    public Table(GeneralRepository repos)
    {
        //inicializar threads dos estudantes
        /*student = new Student[Constants.N];
        for(int i = 0; i < Constants.N; i++){
            student[i] = null;
        }*/

        //inicializar variáveis da table
        //studentsInTableQueue = new LinkedList<>();
        
        //studentsRequestsQueue = new LinkedList<>();
        this.repos = repos;
        clientsSaluted = new boolean[Constants.N];
        menuRead = new boolean[Constants.N];
    }

    //função chamada pelo Bar
    public synchronized void takeASeat() 
    {
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.TKSTT);
        System.out.printf("student %d take a seat, state: %d\n", student.getStudentID(),student.getStudentState());

        while(!clientsSaluted[student.getStudentID()]) 
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void saluteTheClient(int studentID) 
    {
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.PRSMN);
        System.out.printf("waiter salute the client %d, state: %d\n", studentID, waiter.getWaiterState());
        
        //desbloqueia estudante preso em takeASeat
        clientsSaluted[studentID] = true;
        notifyAll();
        //adormecer o waiter até o estudante ler o menu
        while(!menuRead[studentID])
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
        int studentID = student.getStudentID();
        System.out.printf("student %d read menu, state: %d\n", studentID, student.getStudentState());
        //desbloqueia waiter preso em saluteTheClient
        menuRead[studentID] = true;
        notifyAll();
    }

    public synchronized void prepareTheOrder() 
    {
        //primeiro estudante vai organizar o pedido
        //passa para o estado OGODR
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.OGODR);
        System.out.printf("student %d prepare the order, state: %d\n", student.getStudentID(), student.getStudentState());

        //esperar que seja acordado pelos outros estudantes
        //só a thread do primeiro estudante faz esta função
        //por isso faz wait até notify dos outros estudantes
        while (!wasInformed) 
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
        System.out.printf("student %d inform companion, state: %d\n", student.getStudentID(), student.getStudentState());
        //desbloquear o 1º estudante
        wasInformed = true;
        numberOfStudentsRequests += 1;
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
        
        wasInformed = false;
        Student student = (Student) Thread.currentThread();
        System.out.printf("student %d has been informed\n", student.getStudentID());
        //espera por ser desbloqueado pelo informCompanion
        while(!wasInformed)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } 
        }
    }
    
    //função chamada pelo Bar
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
        System.out.println("waiter has the pad");
    }

    public synchronized void getThePad() 
    { 
        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.TKODR);
        System.out.printf("waiter get the pad, state: %d\n", waiter.getWaiterState());

        //acorda o estudante
        waiterHasThePad = true;
        notifyAll(); 

        //espera que ele descreva o pedido
        while(!orderDescribed)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void describeTheOrder() 
    {
        System.out.println("order is described");
        orderDescribed = true;
        notifyAll();
    }

    public synchronized boolean hasEverybodyChosen() 
    {
        System.out.printf("pedidos dos estudantes = %d\n", numberOfStudentsRequests);
        if (numberOfStudentsRequests == Constants.N) return true; else return false;
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

    public synchronized void deliverPortion() 
    {
        numberOfPortionsDelivered++;
        dishReady = true;
        
        //acorda um dos estudantes
        notify();
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

    public synchronized void startEating() 
    {
        //estudante passa para o estado EJYML
        Student student = ((Student) Thread.currentThread());
        student.setStudentState(StudentStates.EJYML);
        System.out.printf("student %d has started eating\n", student.getStudentID());
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
        System.out.printf("student %d has finished eating\n", student.getStudentID());
    }

    public synchronized boolean hasEverybodyFinished() 
    {
        if(numberOfPortionsEaten == Constants.N) {
            System.out.println("everybody has finished this course");
            return true;
        } else return false;
    }

    //função chamada pelo bar em signalTheWaiter
    public synchronized void allFinished() 
    {
        //student espera que waiter prepare a conta
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void presentTheBill() 
    {

        Waiter waiter = (Waiter) Thread.currentThread();
        waiter.setWaiterState(WaiterStates.RECPM);
        
        //waiter bloqueia até honourTheBill
        while(!studentHasPaid)
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

        //student is waken up by the operation presentTheBill
        notifyAll();
    }

    public synchronized void honourTheBill() 
    {
        //mantém no estado PYTBL até conta estar paga
        //se paga passa para estado GGHOM
        isBillDelivered = true;
        if(billPaid)
        {
            Student student = ((Student) Thread.currentThread());
            student.setStudentState(StudentStates.GGHOM);
        }
        studentHasPaid=true;
    }

    public synchronized void goingHome(int studentID) 
    {
        //passam para o estado GGHOM
        //exit quando todos terminaram de comer e último ter pago a conta
        //vai sair um a um, empregado vai dizer adeus
        Student student = (Student)Thread.currentThread();
        //int studentID = student[studentsOrder[i]].getStudentID();
        student.setStudentState(StudentStates.GGHOM);

        while(student.getStudentID() != studentID) 
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

