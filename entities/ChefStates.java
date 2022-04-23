package entities;

/**
 *    Definition of the internal states of the chef during his life cycle.
 */
public class ChefStates {

    /**
     *	The chef is waiting for an order.
     */
    public static int WAFOR = 0;

    /**
     *	The chef is preparing a course.
     */
    public static int PRPCS = 1;

    /**
     *	The chef is dishing the portions.
     */
    public static int DSHPT = 2;

    /**
     *	The chef is delivering the portions.
     */
    public static int DLVPT = 3;

    /**
     *	The chef is closing service.
     */
    public static int CLSSV = 4;
    
}


