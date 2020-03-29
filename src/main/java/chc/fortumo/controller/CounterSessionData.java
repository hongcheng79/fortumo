package chc.fortumo.controller;

/**
 * Contain the session object to keep track of the numerical plus operation
 */
public class CounterSessionData {
    // To hold the count
    private int counter = 0;

    /**
     * Plus operation
     * @param value
     */
    public synchronized void plus(int value) {
        counter = counter + value;
    }

    /**
     * To get the current count
     * @return counter
     */
    public synchronized int value() {
        return counter;
    }

    /**
     * Reset the counter
     */
    public synchronized void reset() {
        counter = 0;
    }
}
