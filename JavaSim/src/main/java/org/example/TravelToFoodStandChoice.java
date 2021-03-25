package org.example;

import org.javasim.*;
import org.javasim.streams.ExponentialStream;

public class TravelToFoodStandChoice extends SimulationProcess {
    private ExponentialStream InterArrivalTime;
    public Customer customer;

    public TravelToFoodStandChoice(Customer customer) {
        this.customer = customer;
    }

    public void run() {
        while (!terminated()) {
            try {
                hold(0.15);
                int branch = SimulatorCore.getBigBranch();
                if (branch < 34) {
                    new TravelToPizza(this.customer).activate();
                } else if (branch < 67) {
                    new TravelToChinese(this.customer).activate();
                } else {
                    new TravelToBurger(this.customer).activate();
                }
                this.terminate();
            } catch (SimulationException e) {
            } catch (RestartException e) {
            }
        }
    }
}
