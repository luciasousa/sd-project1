package entities;

/**
 *    Definition of the internal states of the waiter during his life cycle.
 */
public class WaiterStates {
    /**
     *	The waiter is appraising situation.
     */
    public static int APPST = 0;

    /**
     *	The waiter is presenting the menu.
     */
    public static int PRSMN = 1;

    /**
     *	The waiter is taking the order.
     */
    public static int TKODR = 2;

    /**
     *	The waiter is placing the order.
     */
    public static int PCODR = 3;

    /**
     *	The waiter is waiting for portion.
     */
    public static int WTFPT = 4;

    /**
     *	The waiter is processing the bill.
     */
    public static int PRCBL = 5;

    /**
     *	The waiter is receiving payment.
     */
    public static int RECPM = 6;
}
