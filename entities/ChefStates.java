package entities;

public class ChefStates {

    //initial state waiting for an order
    public static int WAFOR = 0;

    //state preparing a course
    public static int PRPCS = 1;

    //state dishing the portions
    public static int DSHPT = 2;

    //state delivering the portions
    public static int DLVPT = 3;

    //state closing service
    public static int CLSSV = 4;
    
}

/*public enum ChefStates {
    WAITING_FOR_AN_ORDER("WAFOR"),
    PREPARING_A_COURSE("PRPCS"),
    DISHING_THE_PORTIONS("DSHPT"),
    DELIVERING_THE_PORTIONS("DLVPT"),
    CLOSING_SERVICE("CLSSV");

    private ChefStates(String s) {
        
    }    
}*/

