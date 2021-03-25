package org.example;

import org.javasim.*;
import org.javasim.streams.ExponentialStream;

public class Pay extends SimulationProcess {
    private ExponentialStream InterArrivalTime;
    public Customer customer;

    public Pay(Customer customer) {
        this.customer = customer;
    }

    public void run() {
        while (!terminated()) {
            try {
                hold(0.15);
                SimulatorCore.cashiers++;
                if (!SimulatorCore.cashierQueue.isEmpty()) {
                    SimulatorCore.cashierQueue.pop().activate();
                }
                new Leaving(this.customer).activate();
                this.terminate();
            } catch (SimulationException e) {
            } catch (RestartException e) {
            }
        }
    }
}
