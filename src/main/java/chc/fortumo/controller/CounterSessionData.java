package chc.fortumo.controller;

public class CounterSessionData {
    private int counter = 0;

    public synchronized void plus(int value) {
        counter = counter + value;
    }

    public synchronized int value() {
        return counter;
    }

    public synchronized void reset() {
        counter = 0;
    }
}
