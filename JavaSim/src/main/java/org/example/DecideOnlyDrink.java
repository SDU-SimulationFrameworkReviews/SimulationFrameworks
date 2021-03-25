package org.example;

import org.javasim.*;
import org.javasim.streams.ExponentialStream;

public class DecideOnlyDrink extends SimulationProcess {
    private ExponentialStream InterArrivalTime;
    public Customer customer;
    public TravelTo to;

    public DecideOnlyDrink(Customer customer) {
        this.customer = customer;
    }

    public void run() {
        while (!terminated()) {
            try {
                hold(0.15);
                if (SimulatorCore.getBranch() < 6) {
                    new TravelToFoodStandChoice(this.customer).activate();
                } else {
                    new OrderAgainOrLeave(this.customer).activate();
                }
                this.terminate();
            } catch (SimulationException e) {
            } catch (RestartException e) {
            }
        }
    }
}
