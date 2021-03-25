package org.example;

import org.javasim.*;
import org.javasim.streams.ExponentialStream;

import java.util.LinkedList;

public class SimulatorCore extends SimulationProcess {
    public static int customerArrivals = 0;
    public static int tables = 100;
    public static int drinkWorkers = 3;
    public static LinkedList<SimulationProcess> drinkingQueue = new LinkedList<>();
    public static LinkedList<SimulationProcess> tableQueue = new LinkedList<>();
    public static LinkedList<SimulationProcess> pizzaQueue = new LinkedList<>();
    public static LinkedList<SimulationProcess> burgerQueue = new LinkedList<>();
    public static LinkedList<SimulationProcess> chineseQueue = new LinkedList<>();
    public static LinkedList<SimulationProcess> cashierQueue = new LinkedList<>();
    public static int cashiers = 3;
    public static int pizzaWorkers = 3;
    public static int burgerWorkers = 3;
    public static int chineseWorkers = 3;
    public ExponentialStream interArrivalTime = new ExponentialStream(1);
    public static int totalCustomersLeaved = 0;
    public static int totalNumberOfCustomers;

    public SimulatorCore() {
    }

    public static void reset() {
        totalCustomersLeaved = 0;
        customerArrivals = 0;
    }

    public void run() {
        try {
            Arrival a = new Arrival();
            a.activate();
            Simulation.start();
            while (totalCustomersLeaved < totalNumberOfCustomers) {
                hold(1);
            }
            a.interrupt();
            a.terminate();
            Simulation.stop();
            Simulation.reset();
            this.interrupt();
            SimulationProcess.mainResume();
        } catch (final Exception e) {
        }
    }

    public void await() {
        this.resumeProcess();
        SimulationProcess.mainSuspend();
    }

    public static int getBranch() {
        return (int) (Math.random() * (1 - 10)) + 10;
    }

    public static int getBigBranch() {
        return (int) (Math.random() * (1 - 100)) + 100;
    }
}
