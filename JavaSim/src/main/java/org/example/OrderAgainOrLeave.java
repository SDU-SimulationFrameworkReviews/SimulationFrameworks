package org.example;

import org.javasim.*;
import org.javasim.streams.UniformStream;

public class OrderAgainOrLeave extends SimulationProcess {
    private UniformStream burgerMakingTime = new UniformStream(0.15, 0.45);
    public Customer customer;

    public OrderAgainOrLeave(Customer customer) {
        this.customer = customer;
    }

    public void run() {
        while (!terminated()) {
            try {
                hold(0.15);
                int branch = SimulatorCore.getBranch();
                if (branch < 4) {
                    new TravelToFoodOrDrink(this.customer).activate();
                } else {
                    new LeaveTable(this.customer).activate();
                }
                this.terminate();
            } catch (SimulationException e) {
            } catch (RestartException e) {
            }
        }
    }
}
