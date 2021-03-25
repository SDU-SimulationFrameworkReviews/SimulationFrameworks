package org.example;

import org.javasim.*;
import org.javasim.streams.ExponentialStream;

public class Leaving extends SimulationProcess {
    private ExponentialStream InterArrivalTime;
    public Customer customer;

    public Leaving(Customer customer) {
        this.customer = customer;
    }

    public void run() {
        while (!terminated()) {
            try {
                hold(0.15);
                SimulatorCore.totalCustomersLeaved++;
                this.terminate();
            } catch (SimulationException e) {
            } catch (RestartException e) {
            }
        }
    }
}
