package org.example;

import org.javasim.*;
import org.javasim.streams.UniformStream;

import java.io.IOException;

public class RecieveBurger extends SimulationProcess {
    private UniformStream burgerMakingTime = new UniformStream(0.15, 0.45);
    public Customer customer;

    public RecieveBurger(Customer customer) {
        this.customer = customer;
    }

    public void run() {
        while (!terminated()) {
            try {
                hold(burgerMakingTime.getNumber());
                SimulatorCore.burgerWorkers++;
                if (!SimulatorCore.burgerQueue.isEmpty()) {
                    SimulatorCore.burgerQueue.pop().activate();
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
