package org.example;

import org.javasim.RestartException;
import org.javasim.SimulationException;

public class main {
    public static void main(String[] args) throws SimulationException, RestartException {
        long startTime = System.currentTimeMillis();
        SimulatorCore simulation = new SimulatorCore();
        if (args.length == 0) {
            SimulatorCore.totalNumberOfCustomers = 1;
        } else {
            SimulatorCore.totalNumberOfCustomers = Integer.valueOf(args[0]);
        }
        simulation.await();
        SimulatorCore.reset();
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);
        System.exit(0);
    }
}
