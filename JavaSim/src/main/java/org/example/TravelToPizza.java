package org.example;

import org.javasim.*;
import org.javasim.streams.ExponentialStream;

public class TravelToPizza extends SimulationProcess {
    private ExponentialStream InterArrivalTime;
    public Customer customer;

    public TravelToPizza(Customer customer) {
        this.customer = customer;
    }

    public void run() {
        while (!terminated()) {
            try {
                hold(0.15);
                while (SimulatorCore.pizzaWorkers <= 0) {
                    SimulatorCore.pizzaQueue.add(this);
                    this.suspendProcess();
                }
                SimulatorCore.pizzaWorkers--;
                new RecievePizza(this.customer).activate();
                this.terminate();
            } catch (SimulationException e) {
            } catch (RestartException e) {
            }
        }
    }
}
