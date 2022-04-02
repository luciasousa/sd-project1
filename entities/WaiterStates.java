package entities;

public class WaiterStates {
    //initial state appraising situation
    public static int APPST = 0;

    //state presenting the menu
    public static int PRSMN = 1;

    //state taking the order
    public static int TKODR = 2;

    //state placing the order
    public static int PCODR = 3;

    //state waiting for portion
    public static int WTFPT = 4;

    //state processing the bill
    public static int PRCBL = 5;

    //state receiving payment
    public static int RECPM = 6;
}
