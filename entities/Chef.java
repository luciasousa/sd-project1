package entities;

import sharedRegions.*;

public class Chef extends Thread {
    
    //identify the chef
    //the state the chef is in

    public int chefState;

    public Chef(){
        //initial state
        chefState = ChefStates.WAFOR;
    }

    public void setChefState(int state){
        chefState = state;
    }

    public int getChefState(){
        return chefState;
    }

    //fucntion run - thread 
    
}
