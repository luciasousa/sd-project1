package main;

import java.util.*;

import entities.*;
import sharedRegions.*;

public class RestaurantSimulation{

    
    public static void main(String [] args){

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
		
        //array com a ordem de chegada dos estudantes
        int[] studentsOrder;

        //inicializar array [1,2...,N]
        studentsOrder = new int[Constants.N];
        Arrays.setAll(studentsOrder, i -> i + 1);
        //gerar array de 1 a N com ordem alatória
        //studentsOrder = [1,2,3,...,N] to a random order
        Random rand = new Random();
        for (int i = 0; i < studentsOrder.length; i++) {
			int randomIndexToSwap = rand.nextInt(studentsOrder.length);
			int temp = studentsOrder[randomIndexToSwap];
			studentsOrder[randomIndexToSwap] = studentsOrder[i];
			studentsOrder[i] = temp;
		}

        for(int i=0; i<Constants.N; i++){
            //definir o id do estudante
            student[studentsOrder[i]].setStudentID(studentsOrder[i]);
            int studentID = student[studentsOrder[i]].getStudentID();
            //definir o primeiro estado do estudante, GGTRT
            student[studentID].setStudentState(StudentStates.GGTRT);
        }

        //atualizar variáveis do primeiro e último estudante
        student[studentsOrder[0]].setStudentFirst(true);
        student[studentsOrder[Constants.N-1]].setStudentLast(true);

        //start simulation
        //lançar as threads do estudante e sleep durante um periodo random
        //Thread.sleep ((long) (1 + 100 * Math.random ()));
        for(int i=0; i<Constants.N; i++){
            try {
                student[i].wait((long) (1 + 100 * Math.random ()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } 
        }
        

        //wait for the simulation to end
    
    
    }
}