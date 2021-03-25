package org.example;

import org.javasim.*;
import org.javasim.streams.UniformStream;

import java.io.IOException;

public class RecievePizza extends SimulationProcess {
    private UniformStream pizzaMakingTime = new UniformStream(0.15, 0.45);
    public Customer customer;

    public RecievePizza(Customer customer) {
        this.customer = customer;
    }

    public void run() {
        while (!terminated()) {
            try {
                hold(pizzaMakingTime.getNumber());
                SimulatorCore.pizzaWorkers++;
                if (!SimulatorCore.pizzaQueue.isEmpty()) {
                    SimulatorCore.pizzaQueue.pop().activate();
                }
                new OrderAgainOrLeave(this.customer).activate();
                this.terminate();
            } catch (SimulationException e) {
            } catch (RestartException e) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
