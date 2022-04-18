package main;

import entities.*;
import sharedRegions.*;

public class RestaurantSimulation
{
    public static void main(String [] args)
    {

        //instantiate entities
        Chef chef;
        Waiter waiter;
        Student[] student = new Student[Constants.N];

        //instantiate shared regions
        Kitchen kitchen;
        Bar bar;
        Table table;
        GeneralRepository repository;

        System.out.println("The Restaurant Simulation");
    
        //initialize problem

        //start simulation
        
        repository = new GeneralRepository("logger");
        kitchen = new Kitchen(repository);
        table = new Table(repository);
        bar = new Bar(repository, table, kitchen);
        

        chef = new Chef(ChefStates.WAFOR, kitchen, bar);
        waiter = new Waiter(WaiterStates.APPST, bar, kitchen, table);
        for (int i = 0; i < Constants.N; i++)
            student[i] = new Student(i,StudentStates.GGTRT, table, bar);
        
        /* start of the simulation */
        chef.start();
        waiter.start();
        for (int i = 0; i < Constants.N; i++)
        	student[i].start();

        /* waiting for the end of the simulation */
        for (int i = 0; i < Constants.N; i++)
        { 
            try{ 
                student[i].join ();
            }
            catch (InterruptedException e) {}
            System.out.println("The Student "+(i)+" just terminated");
        }
        
        try {
			chef.join();
		} catch (InterruptedException e) {}
        System.out.println("The chef has terminated");
        
        try {
        	waiter.join();	
		} catch (InterruptedException e) {}
        System.out.println("The waiter has terminated");
        
    
        System.out.println("End of the Simulation");
        
        //wait for the simulation to end
        repository.printSumUp();
    
    }
}