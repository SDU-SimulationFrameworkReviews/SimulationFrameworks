package org.example;

import org.javasim.*;

public class TravelToBurger extends SimulationProcess {
    public Customer customer;

    public TravelToBurger(Customer customer) {
        this.customer = customer;
    }

    public void run() {
        while (!terminated()) {
            try {
                hold(0.15);
                while (SimulatorCore.burgerWorkers <= 0) {
                    SimulatorCore.burgerQueue.add(this);
                    this.suspendProcess();
                }
                SimulatorCore.burgerWorkers--;
                new RecieveBurger(this.customer).activate();
                this.terminate();
            } catch (SimulationException e) {
            } catch (RestartException e) {
            }
        }
    }
}
